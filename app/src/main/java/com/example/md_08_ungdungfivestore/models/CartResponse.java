package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartResponse {
    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    private List<CartItem> items;

    // ⭐ Thêm trường success để kiểm tra nhanh kết quả API
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public List<CartItem> getItems() { return items; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }

    // Setters (Nếu cần thiết cho việc unit test hoặc local data)
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
}