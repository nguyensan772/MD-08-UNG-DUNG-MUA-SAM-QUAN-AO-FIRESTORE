package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Comment implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("product_id") // Gửi lên server cần key này
    private String productId;

    @SerializedName("content")
    private String content;

    @SerializedName("rating")
    private float rating;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("user_id")
    private UserInfo user;

    // 1. Constructor không tham số (Bắt buộc cho Retrofit/Gson)
    public Comment() {}

    // 2. Constructor có tham số để bạn dùng ở dòng 79 file DanhGia.java
    public Comment(String productId, float rating, String content) {
        this.productId = productId;
        this.rating = rating;
        this.content = content;
    }

    // --- Getters ---
    public String getContent() { return content; }
    public float getRating() { return rating; }
    public String getCreatedAt() { return createdAt; }

    public String getUserName() {
        return (user != null && user.fullName != null) ? user.fullName : "Người dùng";
    }

    public static class UserInfo implements Serializable {
        @SerializedName("full_name")
        public String fullName;
    }
}