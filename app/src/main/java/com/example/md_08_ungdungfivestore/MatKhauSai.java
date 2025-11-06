package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MatKhauSai extends AppCompatActivity {

    private ImageButton BtnQuayLaiSMK;
    private EditText edtEmail;
    private EditText edtMatKhau;
    private ImageView imgShowHidePassword;
    private TextView nutDangNhapSaiMKTextView, tvRegister, tvReset;

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

        initUI();

        // ✅ Nút quay lại → quay về màn đăng nhập
        BtnQuayLaiSMK.setOnClickListener(v -> {
            finish(); // quay lại màn trước
        });

        // ✅ Toggle show/hide mật khẩu
        imgShowHidePassword.setOnClickListener(v -> togglePassword());

        // ✅ Nhấn Sign In → sang màn chính (MainActivity)
        nutDangNhapSaiMKTextView.setOnClickListener(v -> {
            Intent i = new Intent(MatKhauSai.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        // ✅ Register → sang màn đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent i = new Intent(MatKhauSai.this, ManDangKy.class);
            startActivity(i);
        });

        // ✅ Reset password → sang màn Reset
        tvReset.setOnClickListener(v -> {
            Intent i = new Intent(MatKhauSai.this, MatKhauMoi.class);
            startActivity(i);
        });
    }

    private void initUI() {
        BtnQuayLaiSMK = findViewById(R.id.BtnQuayLaiSMK);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.matKhauDangNhapSaiMKTextInputEditText);
        imgShowHidePassword = findViewById(R.id.imgShowHidePassword);
        nutDangNhapSaiMKTextView = findViewById(R.id.nutDangNhapSaiMKTextView);
        tvRegister = findViewById(R.id.tvRegister);
        tvReset = findViewById(R.id.tvReset);
    }

    private void togglePassword() {
        if (isPasswordVisible) {
            edtMatKhau.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT |
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
            imgShowHidePassword.setImageResource(R.drawable.mat); // icon mắt đóng
        } else {
            edtMatKhau.setInputType(
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            imgShowHidePassword.setImageResource(R.drawable.mat); // icon mắt mở
        }
        edtMatKhau.setSelection(edtMatKhau.length());
        isPasswordVisible = !isPasswordVisible;
    }
}

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
