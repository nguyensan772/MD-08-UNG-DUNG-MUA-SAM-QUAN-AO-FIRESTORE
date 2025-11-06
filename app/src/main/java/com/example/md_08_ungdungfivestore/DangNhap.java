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



        // Xử lý show/hide mật khẩu


        // Xử lý đăng nhập
        nutDangNhap.setOnClickListener(v -> {
            Intent i = new Intent(DangNhap.this, MatKhauSai.class);
            startActivity(i);

        imgLogoDangNhap = findViewById(R.id.imgLogoDangNhap);
        edtEmailDangNhap = findViewById(R.id.edtEmailDangNhap);
        matKhauDangNhapTextInputEditText = findViewById(R.id.matKhauDangNhapTextInputEditText);
        imgShowHidePassword = findViewById(R.id.imgShowHidePassword);
        nutSignInDangNhapTextView = findViewById(R.id.nutSignInDangNhapTextView);
        btnGoogleDangNhap = findViewById(R.id.btnGoogleDangNhap);
        tvRegisterDangNhap = findViewById(R.id.tvRegisterDangNhap);
        tvResetDangNhap = findViewById(R.id.tvResetDangNhap);


        imgShowHidePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Ẩn mật khẩu
                matKhauDangNhapTextInputEditText.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                imgShowHidePassword.setImageResource(R.drawable.mat); // icon con mắt đóng
                isPasswordVisible = false;
            } else {
                // Hiện mật khẩu
                matKhauDangNhapTextInputEditText.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                imgShowHidePassword.setImageResource(R.drawable.mat); // icon con mắt mở
                isPasswordVisible = true;
            }
            // Đưa con trỏ về cuối dòng
            matKhauDangNhapTextInputEditText.setSelection(
                    matKhauDangNhapTextInputEditText.getText().length()
            );
        });

        // ======= NÚT SIGN IN =======
        nutSignInDangNhapTextView.setOnClickListener(v -> {
            String email = edtEmailDangNhap.getText().toString().trim();
            String password = matKhauDangNhapTextInputEditText.getText().toString().trim();
            Intent intent = new Intent(DangNhap.this, MatKhauSai.class);
            startActivity(intent);

        });

        // ======= NÚT GOOGLE SIGN IN =======
        btnGoogleDangNhap.setOnClickListener(v -> {

            Intent intent=new Intent(DangNhap.this,DangNhap.class);
            startActivity(intent);
        });

        // ======= NÚT ĐĂNG KÝ =======
        tvRegisterDangNhap.setOnClickListener(v -> {});

        // ======= NÚT QUÊN MẬT KHẨU =======
        tvResetDangNhap.setOnClickListener(v -> {
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
