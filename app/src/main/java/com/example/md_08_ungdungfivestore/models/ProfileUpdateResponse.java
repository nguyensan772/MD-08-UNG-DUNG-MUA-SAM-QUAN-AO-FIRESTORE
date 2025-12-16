package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

// Model này dùng để ánh xạ response từ PUT /update-profile
// Cấu trúc JSON trả về: { "message": "...", "user": {...}, "token": "..." }
public class ProfileUpdateResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user; // Đối tượng User đã được cập nhật

    @SerializedName("token")
    private String token; // Token JWT mới (rất quan trọng để lưu lại)

    // Constructor (Tùy chọn)
    public ProfileUpdateResponse() {
        // Default constructor
    }

    // --- Getters và Setters ---

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}