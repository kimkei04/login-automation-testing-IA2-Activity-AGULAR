package com.example.activityeventdriven.api;

import com.google.gson.annotations.SerializedName;

// One user in the list (the password is deliberately not included)
public class UserItem {

    @SerializedName("FirstName")
    private String firstName;
    @SerializedName("LastName")
    private String lastName;
    @SerializedName("Email")
    private String email;

    public UserItem(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFullName() {
        String first = firstName == null ? "" : firstName;
        String last = lastName == null ? "" : lastName;
        return (first + " " + last).trim();
    }

    public String getEmail() {
        return email == null ? "" : email;
    }
}