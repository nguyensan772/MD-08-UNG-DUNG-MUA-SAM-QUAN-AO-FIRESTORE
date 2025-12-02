package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.md_08_ungdungfivestore.ManDatHang;
import com.example.md_08_ungdungfivestore.ManDonHang;
import com.example.md_08_ungdungfivestore.R;

public class TrangCaNhanFragment extends Fragment {

    private ImageView btnBack;
    private LinearLayout btnDonHang, btnTheNganHang, btnThongTinCaNhan, btnLienHe, btnDangXuat;

    public TrangCaNhanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trang_ca_nhan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhXa(view);
        setupListeners();
    }

    private void anhXa(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        btnDonHang = view.findViewById(R.id.btnDonHang);
        btnTheNganHang = view.findViewById(R.id.btnTheNganHang);
        btnThongTinCaNhan = view.findViewById(R.id.btnThongTinCaNhan);
        btnLienHe = view.findViewById(R.id.btnLienHe);
        btnDangXuat = view.findViewById(R.id.btnDangXuat);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            // Nếu là fragment trong bottom nav thì có thể không cần back,
            // hoặc back về trang chủ. Tạm thời để trống hoặc Toast.
            Toast.makeText(getContext(), "Quay lại", Toast.LENGTH_SHORT).show();
        });

        btnDonHang.setOnClickListener(
                v -> {
                    Log.d("A","START");
                    this.getContext().startActivity(new Intent(this.getContext(), ManDonHang.class));
                });

        btnTheNganHang.setOnClickListener(v -> Toast
                .makeText(getContext(), "Tính năng Thẻ ngân hàng đang phát triển", Toast.LENGTH_SHORT).show());

        btnThongTinCaNhan.setOnClickListener(v -> Toast
                .makeText(getContext(), "Tính năng Thông tin cá nhân đang phát triển", Toast.LENGTH_SHORT).show());

        btnLienHe.setOnClickListener(
                v -> Toast.makeText(getContext(), "Tính năng Liên hệ đang phát triển", Toast.LENGTH_SHORT).show());

        btnDangXuat.setOnClickListener(v -> {
            // Clear token
            com.example.md_08_ungdungfivestore.utils.TokenManager tokenManager = new com.example.md_08_ungdungfivestore.utils.TokenManager(
                    getContext());
            tokenManager.clearToken();

            // Navigate to Login
            android.content.Intent intent = new android.content.Intent(getContext(),
                    com.example.md_08_ungdungfivestore.DangNhap.class);
            intent.setFlags(
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        });
    }
}