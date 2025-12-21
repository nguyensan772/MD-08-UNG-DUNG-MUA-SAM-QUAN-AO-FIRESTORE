package com.example.md_08_ungdungfivestore.models;

public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getter & Setter (nếu cần, nhưng retrofit dùng reflection nên constructor là đủ)
}