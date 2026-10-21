package com.example.activityeventdriven;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.activityeventdriven.api.ApiClient;
import com.example.activityeventdriven.api.AuthResponse;
import com.example.activityeventdriven.api.CustomerItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerListActivity extends AppCompatActivity {

    private final List<CustomerItem> customers = new ArrayList<>();
    private ArrayAdapter<CustomerItem> adapter;
    private TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        tvHeader = findViewById(R.id.tv_customer_header);
        ListView lvCustomers = findViewById(R.id.lv_customers);
        Button btnAdd = findViewById(R.id.btn_add_customer);
        Button btnBack = findViewById(R.id.btn_customers_back);

        adapter = makeAdapter(customers);
        lvCustomers.setAdapter(adapter);
        loadCustomers();

        btnAdd.setOnClickListener(v -> showAddDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadCustomers() {
        tvHeader.setText("Loading customers from Supabase... (may take a moment)");

        ApiClient.get().getCustomers().enqueue(new Callback<List<CustomerItem>>() {
            @Override
            public void onResponse(Call<List<CustomerItem>> call,
                                   Response<List<CustomerItem>> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    customers.clear();
                    customers.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    tvHeader.setText("Customers: " + customers.size());
                } else {
                    tvHeader.setText("Could not load customers (error " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<CustomerItem>> call, Throwable t) {
                if (isFinishing()) return;
                tvHeader.setText("Could not load customers: " + t.getMessage());
            }
        });
    }

    private void showAddDialog() {
        View form = getLayoutInflater().inflate(R.layout.dialog_add_customer, null);
        EditText etId = form.findViewById(R.id.et_customer_id);
        EditText etLast = form.findViewById(R.id.et_customer_lastname);
        EditText etFirst = form.findViewById(R.id.et_customer_firstname);
        EditText etMobile = form.findViewById(R.id.et_customer_mobile);
        EditText etAddress = form.findViewById(R.id.et_customer_address);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Customer")
                .setView(form)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnSave.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String last = etLast.getText().toString().trim();
            String first = etFirst.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (id.isEmpty()) { etId.setError("Customer ID is required"); return; }
            if (last.isEmpty()) { etLast.setError("Last name is required"); return; }
            if (first.isEmpty()) { etFirst.setError("First name is required"); return; }
            if (mobile.isEmpty()) { etMobile.setError("Mobile number is required"); return; }
            if (address.isEmpty()) { etAddress.setError("Address is required"); return; }

            btnSave.setEnabled(false);
            ApiClient.get().addCustomer(new CustomerItem(id, last, first, mobile, address))
                    .enqueue(new Callback<AuthResponse>() {
                        @Override
                        public void onResponse(Call<AuthResponse> call,
                                               Response<AuthResponse> response) {
                            if (isFinishing()) return;
                            if (response.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(CustomerListActivity.this,
                                        "Customer added", Toast.LENGTH_SHORT).show();
                                loadCustomers();
                            } else {
                                btnSave.setEnabled(true);
                                new AlertDialog.Builder(CustomerListActivity.this)
                                        .setTitle("Could not add customer")
                                        .setMessage(ApiClient.errorMessage(response))
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponse> call, Throwable t) {
                            if (isFinishing()) return;
                            btnSave.setEnabled(true);
                            new AlertDialog.Builder(CustomerListActivity.this)
                                    .setTitle("Could not add customer")
                                    .setMessage("Network error: " + t.getMessage())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    });
        });
    }

    private ArrayAdapter<CustomerItem> makeAdapter(List<CustomerItem> list) {
        return new ArrayAdapter<CustomerItem>(this, android.R.layout.simple_list_item_2,
                android.R.id.text1, list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                CustomerItem c = getItem(position);
                ((TextView) row.findViewById(android.R.id.text1)).setText(c.getFullName());
                ((TextView) row.findViewById(android.R.id.text2)).setText(
                        "ID: " + c.getCustomerId() + "  |  " + c.getMobileNo()
                                + "\n" + c.getAddress());
                return row;
            }
        };
    }
}