package com.example.md_08_ungdungfivestore.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.md_08_ungdungfivestore.DangNhap;
import com.example.md_08_ungdungfivestore.DoiMatKhauActivity; // ⭐ Nhớ import màn hình Đổi Mật Khẩu
import com.example.md_08_ungdungfivestore.ManCaiDatChung;
import com.example.md_08_ungdungfivestore.ManChat;
import com.example.md_08_ungdungfivestore.ManDonHang;
import com.example.md_08_ungdungfivestore.ManThongTinCaNhan;
import com.example.md_08_ungdungfivestore.R;

public class TrangCaNhanFragment extends Fragment {

    // Khai báo các nút
    private LinearLayout btnDonHang,btnCaiDatChung;
    private LinearLayout btnThongTinCaNhan;
    private LinearLayout btnDoiMatKhau; // ⭐ Thêm nút Đổi mật khẩu
    private LinearLayout btnLienHe;
    private LinearLayout btnDangXuat;

    public TrangCaNhanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trang_ca_nhan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ các nút (ID phải khớp với file XML layout)
        btnDonHang = view.findViewById(R.id.btnDonHang);
        btnThongTinCaNhan = view.findViewById(R.id.btnThongTinCaNhan);
        btnDoiMatKhau = view.findViewById(R.id.btnDoiMatKhau); // ⭐ Ánh xạ nút mới
        btnLienHe = view.findViewById(R.id.btnLienHe);
        btnDangXuat = view.findViewById(R.id.btnDangXuat);
        btnCaiDatChung = view.findViewById(R.id.btnCaiDatChung); // ⭐ Ánh xạ nút mới



        // 2. Thiết lập sự kiện Click
        setupListeners();
    }

    private void setupListeners() {
        btnCaiDatChung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ManCaiDatChung.class));
            }
        });
        // --- Nút Đơn hàng ---
        btnDonHang.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ManDonHang.class);
            startActivity(intent);
        });

        // --- Nút Thông tin cá nhân ---
        btnThongTinCaNhan.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ManThongTinCaNhan.class);
            startActivity(intent);
        });

        // --- Nút Đổi mật khẩu (MỚI THÊM) ---
        btnDoiMatKhau.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DoiMatKhauActivity.class);
            startActivity(intent);
        });

        // --- Nút Liên hệ ---
        btnLienHe.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ManChat.class);
            startActivity(intent);
        });

        // --- Nút Đăng xuất ---
        btnDangXuat.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    /**
     * Hiển thị hộp thoại xác nhận Đăng xuất
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Thực hiện logic đăng xuất: Xóa Token và chuyển hướng
     */
    private void logout() {
        Context context = getContext();
        if (context != null) {
            // Xóa thông tin lưu trữ (Token, UserID...)
            // Bạn hãy kiểm tra tên file SharedPreferences ("AuthPrefs" hay tên khác) cho đúng với project của bạn
            SharedPreferences prefs = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.clear(); // Xóa sạch dữ liệu đăng nhập
            editor.apply();

            Toast.makeText(context, "Đã đăng xuất thành công.", Toast.LENGTH_SHORT).show();

            // Chuyển về màn hình Đăng nhập
            Intent intent = new Intent(context, DangNhap.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}