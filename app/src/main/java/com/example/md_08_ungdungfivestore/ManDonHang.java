package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ManDonHang extends AppCompatActivity {

    private TextView tvChoXN, tvDangGiao, tvDaGiao, tvDaHuy, tvEmpty;
    private ImageButton quayLaiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_don_hang); // đúng tên layout của bạn

        // 1. Ánh xạ view
        tvChoXN     = findViewById(R.id.choXacNhanTextView);
        tvDangGiao  = findViewById(R.id.dangGiaoTextView);
        tvDaGiao    = findViewById(R.id.daGiaoTextView);
        tvDaHuy     = findViewById(R.id.daHuyTextView);
        tvEmpty     = findViewById(R.id.tvEmpty);        // TextView nội dung trống
        quayLaiBtn  = findViewById(R.id.quayLaiBtn);     // nút mũi tên vàng

        // 2. Nút quay lại: đóng màn hiện tại → quay về CaiDatActivity
        quayLaiBtn.setOnClickListener(v -> finish());

        // 3. Xử lý click cho 4 tab trạng thái
        tvChoXN.setOnClickListener(v -> {
            highlightTab(tvChoXN);
            tvEmpty.setText("Bạn chưa có đơn hàng chờ xác nhận.");
        });

        tvDangGiao.setOnClickListener(v -> {
            highlightTab(tvDangGiao);
            tvEmpty.setText("Bạn chưa có đơn hàng đang giao.");
        });

        tvDaGiao.setOnClickListener(v -> {
            highlightTab(tvDaGiao);
            tvEmpty.setText("Bạn chưa có đơn hàng đã giao.");
        });

        tvDaHuy.setOnClickListener(v -> {
            highlightTab(tvDaHuy);
            tvEmpty.setText("Bạn chưa có đơn hàng đã hủy.");
        });

        // 4. Tab mặc định khi mới mở màn
        highlightTab(tvChoXN);
        tvEmpty.setText("Bạn chưa có đơn hàng chờ xác nhận.");
    }

    /**
     * Đổi màu tab được chọn
     */
    private void highlightTab(TextView selected) {
        int normalColor   = ContextCompat.getColor(this, android.R.color.white);
        int selectedColor = ContextCompat.getColor(this, R.color.ChuDe); // màu chủ đề của bạn

        // reset tất cả về màu trắng
        tvChoXN.setTextColor(normalColor);
        tvDangGiao.setTextColor(normalColor);
        tvDaGiao.setTextColor(normalColor);
        tvDaHuy.setTextColor(normalColor);

        // set màu nổi bật cho tab được chọn
        selected.setTextColor(selectedColor);
    }
}
