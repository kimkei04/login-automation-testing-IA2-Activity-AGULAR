package com.example.activityeventdriven;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView tvWelcome = findViewById(R.id.tv_dashboard_welcome);
        Button btnCustomers = findViewById(R.id.btn_customers);
        Button btnUsers = findViewById(R.id.btn_users);
        Button btnLogout = findViewById(R.id.btn_logout);

        String firstName = getIntent().getStringExtra("firstName");
        if (firstName != null && !firstName.isEmpty()) {
            tvWelcome.setText("Welcome, " + firstName + "! Choose an option.");
        }

        btnCustomers.setOnClickListener(v ->
                startActivity(new Intent(this, CustomerListActivity.class)));

        btnUsers.setOnClickListener(v ->
                startActivity(new Intent(this, UserListActivity.class)));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}