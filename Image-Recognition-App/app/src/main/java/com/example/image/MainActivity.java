package com.example.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Interpreter tflite;
    private List<String> labels;
    private ImageView imageView;

    // Create ActivityResultLauncher for image capture
    private final ActivityResultLauncher<Intent> imageCaptureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Get the bitmap from the camera intent
                    Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
                    imageView.setImageBitmap(imageBitmap);
                    classifyImage(imageBitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        // Load the TFLite model and labels from the ml directory
        loadModel();
        loadLabels();

        // Start camera intent to capture an image
        dispatchTakePictureIntent();
    }

    private void loadModel() {
        try {
            // Load the model from the ml directory
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(this, "ml/model.tflite");
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(tfliteModel, options);
        } catch (IOException e) {
            Log.e(TAG, "Error loading model", e);
        }
    }

    private void loadLabels() {
        try {
            // Load labels from the ml directory
            labels = FileUtil.loadLabels(this, "ml/labels.txt");
        } catch (IOException e) {
            Log.e(TAG, "Error loading labels", e);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imageCaptureLauncher.launch(takePictureIntent);  // Use the ActivityResultLauncher to handle the result
        }
    }

    private float[][][][] preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        float[][][][] input = new float[1][224][224][3];

        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int pixel = resizedBitmap.getPixel(x, y);
                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.0f;  // R
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.0f;   // G
                input[0][y][x][2] = (pixel & 0xFF) / 255.0f;          // B
            }
        }
        return input;
    }

    private void classifyImage(Bitmap bitmap) {
        float[][][][] input = preprocessImage(bitmap);
        float[][] output = new float[1][1001];  // Assuming MobileNet has 1001 classes

        tflite.run(input, output);

        int topClassIndex = -1;
        float maxProbability = 0.0f;
        for (int i = 0; i < output[0].length; i++) {
            if (output[0][i] > maxProbability) {
                maxProbability = output[0][i];
                topClassIndex = i;
            }
        }

        String label = getLabel(topClassIndex);
        Toast.makeText(this, "Prediction: " + label + " (Confidence: " + maxProbability + ")", Toast.LENGTH_LONG).show();
    }

    private String getLabel(int index) {
        if (labels != null && index >= 0 && index < labels.size()) {
            return labels.get(index);
        }
        return "Unknown";
    }
}
