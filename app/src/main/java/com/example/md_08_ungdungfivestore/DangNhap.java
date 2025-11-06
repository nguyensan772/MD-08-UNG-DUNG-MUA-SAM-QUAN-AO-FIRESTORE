package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;



public class DangNhap extends AppCompatActivity {

    // Ánh xạ
    private EditText edtEmailDangNhap, edtMatKhau;
    private ImageView imgShowHidePassword;
    private TextView nutDangNhap, tvRegisterDangNhap, tvResetDangNhap;
    private LinearLayout btnGoogleDangNhap;

    // Biến kiểm tra show/hide password
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        // Gọi hàm ánh xạ
        initUI();

        // Xử lý show/hide mật khẩu


        // Xử lý đăng nhập
        nutDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, MatKhauSai.class);
            startActivity(i);
        });


        // Click Register
        tvRegisterDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, ManDangKy.class);
            startActivity(i);
        });

        // Click Reset Password
        tvResetDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, GuiMaXacNhan.class);
            startActivity(i);
        });

        // Click Google Sign In
        btnGoogleDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, MainActivity.class);
            startActivity(i);
        });

    }

    private void initUI() {
        edtEmailDangNhap = findViewById(R.id.edtEmailDangNhap);
        edtMatKhau = findViewById(R.id.matKhauDangNhapTextInputEditText);
        imgShowHidePassword = findViewById(R.id.imgShowHidePassword);
        nutDangNhap = findViewById(R.id.nutDangnhapTextView);
        tvRegisterDangNhap = findViewById(R.id.tvRegisterDangNhap);
        tvResetDangNhap = findViewById(R.id.tvResetDangNhap);
        btnGoogleDangNhap = findViewById(R.id.btnGoogleDangNhap);
    }

}
