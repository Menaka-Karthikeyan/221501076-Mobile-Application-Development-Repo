package com.example.ocr;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RoadSignOCR {

    private final Interpreter interpreter;
    private final int inputSize = 224;  // Assuming the model requires 224x224 image input

    // Constructor to load the model
    public RoadSignOCR(Context context) throws IOException {
        // Load the TFLite model
        interpreter = new Interpreter(loadModelFile(context), new Interpreter.Options());
    }

    // Load the model from the ml folder
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        // Get the model file from the ml folder
        File modelFile = new File(context.getFilesDir(), "ml/indian_road_sign_ocr.tflite");

        // Use FileInputStream to load the model file from the specified location
        try (FileInputStream inputStream = new FileInputStream(modelFile);
             FileChannel fileChannel = inputStream.getChannel()) {
            long startOffset = 0;
            long declaredLength = fileChannel.size();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    // Preprocess the image and prepare input for the TFLite model
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];
                float IMAGE_STD = 127.5f;
                float IMAGE_MEAN = 127.5f;
                byteBuffer.putFloat((((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat((((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                byteBuffer.putFloat(((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            }
        }
        return byteBuffer;
    }

    // Run the model and return the prediction
    public String classifyImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false);
        ByteBuffer input = convertBitmapToByteBuffer(resizedBitmap);

        float[][] output = new float[1][1];  // Assuming a single prediction output
        interpreter.run(input, output);

        // Map the output to a label (you may have a label file that lists road signs)
        return getLabel(output[0][0]);
    }

    // Map model output to a label (implement as needed based on your model)
    private String getLabel(float output) {
        // Replace this with the actual labels based on your model
        if (output > 0.5) {
            return "Stop Sign";
        } else {
            return "Speed Limit Sign";
        }
    }

    // Close the interpreter when done
    public void close() {
        interpreter.close();
    }
}
