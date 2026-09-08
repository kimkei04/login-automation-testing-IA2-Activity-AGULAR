package com.aguilar.loginapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Hardcoded valid credentials, used only so the automation script has a
    // known-correct pair to test against.
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "Admin@123";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            // Test case 1: Cannot login (missing fields)
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!username.equals(VALID_USERNAME)) {
            // Test case 2: Wrong user
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(VALID_PASSWORD)) {
            // Test case 3: Wrong password
            Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
    }
}
