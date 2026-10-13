package com.example.activityeventdriven.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    // NEW: gets the list of users from the Render API
    @GET("users")
    Call<List<UserItem>> getUsers();
}