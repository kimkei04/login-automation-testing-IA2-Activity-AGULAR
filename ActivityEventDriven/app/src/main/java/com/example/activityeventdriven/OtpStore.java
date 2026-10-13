package com.example.activityeventdriven;

import android.os.SystemClock;

import java.security.SecureRandom;
import java.util.Locale;

public class OtpStore {

    // How long an OTP is valid: 30 seconds
    public static final long VALIDITY_MS = 30_000;

    // The OTP and the time it was made are kept here, in memory
    private static String currentOtp = null;
    private static long generatedAt = 0;

    // Makes a new random 4-digit OTP (0000 to 9999) and remembers when it was made
    public static String generate() {
        int number = new SecureRandom().nextInt(10000);
        currentOtp = String.format(Locale.US, "%04d", number);
        generatedAt = SystemClock.elapsedRealtime();
        return currentOtp;
    }

    // True if there is no OTP, or more than 30 seconds have passed
    public static boolean isExpired() {
        return currentOtp == null
                || SystemClock.elapsedRealtime() - generatedAt > VALIDITY_MS;
    }

    // Correct only if it matches AND it has not expired
    public static boolean verify(String input) {
        return !isExpired() && currentOtp.equals(input);
    }

    // Forgets the OTP (used after a successful verification)
    public static void clear() {
        currentOtp = null;
    }
}