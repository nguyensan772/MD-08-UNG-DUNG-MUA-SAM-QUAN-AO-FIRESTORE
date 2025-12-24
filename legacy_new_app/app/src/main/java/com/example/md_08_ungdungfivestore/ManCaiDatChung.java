package com.example.md_08_ungdungfivestore;

import android.content.SharedPreferences;import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class ManCaiDatChung extends AppCompatActivity {
    // Tên file lưu trữ cài đặt
    private static final String PREFS_NAME = "AppSettingPrefs";
    private static final String KEY_IS_NIGHT_MODE = "IsNightMode";

    SwitchCompat btnChuDe;
    ImageButton btnBack; // Khai báo nút back
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_cai_dat_chung);

        anhXa();

        // --- XỬ LÝ LOGIC CHẾ ĐỘ TỐI (GIỮ NGUYÊN) ---
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 1. Kiểm tra trạng thái đã lưu để set chế độ
        boolean isNightMode = sharedPreferences.getBoolean(KEY_IS_NIGHT_MODE, false);
        btnChuDe.setChecked(isNightMode);

        // Áp dụng chế độ Sáng/Tối dựa trên cấu hình đã lưu
        // Lưu ý: Việc setDelegate này có thể làm Activity restart lại ngay lập tức
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // 2. Bắt sự kiện khi gạt Switch
        btnChuDe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    // Chuyển sang tối
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    luuTrangThai(true);
                } else {
                    // Chuyển sang sáng
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    luuTrangThai(false);
                }
            }
        });

        // --- XỬ LÝ NÚT QUAY LẠI (MỚI THÊM) ---
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình này để quay lại màn trước
            }
        });
    }

    private void anhXa (){
        btnChuDe = findViewById(R.id.btnChuDe);
        btnBack = findViewById(R.id.btnBack); // Ánh xạ nút back từ XML

        // Đã xóa ánh xạ Toolbar vì layout mới không dùng id toolbarCaiDatChung
    }

    private void luuTrangThai(boolean isNight) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_NIGHT_MODE, isNight);
        editor.apply();
    }
}
