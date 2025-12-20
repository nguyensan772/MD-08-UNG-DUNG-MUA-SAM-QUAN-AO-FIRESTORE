package com.example.md_08_ungdungfivestore.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Quản lý JWT token trong SharedPreferences
 */
public class TokenManager {
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu JWT token
     */
    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    /**
     * Lấy JWT token
     */
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * Xóa JWT token (đăng xuất)
     */
    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.apply();
    }

    /**
     * Kiểm tra xem user đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    /**
     * Lưu user ID
     */
    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    /**
     * Lấy user ID
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
}
