package com.example.activityeventdriven.api;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("FirstName")
    private String firstName;

    public String getMessage() { return message; }
    public String getFirstName() { return firstName; }
}