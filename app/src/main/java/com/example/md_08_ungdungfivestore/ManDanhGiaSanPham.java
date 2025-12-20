package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManDanhGiaSanPham extends AppCompatActivity {

    private RatingBar danhGiaRatingBar;
    private EditText danhGiaEditText;
    private Button guiNhanXetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_danh_gia_san_pham);

        // Áp dụng insets cho layout chính
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        danhGiaRatingBar = findViewById(R.id.danhGiaRatingBar);
        danhGiaEditText = findViewById(R.id.danhGiaEditText);
        guiNhanXetButton = findViewById(R.id.guiNhanXetButton);

        // Bắt sự kiện click nút Gửi
        guiNhanXetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float soSao = danhGiaRatingBar.getRating();
                String nhanXet = danhGiaEditText.getText().toString().trim();

                if (nhanXet.isEmpty()) {
                    Toast.makeText(ManDanhGiaSanPham.this,
                            "Vui lòng nhập nhận xét trước khi gửi!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý dữ liệu đánh giá (ví dụ: gửi lên server hoặc lưu local)
                    // Ở đây mình demo bằng Toast
                    String message = "Bạn đã đánh giá " + soSao + " sao\nNhận xét: " + nhanXet;
                    Toast.makeText(ManDanhGiaSanPham.this, message, Toast.LENGTH_LONG).show();

                    // Sau khi gửi có thể reset form
                    danhGiaRatingBar.setRating(5);
                    danhGiaEditText.setText("");
                }
            }
        });
    }
}
