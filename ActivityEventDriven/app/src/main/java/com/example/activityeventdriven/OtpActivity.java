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

        if (savedInstanceState == null) {
            sendNewOtp();
        }

        btnVerify.setOnClickListener(v -> {
            String typed = etOtp.getText().toString().trim();

            if (typed.length() != 4) {
                etOtp.setError("Enter the 4-digit OTP");
                return;
            }

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
                            // Open the Dashboard (main menu), and close the OTP page
                            Intent intent = new Intent(this, DashboardActivity.class);
                            intent.putExtra("firstName", firstName);
                            startActivity(intent);
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

    private void sendNewOtp() {
        String otp = OtpStore.generate();
        startTimer();
        new AlertDialog.Builder(this)
                .setTitle("Your OTP")
                .setMessage("Your 4-digit OTP is: " + otp + "\n\nIt is valid for 30 seconds.")
                .setPositiveButton("OK", null)
                .show();
    }

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