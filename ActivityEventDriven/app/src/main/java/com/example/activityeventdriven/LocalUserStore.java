package com.example.activityeventdriven;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// A small local "API" that keeps signed-up users in SharedPreferences on the phone
public class LocalUserStore {

    private static final String TAG = "LocalUserStore";
    private static final String PREF_NAME = "UserPrefs";   // the name of the storage file
    private static final String KEY_USERS = "users";       // all users are kept under this key

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Saves a new user. Does nothing if the email is already saved.
    public static void saveUser(Context context, String firstName, String lastName,
                                String email, String password) {
        try {
            if (userExists(context, email)) return;

            JSONArray users = getUsers(context);

            JSONObject user = new JSONObject();
            user.put("FirstName", firstName);
            user.put("LastName", lastName);
            user.put("Email", email);
            user.put("Password", password);
            users.put(user);

            prefs(context).edit().putString(KEY_USERS, users.toString()).apply();
            Log.d(TAG, "Saved user " + email + " | total saved: " + users.length());
        } catch (JSONException e) {
            Log.e(TAG, "Could not save user", e);
        }
    }

    // Returns every saved user as a JSON list
    public static JSONArray getUsers(Context context) {
        String json = prefs(context).getString(KEY_USERS, "[]");
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    // True if this email is already saved
    public static boolean userExists(Context context, String email) {
        JSONArray users = getUsers(context);
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.optJSONObject(i);
            if (u != null && email.equalsIgnoreCase(u.optString("Email"))) {
                return true;
            }
        }
        return false;
    }

    // Deletes all saved users (handy for testing)
    public static void clearAll(Context context) {
        prefs(context).edit().remove(KEY_USERS).apply();
    }
}