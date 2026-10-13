package com.example.activityeventdriven;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.activityeventdriven.api.ApiClient;
import com.example.activityeventdriven.api.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {

    private final List<UserItem> apiUsers = new ArrayList<>();
    private ArrayAdapter<UserItem> apiAdapter;
    private TextView tvApiHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        tvApiHeader = findViewById(R.id.tv_api_header);
        TextView tvLocalHeader = findViewById(R.id.tv_local_header);
        ListView lvApi = findViewById(R.id.lv_api_users);
        ListView lvLocal = findViewById(R.id.lv_local_users);
        Button btnLogout = findViewById(R.id.btn_logout);

        // ---- List 1: users from the Render API ----
        apiAdapter = makeAdapter(apiUsers);
        lvApi.setAdapter(apiAdapter);
        loadApiUsers();

        // ---- List 2: users saved on the phone (SharedPreferences) ----
        List<UserItem> localUsers = loadLocalUsers();
        lvLocal.setAdapter(makeAdapter(localUsers));
        tvLocalHeader.setText("Saved on this phone (SharedPreferences): " + localUsers.size());

        // ---- Logout goes back to the Login screen ----
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Loads the user list from the Render API
    private void loadApiUsers() {
        tvApiHeader.setText("Loading users from Render... (may take a moment)");

        ApiClient.get().getUsers().enqueue(new Callback<List<UserItem>>() {
            @Override
            public void onResponse(Call<List<UserItem>> call, Response<List<UserItem>> response) {
                if (isFinishing()) return;
                if (response.isSuccessful() && response.body() != null) {
                    apiUsers.clear();
                    apiUsers.addAll(response.body());
                    apiAdapter.notifyDataSetChanged();
                    tvApiHeader.setText("Render API users: " + apiUsers.size());
                } else {
                    tvApiHeader.setText("Could not load users (error " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<UserItem>> call, Throwable t) {
                if (isFinishing()) return;
                tvApiHeader.setText("Could not load users: " + t.getMessage());
            }
        });
    }

    // Reads the users saved in SharedPreferences
    private List<UserItem> loadLocalUsers() {
        List<UserItem> list = new ArrayList<>();
        JSONArray array = LocalUserStore.getUsers(this);
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.optJSONObject(i);
            if (o != null) {
                list.add(new UserItem(
                        o.optString("FirstName"),
                        o.optString("LastName"),
                        o.optString("Email")));
            }
        }
        return list;
    }

    // Shows each user as: name (big) + email (small)
    private ArrayAdapter<UserItem> makeAdapter(List<UserItem> list) {
        return new ArrayAdapter<UserItem>(this, android.R.layout.simple_list_item_2,
                android.R.id.text1, list) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                UserItem user = getItem(position);
                ((TextView) row.findViewById(android.R.id.text1)).setText(user.getFullName());
                ((TextView) row.findViewById(android.R.id.text2)).setText(user.getEmail());
                return row;
            }
        };
    }
}