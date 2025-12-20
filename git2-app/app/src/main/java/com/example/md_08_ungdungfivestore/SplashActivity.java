package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.utils.TokenManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Init ApiClient
        ApiClient.init(this);

        // Check token sau 2 giây
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkLoginStatus();
        }, SPLASH_DELAY);
    }

    private void checkLoginStatus() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();

        Intent intent;
        if (token != null && !token.isEmpty()) {
            // Đã login -> vào MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // Chưa login -> vào DangNhap
            intent = new Intent(SplashActivity.this, DangNhap.class);
        }

        startActivity(intent);
        finish();
    }
}
