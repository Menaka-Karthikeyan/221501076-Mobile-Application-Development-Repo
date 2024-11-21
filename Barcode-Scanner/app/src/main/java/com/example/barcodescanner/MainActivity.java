package com.example.barcodescanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private Interpreter tflite;
    private TextView resultTextView;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        scanButton = findViewById(R.id.scanButton);

        // Load the TensorFlow Lite model
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading model", Toast.LENGTH_SHORT).show();
        }

        // Check for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

        scanButton.setOnClickListener(v -> {
            // Trigger barcode scan logic here (if needed for custom scanning)
            Toast.makeText(MainActivity.this, "Scan initiated!", Toast.LENGTH_SHORT).show();
        });
    }

    // Start camera preview with CameraX
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the camera preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(((PreviewView) findViewById(R.id.cameraPreview)).getSurfaceProvider());

                // Set up the image analysis
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::analyzeImage);

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (Exception e) {
                Log.e("CameraX", "Binding use cases failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Analyze the image and run the model
    private void analyzeImage(ImageProxy imageProxy) {
        // Convert the ImageProxy to Bitmap
        Bitmap bitmap = convertImageProxyToBitmap(imageProxy);

        // Preprocess the image to the model's input size (example: 224x224)
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false);

        // Run model on the image
        float[][] output = new float[1][100]; // Adjust output size based on your model
        TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
        inputBuffer.loadBuffer(bitmapToByteBuffer(scaledBitmap));

        // Run the model and get results
        tflite.run(inputBuffer.getBuffer(), output);

        // Process the output and decode the barcode
        String barcodeData = decodeOutput(output);
        resultTextView.setText("Scanned Barcode: " + barcodeData);

        // Close image proxy
        imageProxy.close();
    }

    // Convert ImageProxy to Bitmap
    private Bitmap convertImageProxyToBitmap(ImageProxy imageProxy) {
        // Code to convert ImageProxy to Bitmap (you may need to adjust based on your setup)
        // You can use ImageAnalysis.Analyzer to get the image buffer.
        return Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
    }

    // Load the TensorFlow Lite model from assets
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("barcode_model.tflite");
        FileInputStream inputStream = fileDescriptor.createInputStream();
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Convert Bitmap to byte buffer for the model input
    private ByteBuffer bitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        buffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[224 * 224];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixelValue : intValues) {
            buffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f); // Red
            buffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f); // Green
            buffer.putFloat((pixelValue & 0xFF) / 255.0f); // Blue
        }

        return buffer;
    }

    // Decode output from model
    private String decodeOutput(float[][] output) {
        // Implement your decoding logic here
        // If you trained your model to predict a barcode label, use the output to find the barcode data.
        return "Decoded Barcode Data";
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
