package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.utils.TokenManager;

public class ManChao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_chao);

        // QUAN TRỌNG: Init ApiClient
        ApiClient.init(this);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginAndNavigate();
            }
        }, 3000); // 3 giây
    }

    private void checkLoginAndNavigate() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();

        Intent intent;
        if (token != null && !token.isEmpty()) {
            // Đã đăng nhập -> vào MainActivity
            intent = new Intent(ManChao.this, MainActivity.class);
        } else {
            // Chưa đăng nhập -> vào DangNhap
            intent = new Intent(ManChao.this, DangNhap.class);
        }

        startActivity(intent);
        finish(); // Đóng màn hình chào
    }
}