package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull; // Thêm import này
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Sử dụng LinearLayout cho nút

import com.example.md_08_ungdungfivestore.ManDonHang;
import com.example.md_08_ungdungfivestore.ManThongTinCaNhan; // ⭐ CẦN TẠO ACTIVITY NÀY ⭐
import com.example.md_08_ungdungfivestore.R;

public class TrangCaNhanFragment extends Fragment {

    private LinearLayout btnDonHang;
    private LinearLayout btnThongTinCaNhan; // ⭐ Ánh xạ nút Thông tin cá nhân

    public TrangCaNhanFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trang_ca_nhan, container, false);

        // Ánh xạ các nút
        btnDonHang = view.findViewById(R.id.btnDonHang);
        btnThongTinCaNhan = view.findViewById(R.id.btnThongTinCaNhan); // ⭐ Ánh xạ ID từ layout bạn gửi

        // Xử lý sự kiện click cho nút Đơn hàng
        btnDonHang.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManDonHang.class);
            startActivity(intent);
        });

        // ⭐ Xử lý sự kiện click cho nút Thông tin cá nhân ⭐
        btnThongTinCaNhan.setOnClickListener(v -> {
            // Khởi chạy Activity chỉnh sửa thông tin cá nhân
            Intent intent = new Intent(getActivity(), ManThongTinCaNhan.class);
            startActivity(intent);
        });

        // ⭐ Bạn cũng có thể thêm logic cho btnDangXuat và btnLienHe tại đây ⭐

        return view;
    }
}