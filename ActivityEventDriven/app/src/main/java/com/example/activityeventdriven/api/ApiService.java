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

    @GET("users")
    Call<List<UserItem>> getUsers();

    @GET("customers")
    Call<List<CustomerItem>> getCustomers();

    @POST("customers")
    Call<AuthResponse> addCustomer(@Body CustomerItem customer);
}