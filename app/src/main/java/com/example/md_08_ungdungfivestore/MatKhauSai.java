package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MatKhauSai extends AppCompatActivity {

    private EditText edtEmail;
    private TextInputEditText matKhauDangNhapSaiMKTextInputEditText;
    private TextView nutDangNhapSaiMKTextView, tvRegister, tvReset;
    private ImageView imgShowHidePassword;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mat_khau_sai);

        // 🔹 Ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        matKhauDangNhapSaiMKTextInputEditText = findViewById(R.id.matKhauDangNhapSaiMKTextInputEditText);
        nutDangNhapSaiMKTextView = findViewById(R.id.nutDangNhapSaiMKTextView);
        tvRegister = findViewById(R.id.tvRegister);
        tvReset = findViewById(R.id.tvReset);
        imgShowHidePassword = findViewById(R.id.imgShowHidePassword);

        // 🔹 Khi bấm “Sign In” → quay lại màn đăng nhập
        nutDangNhapSaiMKTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MatKhauSai.this, DangNhap.class);
            startActivity(intent);
            finish();
        });

        // 🔹 Khi bấm “Register” → chuyển qua màn đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MatKhauSai.this, ManDangKy.class);
            startActivity(intent);
        });

        // 🔹 Khi bấm “Reset” → chuyển qua màn gửi mã xác nhận
        tvReset.setOnClickListener(v -> {
            Intent intent = new Intent(MatKhauSai.this, GuiMaXacNhan.class);
            startActivity(intent);
        });}
}