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
import java.util.Stack;
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
        if (a == 0 || b == 0) return 0;
        int gcd = hcf(a, b);
        return Math.abs((a * b) / gcd);
    }

    private int hcf(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
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
                String currentText = text_result.getText().toString();
                // Prevent adding operator if it's the last character and already an operator
                if (!currentText.isEmpty() && isOperator(currentText.charAt(currentText.length() - 1))) {
                    // Replace the last operator with the new one
                    text_result.setText(currentText.substring(0, currentText.length() - 1) + b.getText().toString());
                } else {
                    text_result.append(b.getText().toString());
                }
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
            // Check if the last number segment already contains a dot
            int lastOperatorIndex = Math.max(Math.max(currentText.lastIndexOf('+'), currentText.lastIndexOf('-')),
                    Math.max(currentText.lastIndexOf('*'), Math.max(currentText.lastIndexOf('/'), currentText.lastIndexOf('%'))));
            int lastBracketIndex = Math.max(currentText.lastIndexOf('('), currentText.lastIndexOf(')'));

            // Get the segment of the number currently being typed
            String currentNumberSegment;
            if (lastOperatorIndex > lastBracketIndex) { // Last token was an operator
                currentNumberSegment = currentText.substring(lastOperatorIndex + 1);
            } else if (lastBracketIndex > lastOperatorIndex && currentText.charAt(lastBracketIndex) == '(') { // Last token was an open bracket
                currentNumberSegment = currentText.substring(lastBracketIndex + 1);
            } else { // No operator or bracket, or last was a closing bracket
                currentNumberSegment = currentText;
            }


            if (!currentNumberSegment.contains(".")) {
                if (currentNumberSegment.isEmpty() || isOperator(currentNumberSegment.charAt(currentNumberSegment.length() - 1))) {
                    text_result.append("0."); // Start with "0." if adding dot at the beginning of a number
                } else {
                    text_result.append(".");
                }
            }
            vibrateDevice();
        });

        // Set OnClickListener for comma button (primarily for HCF/LCM input)
        button_comma.setOnClickListener(view -> {
            // Only allow comma if it's not already present in the current input for HCF/LCM
            String currentText = text_result.getText().toString();
            if (!currentText.contains(",")) {
                text_result.append(",");
            }
            vibrateDevice();
        });

        // Set OnClickListener for open bracket button
        button_openbtackets.setOnClickListener(view -> {
            String currentText = text_result.getText().toString();
            if (!currentText.equals("0") && !currentText.isEmpty()) {
                char lastChar = currentText.charAt(currentText.length() - 1);
                if (Character.isDigit(lastChar) || lastChar == '.') {
                    text_result.setText("Error: Misplaced parenthesis");
                    vibrateDevice();
                    return;
                }
            }
            text_result.append("(");
            vibrateDevice();
        });

        // Set OnClickListener for close bracket button
        button_closebrackets.setOnClickListener(view -> {
            String currentText = text_result.getText().toString();
            int openBrackets = countChar(currentText, '(');
            int closeBrackets = countChar(currentText, ')');

            if (openBrackets > closeBrackets) {
                if (!currentText.isEmpty()) {
                    char lastChar = currentText.charAt(currentText.length() - 1);
                    if (isOperator(lastChar) || lastChar == '(') {
                        text_result.setText("Error: Misplaced parenthesis");
                        vibrateDevice();
                        return;
                    }
                }
                text_result.append(")");
            } else {
                text_result.setText("Error: Unmatched parenthesis");
            }
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
                    text_result.setText("Error: LCM needs 2 numbers (e.g., 10,15)");
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
                    text_result.setText("Error: HCF needs 2 numbers (e.g., 10,15)");
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
                if (expression.isEmpty() || expression.equals("Error")) {
                    text_result.setText("0");
                    return;
                }

                // Basic validation for unmatched parentheses before evaluation
                if (countChar(expression, '(') != countChar(expression, ')')) {
                    text_result.setText("Error: Unmatched parenthesis");
                    return;
                }

                double result = evaluateExpression(expression);

                if (Double.isInfinite(result) || Double.isNaN(result)) {
                    text_result.setText("Error");
                } else if (String.valueOf(result).endsWith(".0")) {
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

    // Helper to check if a character is an operator
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    // Helper to count character occurrences
    private int countChar(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    // New and improved evaluateExpression method to handle parentheses and proper operator precedence
    private double evaluateExpression(String expression) throws Exception {
        // Remove spaces for easier parsing
        expression = expression.replaceAll(" ", "");

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                i--; // Decrement i because it will be incremented in the for loop
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    performOperation(numbers, operators);
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // Pop the '('
                } else {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
            } else if (isOperator(c)) {
                // Handle negative numbers at the start or after an open parenthesis
                if (c == '-' && (i == 0 || expression.charAt(i - 1) == '(')) {
                    // Check if the next character is a digit
                    if (i + 1 < expression.length() && Character.isDigit(expression.charAt(i + 1))) {
                        StringBuilder sb = new StringBuilder("-");
                        i++; // Move past the '-'
                        while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                            sb.append(expression.charAt(i));
                            i++;
                        }
                        i--; // Adjust i for the outer loop
                        numbers.push(Double.parseDouble(sb.toString()));
                        continue; // Continue to next iteration of for loop
                    }
                }

                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    performOperation(numbers, operators);
                }
                operators.push(c);
            } else {
                throw new IllegalArgumentException("Invalid character in expression: " + c);
            }
        }

        while (!operators.isEmpty()) {
            if (operators.peek() == '(') {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            performOperation(numbers, operators);
        }

        if (numbers.size() != 1 || !operators.isEmpty()) {
            throw new IllegalArgumentException("Invalid expression format");
        }

        return numbers.pop();
    }

    // Determines operator precedence
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        // Multiplication, Division, Modulo have higher precedence
        if ((op1 == '*' || op1 == '/' || op1 == '%') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true; // op1 has lower or equal precedence, or same precedence (e.g., + and -)
    }

    // Performs the operation based on the operator popped from the stack
    private void performOperation(Stack<Double> numbers, Stack<Character> operators) {
        if (numbers.size() < 2 || operators.isEmpty()) {
            throw new IllegalArgumentException("Invalid expression");
        }
        char operator = operators.pop();
        double b = numbers.pop();
        double a = numbers.pop();
        double result;

        switch (operator) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                result = a / b;
                break;
            case '%':
                if (b == 0) throw new ArithmeticException("Modulo by zero");
                result = a % b;
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
        numbers.push(result);
    }
}