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

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        initUI();

        // ======= SHOW/HIDE PASSWORD =======
        imgShowHidePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                matKhauDangNhapTextInputEditText.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                imgShowHidePassword.setImageResource(R.drawable.mat);
                isPasswordVisible = false;
            } else {
                matKhauDangNhapTextInputEditText.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                imgShowHidePassword.setImageResource(R.drawable.mat);
                isPasswordVisible = true;
            }
            matKhauDangNhapTextInputEditText.setSelection(
                    matKhauDangNhapTextInputEditText.getText().length()
            );
        });

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
        nutSignInDangNhapTextView = findViewById(R.id.nutDangnhapSignInTextView);
        btnGoogleDangNhap = findViewById(R.id.btnGoogleDangNhap);
        tvRegisterDangNhap = findViewById(R.id.tvRegisterDangNhap);
        tvResetDangNhap = findViewById(R.id.tvResetDangNhap);
    }
}
