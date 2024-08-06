package com.example.textmodifier;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        Button changeButton = findViewById(R.id.changeButton);

        changeButton.setOnClickListener(v -> {
            // Change text color
            textView.setTextColor(Color.RED);

            // Change font style
            Typeface customFont = getResources().getFont(R.font.dancing_script);
            textView.setTypeface(customFont, Typeface.BOLD);

            // Display a Toast message
            Toast.makeText(MainActivity.this, "Text style changed!", Toast.LENGTH_SHORT).show();
        });
    }
}