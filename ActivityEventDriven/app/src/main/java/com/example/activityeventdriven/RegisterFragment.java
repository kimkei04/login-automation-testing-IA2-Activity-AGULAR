package com.example.activityeventdriven;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.activityeventdriven.api.ApiClient;
import com.example.activityeventdriven.api.AuthResponse;
import com.example.activityeventdriven.api.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etFirstName = view.findViewById(R.id.et_first_name);
        EditText etLastName = view.findViewById(R.id.et_last_name);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPassword = view.findViewById(R.id.et_password);
        Button btnRegister = view.findViewById(R.id.btn_register);
        Button btnGoLogin = view.findViewById(R.id.btn_go_login);

        // Button that goes back to the Login fragment (no more swiping)
        btnGoLogin.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnRegister.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // ---- VALIDATION ----
            if (firstName.isEmpty()) {
                etFirstName.setError("First name is required");
                return;
            }
            if (lastName.isEmpty()) {
                etLastName.setError("Last name is required");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email");
                return;
            }
            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            // ---- CALL THE RENDER API ----
            btnRegister.setEnabled(false);
            ApiClient.get().register(new RegisterRequest(firstName, lastName, email, password))
                    .enqueue(new Callback<AuthResponse>() {
                        @Override
                        public void onResponse(Call<AuthResponse> call,
                                               Response<AuthResponse> response) {
                            if (!isAdded()) return;
                            btnRegister.setEnabled(true);

                            if (response.isSuccessful()) {
                                // Also save the new user in SharedPreferences on the phone
                                LocalUserStore.saveUser(requireContext(), firstName, lastName, email, password);

                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Registration Successful")
                                        .setMessage("Your account has been created. You can now log in.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (d, w) -> {
                                            if (isAdded()) {
                                                getParentFragmentManager().popBackStack();
                                            }
                                        })
                                        .show();
                            } else {
                                // e.g. "Email is already registered"
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Registration Unsuccessful")
                                        .setMessage(ApiClient.errorMessage(response))
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponse> call, Throwable t) {
                            if (!isAdded()) return;
                            btnRegister.setEnabled(true);
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Registration Unsuccessful")
                                    .setMessage("Network error: " + t.getMessage())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    });
        });
    }
}