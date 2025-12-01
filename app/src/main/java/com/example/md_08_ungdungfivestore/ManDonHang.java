package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ManDonHang extends AppCompatActivity {
    private TabLayout datHangTabLayout;
    private ViewPager datHangViewPager;
    private ImageButton quayLaiBtn;

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

        anhXa();

        quayLaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void anhXa() {
        datHangTabLayout = findViewById(R.id.datHangTabLayout);
        datHangViewPager = findViewById(R.id.datHangViewPager);
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
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
