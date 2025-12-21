package com.example.md_08_ungdungfivestore.services;

import com.google.gson.annotations.SerializedName;

public class VNPayResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("paymentUrl")
    private String paymentUrl;

    @SerializedName("message")
    private String message;

    // Getters
    public boolean isSuccess() { return success; }
    public String getPaymentUrl() { return paymentUrl; }
    public String getMessage() { return message; }
}