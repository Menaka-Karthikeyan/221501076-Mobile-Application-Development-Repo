package com.example.speechtextconvertor;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TamilSpeechRecogniser {
    private Interpreter tflite;
    private static final String TAG = "TamilSpeechRecogniser";

    // Constructor to initialize the model
    public TamilSpeechRecogniser(Context context) {
        try {
            // Load the TensorFlow Lite model from the 'ml' folder
            tflite = new Interpreter(loadModelFile(context));
        } catch (Exception e) {
            // Handle exceptions that occur during model loading
            Log.e(TAG, "Error loading model: " + e.getMessage());
            tflite = null; // Gracefully handle issues by setting tflite to null
        }
    }

    // Load the model file from the 'ml' folder inside the src/main directory
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        // Get the path to the model in the 'ml' directory
        String modelPath = context.getFilesDir().getAbsolutePath() + "/ml/" + "stt_model.tflite";
        try (FileInputStream inputStream = new FileInputStream(modelPath);
             FileChannel fileChannel = inputStream.getChannel()) {
            long modelLength = fileChannel.size();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, modelLength);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load model from ml folder: " + e.getMessage());
            throw new IOException("Could not load model file", e);
        }
    }

    // Method for performing speech recognition
    public float[] recogniseSpeech(float[] inputAudio) {
        // Validate the input audio array
        if (inputAudio == null || inputAudio.length == 0) {
            throw new IllegalArgumentException("Input audio cannot be null or empty");
        }

        // Check if the TFLite interpreter is properly initialized
        if (tflite == null) {
            Log.e(TAG, "TFLite interpreter is not initialized.");
            return null;
        }

        // Prepare the output tensor (ensure it matches the model's expected output shape)
        float[][] output = new float[1][inputAudio.length]; // Adjust as needed based on model output
        tflite.run(inputAudio, output); // Perform inference

        return output[0]; // Return the inference result
    }

    // Close the TFLite interpreter and release resources
    public void close() {
        if (tflite != null) {
            tflite.close();
        }
    }
}
