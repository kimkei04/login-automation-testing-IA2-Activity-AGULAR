package com.example.activityeventdriven;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.activityeventdriven.api.ApiClient;
import com.example.activityeventdriven.api.AuthResponse;
import com.example.activityeventdriven.api.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPassword = view.findViewById(R.id.et_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnGoRegister = view.findViewById(R.id.btn_go_register);

        // Button that opens the Register fragment (no more swiping)
        btnGoRegister.setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .addToBackStack(null)
                        .commit());

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // ---- LOGIN VALIDATION ----
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email");
                return;
            }
            if (password.isEmpty()) {
                etPassword.setError("Password is required");
                return;
            }

            // ---- CALL THE RENDER API ----
            btnLogin.setEnabled(false);
            Toast.makeText(requireContext(),
                    "Logging in... (first try may be slow)", Toast.LENGTH_SHORT).show();

            ApiClient.get().login(new LoginRequest(email, password))
                    .enqueue(new Callback<AuthResponse>() {
                        @Override
                        public void onResponse(Call<AuthResponse> call,
                                               Response<AuthResponse> response) {
                            if (!isAdded()) return;
                            btnLogin.setEnabled(true);

                            if (response.isSuccessful() && response.body() != null) {
                                String firstName = response.body().getFirstName();
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Login Successful")
                                        .setMessage("Welcome, " + firstName + "! Next, verify your OTP.")
                                        .setCancelable(false)
                                        .setPositiveButton("Continue", (d, w) -> {
                                            Intent intent = new Intent(requireContext(), OtpActivity.class);
                                            intent.putExtra("firstName", firstName);
                                            startActivity(intent);
                                        })
                                        .show();
                            } else {
                                // "User does not exist. Please sign up." or "Incorrect password"
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Login Unsuccessful")
                                        .setMessage(ApiClient.errorMessage(response))
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponse> call, Throwable t) {
                            if (!isAdded()) return;
                            btnLogin.setEnabled(true);
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Login Unsuccessful")
                                    .setMessage("Network error: " + t.getMessage())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    });
        });
    }
}