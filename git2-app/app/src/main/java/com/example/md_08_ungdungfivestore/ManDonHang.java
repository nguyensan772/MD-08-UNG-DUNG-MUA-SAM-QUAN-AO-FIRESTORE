package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.md_08_ungdungfivestore.adapters.TabLayoutAdapter;
import com.google.android.material.tabs.TabLayout;

public class ManDonHang extends AppCompatActivity {
    private TabLayout datHangTabLayout;
    private ViewPager datHangViewPager;
    private ImageButton quayLaiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_don_hang);
        Log.d("A","START");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();
        Toast.makeText(
                this,
                "ABC",
                Toast.LENGTH_SHORT
        );
        // Setup Tabs
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Chờ xác nhận"));
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đang giao"));
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đã giao"));
        datHangTabLayout.addTab(datHangTabLayout.newTab().setText("Đã hủy"));
        datHangTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Setup Adapter
        TabLayoutAdapter adapter = new TabLayoutAdapter(this, getSupportFragmentManager(),
                datHangTabLayout.getTabCount());
        datHangViewPager.setAdapter(adapter);

        // Sync Tab and ViewPager
        datHangViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(datHangTabLayout));
        datHangTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                datHangViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
}