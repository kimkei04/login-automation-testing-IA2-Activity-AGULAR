package com.example.activityeventdriven;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private TextView tvTimer;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        etOtp = findViewById(R.id.et_otp);
        tvTimer = findViewById(R.id.tv_timer);
        Button btnVerify = findViewById(R.id.btn_verify);
        TextView tvResend = findViewById(R.id.tv_resend);

        String firstName = getIntent().getStringExtra("firstName");

        // Make an OTP as soon as this screen opens (not again if the screen rotates)
        if (savedInstanceState == null) {
            sendNewOtp();
        }

        btnVerify.setOnClickListener(v -> {
            String typed = etOtp.getText().toString().trim();

            if (typed.length() != 4) {
                etOtp.setError("Enter the 4-digit OTP");
                return;
            }

            // Check expiry first
            if (OtpStore.isExpired()) {
                etOtp.setText("");
                new AlertDialog.Builder(this)
                        .setTitle("OTP Expired")
                        .setMessage("Your OTP is only valid for 30 seconds. Tap Resend OTP to get a new one.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            if (OtpStore.verify(typed)) {
                if (timer != null) timer.cancel();
                OtpStore.clear();
                new AlertDialog.Builder(this)
                        .setTitle("Verification Successful")
                        .setMessage("Welcome, " + (firstName == null ? "" : firstName) + "! You are now logged in.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (d, w) -> {
                            // NEW: open the Users page, and close the OTP page
                            startActivity(new Intent(this, UserListActivity.class));
                            finish();
                        })
                        .show();
            } else {
                etOtp.setText("");
                new AlertDialog.Builder(this)
                        .setTitle("Verification Unsuccessful")
                        .setMessage("Wrong OTP. Please try again.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        tvResend.setOnClickListener(v -> sendNewOtp());
    }

    // Generates a new random OTP, stores it in memory, shows it, and starts the 30s countdown
    private void sendNewOtp() {
        String otp = OtpStore.generate();
        startTimer();
        new AlertDialog.Builder(this)
                .setTitle("Your OTP")
                .setMessage("Your 4-digit OTP is: " + otp + "\n\nIt is valid for 30 seconds.")
                .setPositiveButton("OK", null)
                .show();
    }

    // Counts down from 30 seconds and shows it on screen
    private void startTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(OtpStore.VALIDITY_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("OTP expires in " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("OTP expired. Tap Resend OTP.");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}