package com.example.md_08_ungdungfivestore.models;

public class ChangePasswordResponse {
    private String message;
    private String token;
    // Có thể thêm User user nếu cần dùng, nhưng tạm thời chỉ cần message để báo thành công

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}