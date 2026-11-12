package com.example.smartcalculator;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private TextView tvExpression, tvResult;

    private StringBuilder current = new StringBuilder();
    private double firstValue = 0;
    private String operator = null;
    private boolean justCalculated = false;
    private String expression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        attachListeners(findViewById(R.id.keypad));

        findViewById(R.id.btnTheme).setOnClickListener(v -> {
            boolean dark = AppCompatDelegate.getDefaultNightMode()
                    == AppCompatDelegate.MODE_NIGHT_YES;
            AppCompatDelegate.setDefaultNightMode(dark
                    ? AppCompatDelegate.MODE_NIGHT_NO
                    : AppCompatDelegate.MODE_NIGHT_YES);
        });

        if (savedInstanceState != null) {
            current = new StringBuilder(savedInstanceState.getString("current", ""));
            firstValue = savedInstanceState.getDouble("first", 0);
            operator = savedInstanceState.getString("op", null);
            justCalculated = savedInstanceState.getBoolean("calc", false);
            expression = savedInstanceState.getString("expr", "");
        }
        updateDisplay();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("current", current.toString());
        out.putDouble("first", firstValue);
        out.putString("op", operator);
        out.putBoolean("calc", justCalculated);
        out.putString("expr", expression);
    }

    private void attachListeners(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup) {
                attachListeners((ViewGroup) child);
            } else if (child instanceof MaterialButton) {
                child.setOnClickListener(v ->
                        handleInput(((MaterialButton) v).getText().toString()));
            }
        }
    }

    private void handleInput(String t) {
        switch (t) {
            case "C":  clearAll(); break;
            case "⌫":  backspace(); break;
            case "%":  percent(); break;
            case "+": case "−": case "×": case "÷": setOperator(t); break;
            case "=":  calculateResult(); break;
            case ".":  appendDot(); break;
            default:   appendDigit(t);
        }
        updateDisplay();
    }

    private void appendDigit(String d) {
        if (justCalculated) {
            current.setLength(0);
            expression = "";
            justCalculated = false;
        }
        if (current.length() >= 15) return;
        if (current.toString().equals("0")) current.setLength(0);
        current.append(d);
    }

    private void appendDot() {
        if (justCalculated) {
            current.setLength(0);
            expression = "";
            justCalculated = false;
        }
        if (current.indexOf(".") != -1) return;
        if (current.length() == 0) current.append("0");
        current.append(".");
    }

    private void setOperator(String op) {
        if (current.length() > 0) {
            double value = Double.parseDouble(current.toString());
            if (operator != null && !justCalculated) {
                if (operator.equals("÷") && value == 0) { divideByZero(); return; }
                firstValue = compute(firstValue, value, operator);
            } else {
                firstValue = value;
            }
        } else if (operator == null && !justCalculated) {
            firstValue = 0;
        }
        operator = op;
        current.setLength(0);
        justCalculated = false;
        expression = "";
    }

    private void calculateResult() {
        if (operator == null || current.length() == 0) return;
        double second = Double.parseDouble(current.toString());
        if (operator.equals("÷") && second == 0) { divideByZero(); return; }

        double result = compute(firstValue, second, operator);
        expression = format(firstValue) + " " + operator + " " + format(second) + " =";
        current = new StringBuilder(format(result));
        operator = null;
        justCalculated = true;
    }

    private void percent() {
        if (current.length() == 0) return;
        double value = Double.parseDouble(current.toString());
        value = (operator != null) ? firstValue * value / 100 : value / 100;
        current = new StringBuilder(format(value));
    }

    private void backspace() {
        if (justCalculated || current.length() == 0) return;
        current.deleteCharAt(current.length() - 1);
    }

    private void clearAll() {
        current.setLength(0);
        firstValue = 0;
        operator = null;
        justCalculated = false;
        expression = "";
    }

    private void divideByZero() {
        clearAll();
        expression = "Cannot divide by zero";
    }

    private double compute(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "−": return a - b;
            case "×": return a * b;
            default:  return a / b;
        }
    }

    private String format(double v) {
        BigDecimal bd = new BigDecimal(v).setScale(10, RoundingMode.HALF_UP);
        String s = bd.stripTrailingZeros().toPlainString();
        return s.equals("-0") ? "0" : s;
    }

    private void updateDisplay() {
        if (operator != null) {
            tvExpression.setText(format(firstValue) + " " + operator);
        } else {
            tvExpression.setText(expression);
        }
        tvResult.setText(current.length() == 0 ? "0" : current.toString());
    }
}