package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class DangNhap extends AppCompatActivity {

    private ImageView imgLogoDangNhap, imgShowHidePassword;
    private EditText edtEmailDangNhap;
    private TextInputEditText matKhauDangNhapTextInputEditText;
    private TextView nutSignInDangNhapTextView, tvRegisterDangNhap, tvResetDangNhap;
    private LinearLayout btnGoogleDangNhap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        initUI();



        // ======= SIGN IN =======
        nutSignInDangNhapTextView.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, MatKhauSai.class);
            startActivity(i);
        });

        // ======= GOOGLE SIGN IN =======
        btnGoogleDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, MainActivity.class);
            startActivity(i);
        });

        // ======= REGISTER =======
        tvRegisterDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, ManDangKy.class);
            startActivity(i);
        });

        // ======= RESET PASSWORD =======
        tvResetDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, GuiMaXacNhan.class);
            startActivity(i);
        });



    }

    private void initUI() {
        imgLogoDangNhap = findViewById(R.id.imgLogoDangNhap);
        edtEmailDangNhap = findViewById(R.id.edtEmailDangNhap);
        matKhauDangNhapTextInputEditText = findViewById(R.id.matKhauDangNhapTextInputEditText);
        imgShowHidePassword = findViewById(R.id.imgShowHidePassword);
        nutSignInDangNhapTextView = findViewById(R.id.nutDangnhapvSignInTextView);
        btnGoogleDangNhap = findViewById(R.id.btnGoogleDangNhap);
        tvRegisterDangNhap = findViewById(R.id.tvRegisterDangNhap);
        tvResetDangNhap = findViewById(R.id.tvResetDangNhap);
    }
}
