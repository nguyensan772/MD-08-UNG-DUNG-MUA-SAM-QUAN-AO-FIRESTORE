package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.md_08_ungdungfivestore.fragments.ChoGiaoHangFragment;
import com.example.md_08_ungdungfivestore.fragments.ChoXacNhanFragment;
import com.example.md_08_ungdungfivestore.fragments.DaGiaoFragment;
import com.example.md_08_ungdungfivestore.fragments.DaHuyFragment;
import com.example.md_08_ungdungfivestore.fragments.DaXacNhanFragment; // Import mới
import com.example.md_08_ungdungfivestore.fragments.DangLayHangFragment; // Import mới

public class TabLayoutAdapter extends FragmentPagerAdapter {
    private Context context;
    int tabs;

    public TabLayoutAdapter(Context context, FragmentManager fm, int tabs) {
        super(fm);
        this.context = context;
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChoXacNhanFragment(); // Pending
            case 1:
                return new DaXacNhanFragment();   // Confirmed (MỚI)
            case 2:
                return new DangLayHangFragment(); // Processing (MỚI)
            case 3:
                return new ChoGiaoHangFragment(); // Shipping
            case 4:
                return new DaGiaoFragment();      // Delivered
            case 5:
                return new DaHuyFragment();       // Cancelled
            default:
                return new ChoXacNhanFragment();
        }
    }

    @Override
    public int getCount() {
        return tabs; // = 6
    }
}