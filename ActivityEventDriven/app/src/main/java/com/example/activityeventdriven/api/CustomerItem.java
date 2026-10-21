package com.example.activityeventdriven.api;

import com.google.gson.annotations.SerializedName;

public class CustomerItem {

    @SerializedName("customerid")
    private String customerId;
    @SerializedName("lastname")
    private String lastName;
    @SerializedName("firstname")
    private String firstName;
    @SerializedName("mobileno")
    private String mobileNo;
    @SerializedName("address")
    private String address;

    public CustomerItem(String customerId, String lastName, String firstName,
                        String mobileNo, String address) {
        this.customerId = customerId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.mobileNo = mobileNo;
        this.address = address;
    }

    public String getCustomerId() { return customerId == null ? "" : customerId; }
    public String getMobileNo() { return mobileNo == null ? "" : mobileNo; }
    public String getAddress() { return address == null ? "" : address; }

    public String getFullName() {
        String first = firstName == null ? "" : firstName;
        String last = lastName == null ? "" : lastName;
        return (first + " " + last).trim();
    }
}