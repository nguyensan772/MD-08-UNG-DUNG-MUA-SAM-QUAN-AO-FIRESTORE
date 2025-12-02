package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.md_08_ungdungfivestore.fragments.DonHangFragment;

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
            case 0: // Chờ xác nhận
                return DonHangFragment.newInstance("pending");
            case 1: // Đang giao (Gộp confirmed, processing, shipping)
                return DonHangFragment.newInstance("shipping");
            case 2: // Đã giao
                return DonHangFragment.newInstance("delivered");
            case 3: // Đã hủy
                return DonHangFragment.newInstance("cancelled");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }
}