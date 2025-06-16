package com.example.calculator;

import static com.example.calculator.R.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

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

        EditText editText = findViewById(R.id.text_result);
        HorizontalScrollView scrollView = findViewById(R.id.horizontalScrollView);

// Scroll to right when text is updated
        editText.setText("0");
        editText.setSelection(editText.getText().length());

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_RIGHT));

//        EditText editText = findViewById(R.id.text_result);

// Disable keyboard (soft input)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(false);
        } else {
            // For older devices
            try {
                Method method = EditText.class.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        text_result = findViewById(id.text_result);

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        button_ac.setOnClickListener(v -> {
            text_result.setText("0");
            vibrateDevice();
        });


        ArrayList<MaterialButton> nums = new ArrayList<>();
        nums.add(button_0); nums.add(button_1); nums.add(button_2);
        nums.add(button_3); nums.add(button_4); nums.add(button_5);
        nums.add(button_6); nums.add(button_7); nums.add(button_8); nums.add(button_9);
        for (MaterialButton b : nums) {
            b.setOnClickListener(v -> {
                String current = text_result.getText().toString();
                if (current.equals("0") || current.equals("Error")) {
                    text_result.setText(b.getText().toString());
                } else {
                    text_result.append(b.getText().toString());
                }
                vibrateDevice();
            });
        }

        // Operators
        ArrayList<MaterialButton> ops = new ArrayList<>();
        ops.add(button_addition); ops.add(button_subtraction);
        ops.add(button_multiply); ops.add(button_divide); ops.add(button_modulo);
        for (MaterialButton b : ops) {
            b.setOnClickListener(v -> {
                String current = text_result.getText().toString();
                char last = current.charAt(current.length() - 1);
                if (!current.isEmpty() && isOperator(last)) {
                    text_result.setText(current.substring(0, current.length() - 1) + b.getText().toString());
                } else if (current.equals("0") && b.getText().toString().equals("-")) {
                    text_result.setText("-");
                } else {
                    text_result.append(b.getText().toString());
                }
                vibrateDevice();
            });
        }

        button_dot.setOnClickListener(v -> {
            String current = text_result.getText().toString();
            int lastOp = -1;
            for (int i = current.length() - 1; i >= 0; i--) {
                if (isOperator(current.charAt(i)) || current.charAt(i) == '(' || current.charAt(i) == ')') {
                    lastOp = i;
                    break;
                }
            }
            String segment = (lastOp != -1) ? current.substring(lastOp + 1) : current;
            if (!segment.contains(".")) {
                if (segment.isEmpty() || isOperator(current.charAt(current.length() - 1))) {
                    text_result.append("0.");
                } else {
                    text_result.append(".");
                }
            }
            vibrateDevice();
        });


        button_c.setOnClickListener(v -> {
            String s = text_result.getText().toString();
            if (!s.equals("0") && !s.equals("Error")) {
                s = s.substring(0, s.length() - 1);
                text_result.setText((s.isEmpty() || s.equals("-")) ? "0" : s);
            } else {
                text_result.setText("0");
            }
            vibrateDevice();
        });


        button_openbtackets.setOnClickListener(v -> {
            String t = text_result.getText().toString();
            if (t.equals("0") || t.equals("Error")) {
                text_result.setText("(");
            } else {
                char last = t.charAt(t.length() - 1);
                if (Character.isDigit(last) || last == '.') {
                    text_result.setText("Error: Misplaced parenthesis");
                    vibrateDevice();
                    return;
                }
                text_result.append("(");
            }
            vibrateDevice();
        });

        button_closebrackets.setOnClickListener(v -> {
            String t = text_result.getText().toString();
            int open = countChar(t, '(');
            int close = countChar(t, ')');
            if (open > close) {
                char last = t.charAt(t.length() - 1);
                if (isOperator(last) || last == '(') {
                    text_result.setText("Error: Misplaced parenthesis");
                } else {
                    text_result.append(")");
                }
            } else {
                text_result.setText("Error: Unmatched parenthesis");
            }
            vibrateDevice();
        });


        button_comma.setOnClickListener(v -> {
            String t = text_result.getText().toString();
            char lastChar = t.charAt(t.length() - 1);

            if (t.equals("0") || t.equals("Error")) {
                text_result.setText("0,");
            } else if (lastChar != ',') {
                text_result.append(",");
            }

            vibrateDevice();
        });



        button_lcm.setOnClickListener(v -> handleLcmHcf(true));
        button_hcf.setOnClickListener(v -> handleLcmHcf(false));

        button_equals.setOnClickListener(v -> {
            vibrateDevice();
            try {
                String exp = text_result.getText().toString();
                if (exp.isEmpty() || exp.equals("Error") || exp.equals("0")) {
                    text_result.setText("0");
                    return;
                }
                if (countChar(exp, '(') != countChar(exp, ')')) {
                    text_result.setText("Error: Unmatched parenthesis");
                    return;
                }
                char last = exp.charAt(exp.length() - 1);
                if (isOperator(last) || last == '(') {
                    text_result.setText("Error: Incomplete expression");
                    return;
                }
                double res = evaluateExpression(exp);
                text_result.setText((res % 1 == 0) ? String.valueOf((long) res) : String.valueOf(res));
            } catch (Exception e) {
                text_result.setText("Error");
            }
        });
    }

    private void handleLcmHcf(boolean isLcm) {
        try {
            String input = text_result.getText().toString().trim();

            if (input.isEmpty() || input.equals("0") || input.equals("Error")) {
                text_result.setText("Error: Provide at least two numbers");
                vibrateDevice();
                return;
            }

            String[] parts = input.split(",");

            if (parts.length < 2) {
                text_result.setText("Error: Needs 2 or more numbers (e.g., 12,18)");
                vibrateDevice();
                return;
            }

            int result = Integer.parseInt(parts[0].trim());

            for (int i = 1; i < parts.length; i++) {
                int num = Integer.parseInt(parts[i].trim());
                result = isLcm ? lcm(result, num) : hcf(result, num);
            }

            text_result.setText(String.valueOf(result));
        } catch (Exception e) {
            text_result.setText("Error: Invalid input");
        }
        vibrateDevice();
    }


    private void vibrateDevice() {
        if (vibrator != null) vibrator.vibrate(50);
    }

    private boolean isOperator(char c) {
        return "+-*/%".indexOf(c) != -1;
    }

    private int countChar(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) if (c == ch) count++;
        return count;
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/' || op1 == '%') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private void performOperation(Stack<Double> numbers, Stack<Character> operators) {
        double b = numbers.pop(), a = numbers.pop(), res;
        switch (operators.pop()) {
            case '+': res = a + b; break;
            case '-': res = a - b; break;
            case '*': res = a * b; break;
            case '/': if (b == 0) throw new ArithmeticException(); res = a / b; break;
            case '%': if (b == 0) throw new ArithmeticException(); res = a % b; break;
            default: throw new IllegalArgumentException();
        }
        numbers.push(res);
    }

    private double evaluateExpression(String exp) throws Exception {
        exp = exp.replaceAll(" ", "");
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
                    sb.append(exp.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') performOperation(numbers, operators);
                if (!operators.isEmpty()) operators.pop(); else throw new IllegalArgumentException("Mismatched parenthesis");
            } else if (isOperator(c)) {
                if (c == '-' && (i == 0 || exp.charAt(i - 1) == '(' || isOperator(exp.charAt(i - 1)))) {
                    StringBuilder sb = new StringBuilder("-");
                    i++;
                    while (i < exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
                        sb.append(exp.charAt(i++));
                    }
                    i--;
                    numbers.push(Double.parseDouble(sb.toString()));
                    continue;
                }
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) performOperation(numbers, operators);
                operators.push(c);
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }

        while (!operators.isEmpty()) performOperation(numbers, operators);
        return numbers.pop();
    }
}
