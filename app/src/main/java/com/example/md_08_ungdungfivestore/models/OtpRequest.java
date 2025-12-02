package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

public class OtpRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("password")
    private String password;

    public OtpRequest(String email, String otp, String full_name, String password) {
        this.email = email;
        this.otp = otp;
        this.full_name = full_name;
        this.password = password;
    }

    // getters
    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getPassword() {
        return password;
    }
}
