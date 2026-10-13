package com.example.activityeventdriven.api;

import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://user-api-llwn.onrender.com/";
    private static ApiService service;

    public static ApiService get() {
        if (service == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)   // Render may need time to wake up
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            service = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return service;
    }

    // Reads the "message" the server sends when something is wrong
    public static String errorMessage(Response<?> response) {
        try {
            AuthResponse err = new Gson().fromJson(response.errorBody().string(), AuthResponse.class);
            return err.getMessage();
        } catch (Exception e) {
            return "Error " + response.code();
        }
    }
}