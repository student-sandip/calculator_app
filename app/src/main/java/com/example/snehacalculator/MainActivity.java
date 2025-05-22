package com.example.snehacalculator;

import static com.example.snehacalculator.R.*;

import android.os.Vibrator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    MaterialButton button_c, button_lcm, button_hcf, button_comma, button_modulo, button_openbtackets, button_closebrackets;
    MaterialButton button_divide, button_multiply, button_subtraction, button_addition, button_equals;
    MaterialButton button_7, button_8, button_9, button_4, button_5, button_6, button_1, button_2, button_3, button_0;
    MaterialButton button_ac, button_dot;
    TextView text_result;
    Vibrator vibrator;

    private int lcm(int a, int b) {
        int gcd = hcf(a, b); // Calculate HCF first
        return (a * b) / gcd;
    }

    private int hcf(int a, int b) {
        if (b == 0) {
            return a;
        }
        return hcf(b, a % b);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(layout.activity_main);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Initialize TextView
        text_result = findViewById(id.text_result);

        // Initialize all MaterialButtons
        button_ac = findViewById(id.button_ac);
        button_dot = findViewById(id.button_dot);
        button_c = findViewById(id.button_c);
        button_lcm = findViewById(id.button_lcm);
        button_hcf = findViewById(id.button_hcf);
        button_divide = findViewById(id.button_divide);
        button_multiply = findViewById(id.button_multiply);
        button_subtraction = findViewById(id.button_subtraction);
        button_addition = findViewById(id.button_addition);
        button_equals = findViewById(id.button_equals);
        button_modulo = findViewById(id.button_modulo);
        button_comma = findViewById(id.button_comma);
        button_openbtackets = findViewById(id.button_openbtackets);
        button_closebrackets = findViewById(id.button_closebrackets);

        button_0 = findViewById(id.button_0);
        button_1 = findViewById(id.button_1);
        button_2 = findViewById(id.button_2);
        button_3 = findViewById(id.button_3);
        button_4 = findViewById(id.button_4);
        button_5 = findViewById(id.button_5);
        button_6 = findViewById(id.button_6);
        button_7 = findViewById(id.button_7);
        button_8 = findViewById(id.button_8);
        button_9 = findViewById(id.button_9);

        // Set OnClickListener for AC button
        button_ac.setOnClickListener(view -> {
            text_result.setText("0");
            vibrateDevice();
        });

        // Set OnClickListener for numeric buttons
        ArrayList<MaterialButton> nums = new ArrayList<>();
        nums.add(button_0);
        nums.add(button_1);
        nums.add(button_2);
        nums.add(button_3);
        nums.add(button_4);
        nums.add(button_5);
        nums.add(button_6);
        nums.add(button_7);
        nums.add(button_8);
        nums.add(button_9);

        for (MaterialButton b : nums) {
            b.setOnClickListener(view -> {
                if (text_result.getText().toString().equals("0") || text_result.getText().toString().equals("Error")) {
                    text_result.setText(b.getText().toString());
                } else {
                    text_result.append(b.getText().toString());
                }
                vibrateDevice();
            });
        }

        // Set OnClickListener for operator buttons (excluding LCM/HCF which need special handling)
        ArrayList<MaterialButton> operators = new ArrayList<>();
        operators.add(button_addition);
        operators.add(button_subtraction);
        operators.add(button_multiply);
        operators.add(button_divide);
        operators.add(button_modulo);

        for (MaterialButton b : operators) {
            b.setOnClickListener(view -> {
                text_result.append(b.getText().toString());
                vibrateDevice();
            });
        }

        // Set OnClickListener for C (Clear last character) button
        button_c.setOnClickListener(view -> {
            String s = text_result.getText().toString();
            if (s.length() > 0 && !s.equals("0")) {
                s = s.substring(0, s.length() - 1);
                if (s.isEmpty()) {
                    text_result.setText("0");
                } else {
                    text_result.setText(s);
                }
            } else if (s.equals("Error")) {
                text_result.setText("0");
            }
            vibrateDevice();
        });

        // Set OnClickListener for dot button
        button_dot.setOnClickListener(view -> {
            String currentText = text_result.getText().toString();
            // Prevent multiple dots in a single number
            Pattern pattern = Pattern.compile("[+\\-*/%]");
            String[] parts = pattern.split(currentText);
            String lastPart = parts[parts.length - 1];

            if (!lastPart.contains(".")) {
                text_result.append(".");
            }
            vibrateDevice();
        });

        // Set OnClickListener for comma button (primarily for HCF/LCM input)
        button_comma.setOnClickListener(view -> {
            text_result.append(",");
            vibrateDevice();
        });

        // Set OnClickListener for open bracket button
        button_openbtackets.setOnClickListener(view -> {
            text_result.append("(");
            vibrateDevice();
        });

        // Set OnClickListener for close bracket button
        button_closebrackets.setOnClickListener(view -> {
            text_result.append(")");
            vibrateDevice();
        });

        // Set OnClickListener for LCM button
        button_lcm.setOnClickListener(view -> {
            try {
                String expression = text_result.getText().toString();
                String[] numbers = expression.split(",");
                if (numbers.length == 2) {
                    int num1 = Integer.parseInt(numbers[0].trim());
                    int num2 = Integer.parseInt(numbers[1].trim());
                    int result = lcm(num1, num2);
                    text_result.setText(String.valueOf(result));
                } else {
                    text_result.setText("Error: Invalid input for LCM (e.g., 10,15)");
                }
            } catch (NumberFormatException e) {
                text_result.setText("Error: Invalid number format for LCM");
            }
            vibrateDevice();
        });

        // Set OnClickListener for HCF button
        button_hcf.setOnClickListener(view -> {
            try {
                String expression = text_result.getText().toString();
                String[] numbers = expression.split(",");
                if (numbers.length == 2) {
                    int num1 = Integer.parseInt(numbers[0].trim());
                    int num2 = Integer.parseInt(numbers[1].trim());
                    int result = hcf(num1, num2);
                    text_result.setText(String.valueOf(result));
                } else {
                    text_result.setText("Error: Invalid input for HCF (e.g., 10,15)");
                }
            } catch (NumberFormatException e) {
                text_result.setText("Error: Invalid number format for HCF");
            }
            vibrateDevice();
        });

        // Set OnClickListener for Equals button
        button_equals.setOnClickListener(view -> {
            vibrateDevice();
            try {
                String expression = text_result.getText().toString();
                if (expression.isEmpty()) {
                    text_result.setText("0");
                    return;
                }

                double result = evaluateExpression(expression);

                if (String.valueOf(result).endsWith(".0")) {
                    text_result.setText(String.valueOf((long) result));
                } else {
                    text_result.setText(String.valueOf(result));
                }

            } catch (Exception e) {
                text_result.setText("Error");
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void vibrateDevice() {
        if (vibrator != null) {
            vibrator.vibrate(80); // Vibrate for 80 milliseconds
        }
    }

    // A simple expression evaluator (handles +, -, *, /, %)
    private double evaluateExpression(String expression) throws Exception {
        // Remove spaces for easier parsing
        expression = expression.replaceAll(" ", "");

        // Regular expression to split numbers and operators, keeping operators
        Pattern pattern = Pattern.compile("(?<=[0-9.])(?=[+\\-*/%])|(?<=[+\\-*/%])(?=[0-9.])");
        String[] tokens = pattern.split(expression);

        if (tokens.length == 0) {
            throw new IllegalArgumentException("Empty expression");
        }

        // Use ArrayLists for mutable lists of numbers and operators
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();

        // Populate numbers and operators lists
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            try {
                numbers.add(Double.parseDouble(token));
            } catch (NumberFormatException e) {
                operators.add(token);
            }
        }

        // Perform multiplication and division first
        for (int i = 0; i < operators.size(); i++) {
            String op = operators.get(i);
            if (op.equals("*") || op.equals("/") || op.equals("%")) {
                double num1 = numbers.get(i);
                double num2 = numbers.get(i + 1);
                double result = 0;

                switch (op) {
                    case "*":
                        result = num1 * num2;
                        break;
                    case "/":
                        if (num2 == 0) throw new ArithmeticException("Division by zero");
                        result = num1 / num2;
                        break;
                    case "%":
                        result = num1 % num2;
                        break;
                }
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; // Adjust index after removal
            }
        }

        // Perform addition and subtraction
        double finalResult = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            String op = operators.get(i);
            double num = numbers.get(i + 1);

            switch (op) {
                case "+":
                    finalResult += num;
                    break;
                case "-":
                    finalResult -= num;
                    break;
            }
        }
        return finalResult;
    }
}