package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;import androidx.appcompat.app.AppCompatActivity;

public class ManChinhSach extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinh_sach);

        // 1. Ánh xạ nút quay lại từ giao diện
        btnBack = findViewById(R.id.btnBack);

        // 2. Bắt sự kiện click để đóng màn hình
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity hiện tại và quay về màn hình trước đó
            }
        });
    }
}
