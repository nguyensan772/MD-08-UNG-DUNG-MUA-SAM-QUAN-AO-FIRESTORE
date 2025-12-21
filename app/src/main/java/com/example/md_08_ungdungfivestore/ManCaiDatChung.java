package com.example.md_08_ungdungfivestore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManCaiDatChung extends AppCompatActivity {
    // Tên file lưu trữ cài đặt
    private static final String PREFS_NAME = "AppSettingPrefs";
    private static final String KEY_IS_NIGHT_MODE = "IsNightMode";
    SwitchCompat btnChuDe;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_cai_dat_chung);
        anhXa();

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // 1. Kiểm tra trạng thái đã lưu để set chế độ ngay khi mở app
        boolean isNightMode = sharedPreferences.getBoolean(KEY_IS_NIGHT_MODE, false);
        // Cập nhật trạng thái hiển thị của Switch cho đúng với chế độ hiện tại
        btnChuDe.setChecked(isNightMode);

        // [QUAN TRỌNG] Áp dụng chế độ Sáng/Tối dựa trên cấu hình đã lưu
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 2. Bắt sự kiện khi gạt Switch
        btnChuDe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    //Chuyển sang tối
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    luuTrangThai(true);
                }else {
                    //Chuyển sang sáng
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    luuTrangThai(false);
                }
            }
        });


    }



    private void anhXa (){
        btnChuDe = findViewById(R.id.btnChuDe);
    }

    private void luuTrangThai(boolean isNight) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_NIGHT_MODE, isNight);
        editor.apply();
    }
}