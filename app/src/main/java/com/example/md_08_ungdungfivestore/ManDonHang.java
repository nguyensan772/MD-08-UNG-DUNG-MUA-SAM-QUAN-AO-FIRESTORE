package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.md_08_ungdungfivestore.adapters.TabLayoutAdapter;
import com.google.android.material.tabs.TabLayout;

public class ManDonHang extends AppCompatActivity implements OnOrderUpdateListener {

    private TabLayout datHangTabLayout;
    private ViewPager datHangViewPager;
    private ImageButton quayLaiBtn;

    private static final String TAG = "ManDonHang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_don_hang);

        anhXa();
        setupTabs();
        setupListeners();
        checkIntentAndSelectTab();
    }

    private void anhXa() {
        datHangTabLayout = findViewById(R.id.datHangTabLayout);
        datHangViewPager = findViewById(R.id.datHangViewPager);
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
    }

    private void setupTabs() {
        // Xóa các tab cũ để tránh trùng lặp nếu có
        datHangTabLayout.removeAllTabs();

        // 1. Thêm 6 Tab theo đúng quy trình Server
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Chờ xác nhận"));  // Index 0
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đã xác nhận"));   // Index 1
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đang lấy hàng")); // Index 2
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đang giao"));     // Index 3
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đã giao"));       // Index 4
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đã hủy"));        // Index 5

        // 2. Setup Adapter
        TabLayoutAdapter adapter = new TabLayoutAdapter(this, getSupportFragmentManager(), datHangTabLayout.getTabCount());
        datHangViewPager.setAdapter(adapter);

        // Tối ưu: Load trước 5 trang để khi vuốt cho mượt (tùy chọn)
        datHangViewPager.setOffscreenPageLimit(5);

        // 3. Đồng bộ hóa ViewPager và TabLayout
        datHangViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(datHangTabLayout));
        datHangTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                datHangViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupListeners() {
        quayLaiBtn.setOnClickListener(view -> finish());
    }

    private void checkIntentAndSelectTab() {
        if (getIntent() != null) {
            int targetTab = getIntent().getIntExtra("targetTab", 0);
            if (targetTab >= 0 && targetTab < datHangTabLayout.getTabCount()) {
                datHangViewPager.setCurrentItem(targetTab, true);

                // Chọn visual trên TabLayout
                TabLayout.Tab tab = datHangTabLayout.getTabAt(targetTab);
                if (tab != null) {
                    tab.select();
                }
            }
        }
    }

    // Khi hủy đơn thành công ở tab "Chờ xác nhận", chuyển ngay sang tab "Đã hủy"
    @Override
    public void onOrderCancelled(String orderId) {
        Toast.makeText(this, "Đơn hàng #" + orderId + " đã hủy.", Toast.LENGTH_LONG).show();
        // Tab "Đã hủy" nằm ở vị trí số 5
        datHangViewPager.setCurrentItem(5, true);
    }
}