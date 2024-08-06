package com.example.scientificcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private String currentInput = "";
    private final Calculator calculator = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        int[] buttonIds = {
                R.id.btnClear, R.id.btnBackspace, R.id.btnOpenParen, R.id.btnCloseParen,
                R.id.btnExp, R.id.btnLn, R.id.btnSqrt, R.id.btnPower, R.id.btnSin,
                R.id.btnCos, R.id.btnTan, R.id.btnLog, R.id.btnDivide, R.id.btnMultiply,
                R.id.btnSubtract, R.id.btnAdd, R.id.btnEquals, R.id.btnDot,
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5,
                R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(this::handleButtonClick);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void handleButtonClick(View view) {
        int buttonId = view.getId();
        if (buttonId == R.id.btnClear) {
            currentInput = "";
        } else if (buttonId == R.id.btnBackspace) {
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }
        } else if (buttonId == R.id.btnEquals) {
            try {
                currentInput = calculator.calculate(currentInput);
            } catch (Exception e) {
                currentInput = "Error";
            }
        } else {
            Button button = (Button) view;
            String buttonText = button.getText().toString();
            currentInput += buttonText;
        }
        tvResult.setText(currentInput);

        tvResult.setTextSize(48);
        if (currentInput.length() > 10) {
            tvResult.setTextSize(36);
        }
    }
}