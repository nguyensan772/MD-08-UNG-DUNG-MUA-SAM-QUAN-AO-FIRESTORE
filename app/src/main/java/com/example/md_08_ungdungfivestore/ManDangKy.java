package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.RegisterRequest;
import com.example.md_08_ungdungfivestore.models.RegisterResponse;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManDangKy extends AppCompatActivity {

    EditText edtTen, edtEmail;
    TextInputEditText edtMatKhau, edtNhapLaiMatKhau;
    TextView nutDangKyTextView, dangNhapTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_dang_ky);

        // Khai báo đúng kiểu dữ liệu
        edtTen = findViewById(R.id.tenDangKyEditText);
        edtEmail = findViewById(R.id.edtEmailDangNhap);
        edtMatKhau = findViewById(R.id.matKhauTextInputEditText);
        edtNhapLaiMatKhau = findViewById(R.id.nhapLaiMatKhauTextInputEditText);

        nutDangKyTextView = findViewById(R.id.nutDangKyTextView);
        dangNhapTextView = findViewById(R.id.dangNhapTextView);

        // Chuyển sang màn đăng nhập
        dangNhapTextView.setOnClickListener(v -> {
            startActivity(new Intent(ManDangKy.this, DangNhap.class));
            finish();
        });

        // Nút đăng ký
        nutDangKyTextView.setOnClickListener(v -> {
            if(validateInputs()) {
                String ten = edtTen.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String matKhau = edtMatKhau.getText().toString().trim();
                dangKyTaoOtp(ten, email, matKhau);
            }
        });
    }

    // ======= Hàm validate dữ liệu =======
    private boolean validateInputs() {
        String ten = edtTen.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String nhapLaiMatKhau = edtNhapLaiMatKhau.getText().toString().trim();

        // 1. Kiểm tra tên
        if(ten.isEmpty()) {
            edtTen.setError("Vui lòng nhập tên");
            edtTen.requestFocus();
            return false;
        }

        // Kiểm tra tên chỉ gồm chữ và khoảng trắng
        if(!ten.matches("[a-zA-Z\\s]+")) {
            edtTen.setError("Tên chỉ được chứa chữ và khoảng trắng, không được có số hoặc ký tự đặc biệt");
            edtTen.requestFocus();
            return false;
        }

        // 2. Kiểm tra email
        if(email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return false;
        }
        // email phải có (@ và .)
        if(!email.contains("@") || !email.contains(".")) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return false;
        }

        // 3. Kiểm tra mật khẩu
        if(matKhau.isEmpty()) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return false;
        }
        //mật khẩu phải lớn hơn 6 ký tự
        if(matKhau.length() < 6) {
            edtMatKhau.setError("Mật khẩu phải từ 6 ký tự trở lên");
            edtMatKhau.requestFocus();
            return false;
        }
        //mật khẩu phải có chữ và số
        if(!matKhau.matches(".*[A-Za-z].*") || !matKhau.matches(".*\\d.*")) {
            edtMatKhau.setError("Mật khẩu phải bao gồm cả chữ và số");
            edtMatKhau.requestFocus();
            return false;
        }

        // 4. Kiểm tra nhập lại mật khẩu
        if(!matKhau.equals(nhapLaiMatKhau)) {
            edtNhapLaiMatKhau.setError("Mật khẩu nhập lại không khớp");
            edtNhapLaiMatKhau.requestFocus();
            return false;
        }

        return true; // tất cả hợp lệ
    }

    // ======= Gọi API tạo OTP =======
    private void dangKyTaoOtp(String ten, String email, String matKhau) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        RegisterRequest request = new RegisterRequest(ten, email, matKhau);

        Call<RegisterResponse> call = apiService.register(request);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ManDangKy.this, "OTP đã được gửi. Kiểm tra console để lấy mã", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ManDangKy.this, OtpActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("full_name", ten);
                    intent.putExtra("password", matKhau);
                    startActivity(intent);
                } else {
                    Toast.makeText(ManDangKy.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(ManDangKy.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
