package com.example.loginvalidation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText usernameEditText;
    private EditText idEditText;
    private static final HashMap<String, String> userDatabase = new HashMap<>(); // In-memory user storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetter(source.charAt(i))) {
                            return "";
                        }
                    }
                    return null;
                }
        });

        idEditText = findViewById(R.id.idEditText);
        Button registerButton = findViewById(R.id.registerButton);
        Button validateButton = findViewById(R.id.validateButton);

        if (usernameEditText == null || idEditText == null || registerButton == null || validateButton == null) {
            Log.e(TAG, "One or more UI elements not initialized properly.");
        }

        assert registerButton != null;
        registerButton.setOnClickListener(v -> {
            Log.d(TAG, "Register button clicked");
            registerUser();
        });

        assert validateButton != null;
        validateButton.setOnClickListener(v -> {
            Log.d(TAG, "Validate button clicked");
            loginUser();
        });

        TextView copyrightTextView = findViewById(R.id.copyrightTextView);
        if (copyrightTextView != null) {
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            @SuppressLint("DefaultLocale") String copyrightText = String.format("© %d Menaka", currentYear);
            copyrightTextView.setText(copyrightText);
        } else {
            Log.e(TAG, "Copyright TextView is null");
        }
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String id = idEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "ID cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!id.matches("[a-zA-Z0-9]{8}")) {
            Toast.makeText(this, "ID must be 8 alphanumeric characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDatabase.containsKey(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        userDatabase.put(username, id);
        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "User registered: " + username + " with ID: " + id);
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String id = idEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "ID cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDatabase.containsKey(username)) {
            String registeredId = Objects.requireNonNull(userDatabase.get(username));
            if (registeredId.equals(id)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wrong ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid username or ID", Toast.LENGTH_SHORT).show();
        }
    }
}