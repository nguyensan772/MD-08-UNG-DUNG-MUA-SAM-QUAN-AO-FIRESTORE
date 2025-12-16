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

import com.example.md_08_ungdungfivestore.DangNhap; // Màn hình Đăng nhập
import com.example.md_08_ungdungfivestore.ManThongTinCaNhan; // Màn hình Thông tin cá nhân
import com.example.md_08_ungdungfivestore.ManDonHang; // ⭐ Cần tạo/import Activity Đơn hàng ⭐
import com.example.md_08_ungdungfivestore.R;

// Giả định tên fragment của bạn
public class TrangCaNhanFragment extends Fragment {

    private LinearLayout btnDangXuat;
    private LinearLayout btnDonHang; // ⭐ Khai báo nút Đơn hàng ⭐
    private LinearLayout btnThongTinCaNhan; // Khai báo nút Thông tin cá nhân

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

        // 1. Ánh xạ các nút
        btnDangXuat = view.findViewById(R.id.btnDangXuat);
        btnThongTinCaNhan = view.findViewById(R.id.btnThongTinCaNhan);
        btnDonHang = view.findViewById(R.id.btnDonHang); // ⭐ Ánh xạ nút Đơn hàng ⭐

        // 2. Thiết lập sự kiện Click cho nút Đăng xuất
        btnDangXuat.setOnClickListener(v -> showLogoutConfirmationDialog());

        // 3. Xử lý click cho nút "Thông tin cá nhân"
        btnThongTinCaNhan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManThongTinCaNhan.class);
            startActivity(intent);
        });

        // ⭐ 4. XỬ LÝ CLICK CHO NÚT "ĐƠN HÀNG" ⭐
        btnDonHang.setOnClickListener(v -> {
            // ⭐ Chuyển đến Activity quản lý đơn hàng
            Intent intent = new Intent(getActivity(), ManDonHang.class);
            startActivity(intent);
        });

        // (Nếu có nút Liên hệ, bạn cũng có thể thêm ở đây)
    }

    /**
     * Hiển thị hộp thoại xác nhận Đăng xuất
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Gọi hàm xử lý đăng xuất khi người dùng đồng ý
                    logout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Thực hiện logic đăng xuất: Xóa Token và chuyển hướng
     */
    private void logout() {
        // 1. Xóa JWT Token và User ID khỏi SharedPreferences (Giả định lưu ở đây)
        Context context = getContext();
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.remove("jwt_token");
            editor.remove("userId");
            editor.apply();

            Toast.makeText(context, "Đã đăng xuất thành công.", Toast.LENGTH_SHORT).show();

            // 2. Chuyển về màn hình Đăng nhập (DangNhap)
            Intent intent = new Intent(context, DangNhap.class);
            // Xóa hết các Activity trước đó trong stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}