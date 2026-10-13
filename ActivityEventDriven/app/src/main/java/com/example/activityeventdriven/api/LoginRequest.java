package com.example.activityeventdriven.api;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("Email")
    private String email;
    @SerializedName("Password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}