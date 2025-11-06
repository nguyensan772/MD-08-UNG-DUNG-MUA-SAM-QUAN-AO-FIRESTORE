package com.example.md_08_ungdungfivestore;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.md_08_ungdungfivestore.adapters.TabLayoutAdapter;
import com.google.android.material.tabs.TabLayout;

public class TrangCaNhanActivity extends AppCompatActivity {
    private TabLayout caNhanTabLayout;
    private ViewPager caNhanViewPager;
    private TabLayoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_ca_nhan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        caNhanTabLayout = findViewById(R.id.caNhanTabLayout);
        caNhanViewPager = findViewById(R.id.caNhanViewPager);

        caNhanTabLayout.addTab(caNhanTabLayout.newTab().setText("Chờ xác nhận"));
        caNhanTabLayout.addTab(caNhanTabLayout.newTab().setText("Chờ giao hàng"));
        caNhanTabLayout.addTab(caNhanTabLayout.newTab().setText("Đánh giá"));
        caNhanTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new TabLayoutAdapter(this, getSupportFragmentManager(), caNhanTabLayout.getTabCount());

        caNhanViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(caNhanTabLayout));
        caNhanViewPager.setAdapter(adapter);

        caNhanTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                caNhanViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}