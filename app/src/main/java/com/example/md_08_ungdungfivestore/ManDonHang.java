// File: com.example.md_08_ungdungfivestore.ManDonHang.java

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
        // 1. Thêm 4 Tab với tên trạng thái
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Chờ xác nhận")); // Vị trí 0
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đang giao"));    // Vị trí 1
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đã giao"));      // Vị trí 2
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đã hủy"));       // Vị trí 3

        // 2. Setup Adapter để cấp 4 Fragments tương ứng
        // CHÚ Ý: ĐẢM BẢO FILE TabLayoutAdapter.java TỒN TẠI VÀ ĐƯỢC TRIỂN KHAI ĐÚNG
        TabLayoutAdapter adapter = new TabLayoutAdapter(this, getSupportFragmentManager(),
                datHangTabLayout.getTabCount());
        datHangViewPager.setAdapter(adapter);

        // 3. Đồng bộ hóa ViewPager và TabLayout (Cho phép vuốt và nhấn tab)
        datHangViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(datHangTabLayout));
        datHangTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Khi nhấn Tab, chuyển ViewPager sang trang đó
                datHangViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không làm gì
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không làm gì
            }
        });
    }

    private void setupListeners() {
        quayLaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkIntentAndSelectTab() {
        if (getIntent() != null) {
            int targetTab = getIntent().getIntExtra("targetTab", 0);
            if (targetTab >= 0 && targetTab < datHangTabLayout.getTabCount()) {
                // Thiết lập ViewPager và Tab đã chọn
                datHangViewPager.setCurrentItem(targetTab, true);
                TabLayout.Tab tab = datHangTabLayout.getTabAt(targetTab);
                if (tab != null) {
                    tab.select();
                }

                String newOrderId = getIntent().getStringExtra("orderId");
                if (newOrderId != null) {
                    Log.d(TAG, "Mở màn hình đơn hàng với đơn mới: " + newOrderId);
                }
            }
        }
    }

    // TRIỂN KHAI PHƯƠNG THỨC CỦA INTERFACE (Ví dụ: Chuyển sang tab Đã hủy sau khi hủy đơn)
    @Override
    public void onOrderCancelled(String orderId) {
        Toast.makeText(this, "Đơn hàng #" + orderId + " đã được hủy thành công và chuyển sang tab Đã hủy.", Toast.LENGTH_LONG).show();

        // Chuyển sang tab "Đã Hủy" (Vị trí 3)
        datHangViewPager.setCurrentItem(3, true);
    }
}