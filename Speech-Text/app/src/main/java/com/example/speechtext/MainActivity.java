package com.example.speechtext;

import static android.Manifest.permission.*;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Arrays;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private TamilSpeechRecogniser tamilSpeechRecogniser;

    private TextView outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views by ID
        Button btnSpeechToText = findViewById(R.id.btnSpeechToText);
        outputText = findViewById(R.id.outputText);

        // Request permission to record audio
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            try {
                initRecogniser();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Set the button click listener
        btnSpeechToText.setOnClickListener(v -> startSpeechRecognition());
    }

    private void initRecogniser() throws IOException {
        // Initialize the speech recognizer with the model
        tamilSpeechRecogniser = new TamilSpeechRecogniser(this);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    initRecogniser();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Permission denied
                Log.e("TFLite", "Permission to record audio was denied");
            }
        }
    }

    private void startSpeechRecognition() {
        if (tamilSpeechRecogniser != null) {
            float[] audioInput = getAudioInput();

            if (audioInput != null) {
                transcribeAudio(audioInput);
            } else {
                Log.e("TFLite", "Error getting audio input");
            }
        }
    }

    private void transcribeAudio(float[] audioInput) {
        if (tamilSpeechRecogniser != null) {
            float[] output = tamilSpeechRecogniser.recogniseSpeech(audioInput);
            // Display the output in the TextView
            String transcription = Arrays.toString(output);
            outputText.setText(transcription);
            Log.d("Transcription", "Transcription result: " + transcription);
        }
    }

    private float[] getAudioInput() {
        // Sample rate in Hz (must match the model requirement)
        int sampleRate = 16000;
        // Configure the audio format
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        // Buffer size required for recording
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e("AudioRecord", "Invalid buffer size");
            return null;
        }

        // Check for permission
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            return null;
        }

        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
        );

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioRecord", "AudioRecord initialization failed");
            return null;
        }

        short[] audioBuffer = new short[bufferSize];
        float[] audioInput = new float[bufferSize];

        try {
            // Start recording
            audioRecord.startRecording();

            // Read audio data from the microphone
            int numRead = audioRecord.read(audioBuffer, 0, bufferSize);
            if (numRead > 0) {
                // Convert short[] audio data to float[] format
                for (int i = 0; i < numRead; i++) {
                    audioInput[i] = audioBuffer[i] / 32768.0f; // Normalize the audio data
                }
            }

        } catch (Exception e) {
            Log.e("AudioRecord", "Error reading audio data", e);
            return null;
        } finally {
            // Stop and release the AudioRecord instance
            audioRecord.stop();
            audioRecord.release();
        }

        return audioInput;
    }
}
