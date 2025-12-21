package com.example.md_08_ungdungfivestore.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.md_08_ungdungfivestore.R;

public class DialogDangNhap extends DialogFragment {
    // 1. Định nghĩa Interface để bắn sự kiện ra ngoài
    public interface OnDialogAction {
        void onConfirm();
        void onCancel();
    }


    private String tieuDe;
    private String noiDung;
    private OnDialogAction actionListener;

    public static DialogDangNhap newInstance(String tieuDe, String noiDung, OnDialogAction listener) {
        DialogDangNhap fragment = new DialogDangNhap();
        fragment.tieuDe =tieuDe;
        fragment.noiDung = noiDung;
        fragment.actionListener = listener;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bat_dang_nhap,container,false);
        // Làm nền trong suốt và bo góc (nếu cần)
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        // Ánh xạ View
        TextView tvTieuDe = view.findViewById(R.id.tvTieuDeDialogDangNhap);
        TextView tvNoiDung = view.findViewById(R.id.tvNoiDungDialogDangNhap);
        Button btnDangNhap = view.findViewById(R.id.btnDangNhapDialogDangNhap);
        Button btnHuy = view.findViewById(R.id.btnHuyDialogDangNhap);

        // Gán dữ liệu
        tvTieuDe.setText(tieuDe);
        tvNoiDung.setText(noiDung);

        // Xử lý sự kiện click
        btnDangNhap.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onConfirm();
            dismiss(); // Đóng dialog
        });

        btnHuy.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onCancel();
            dismiss(); // Đóng dialog
        });


        return view;
    }
}
