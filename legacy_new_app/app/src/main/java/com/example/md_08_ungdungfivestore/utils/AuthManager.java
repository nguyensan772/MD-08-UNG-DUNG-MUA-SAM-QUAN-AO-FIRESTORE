package com.example.md_08_ungdungfivestore.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    // Tên file SharedPreferences và key phải khớp chính xác!
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String ID_KEY = "user_id"; // 👈 Thêm Key mới cho UserID

    // --- HÀM LƯU TOKEN ---
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    // --- HÀM LẤY TOKEN ---
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, null);
    }

    // ==========================================
    // ⭐ PHẦN MỚI THÊM ĐỂ FIX LỖI SOCKET ⭐
    // ==========================================

    // --- HÀM LƯU USER ID (Gọi khi Login thành công) ---
    public static void saveUserId(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ID_KEY, userId);
        editor.apply();
    }

    // --- HÀM LẤY USER ID (MainActivity sẽ gọi hàm này) ---
    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(ID_KEY, null); // Trả về null nếu chưa lưu
    }

    // ==========================================

    // --- HÀM XÓA DỮ LIỆU (Dùng khi Logout) ---
    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(TOKEN_KEY);
        editor.remove(ID_KEY); // 👈 Xóa luôn cả ID khi đăng xuất
        editor.apply();
    }
}