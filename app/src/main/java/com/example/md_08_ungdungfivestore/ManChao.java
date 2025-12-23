package com.example.md_08_ungdungfivestore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ManChao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_chao);
        SharedPreferences sharedPreferences= getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences shareTheme= getSharedPreferences("AppSettingPrefs", Context.MODE_PRIVATE);
        if (shareTheme.getBoolean("IsNightMode", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if (sharedPreferences.getString("isChecked","0").equals("1")){
            startActivity(new Intent(ManChao.this,MainActivity.class));
            finish();
            return;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent
                        (ManChao.this, DangNhap.class);
                startActivity(intent); // 👉 Thực hiện chuyển màn
                finish(); // 👉 Đóng màn hình chào để không quay lại khi nhấn Back

            }
        },5000);
    }
}