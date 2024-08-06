package com.example.scientificcalculator;

import android.util.Log;

import java.util.Stack;

public class Calculator {

    private static final String TAG = "Calculator";

    public String calculate(String expression) {
        try {
            double result = evaluate(expression);
            return String.valueOf(result);
        } catch (Exception e) {
            Log.e(TAG, "Calculation error", e);
            return "Error";
        }
    }

    private double evaluate(String expression) {
        char[] tokens = expression.toCharArray();

        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ') {
                continue;
            }

            if (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.') {
                StringBuilder sbuf = new StringBuilder();
                while (i < tokens.length && (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sbuf.toString()));
                i--;
            } else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '^') {
                while (!operators.empty() && hasPrecedence(tokens[i], operators.peek())) {
                    values.push(applyOp(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(tokens[i]);
            } else if (Character.isLetter(tokens[i])) {
                StringBuilder sbuf = new StringBuilder();
                while (i < tokens.length && Character.isLetter(tokens[i])) {
                    sbuf.append(tokens[i++]);
                }
                i--;

                String function = sbuf.toString();
                if (isFunction(function)) {
                    int j = i + 1;
                    if (tokens[j] == '(') {
                        int count = 0;
                        while (j < tokens.length) {
                            if (tokens[j] == '(') count++;
                            if (tokens[j] == ')') count--;
                            if (count == 0) break;
                            j++;
                        }
                    }

                    double value = evaluate(expression.substring(i + 2, j));
                    values.push(applyFunction(function, value));
                    i = j;
                }
            }
        }

        while (!operators.empty()) {
            values.push(applyOp(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return op1 != '^' || (op2 != '*' && op2 != '/' && op2 != '+' && op2 != '-');
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    Log.e(TAG, "Cannot divide by zero");
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
            case '^':
                return Math.pow(a, b);
        }
        return 0;
    }

    private boolean isFunction(String function) {
        return function.equals("sin") || function.equals("cos") || function.equals("tan") ||
                function.equals("log") || function.equals("ln") || function.equals("sqrt");
    }

    private double applyFunction(String function, double value) {
        switch (function) {
            case "sin":
                return Math.sin(Math.toRadians(value));
            case "cos":
                return Math.cos(Math.toRadians(value));
            case "tan":
                return Math.tan(Math.toRadians(value));
            case "log":
                return Math.log10(value);
            case "ln":
                return Math.log(value);
            case "sqrt":
                return Math.sqrt(value);
            default:
                Log.e(TAG, "Unknown function: " + function);
                throw new UnsupportedOperationException("Unknown function: " + function);
        }
    }
}