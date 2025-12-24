package com.example.md_08_ungdungfivestore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.ChangePasswordRequest;
import com.example.md_08_ungdungfivestore.models.ChangePasswordResponse;
import com.example.md_08_ungdungfivestore.services.ApiClientCaNhan;
import com.example.md_08_ungdungfivestore.services.UserApiService;
import com.example.md_08_ungdungfivestore.utils.AuthManager; // Giả sử bạn có class này, nếu không thì dùng code SharedPreferences bên dưới
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoiMatKhauActivity extends AppCompatActivity {

    private TextInputEditText edtOldPass, edtNewPass, edtReNewPass;
    private Button btnSave;
    private ImageView btnBack;
    private UserApiService userApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        // Khởi tạo Service
        userApiService = ApiClientCaNhan.getUserApiService(this);

        anhXa();
        setupListeners();
    }

    private void anhXa() {
        edtOldPass = findViewById(R.id.edtOldPass);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtReNewPass = findViewById(R.id.edtReNewPass);
        btnSave = findViewById(R.id.btnLuuThayDoi);
        btnBack = findViewById(R.id.btnBackDoiMatKhau);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String oldPass = edtOldPass.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String reNewPass = edtReNewPass.getText().toString().trim();

            if (validateInputs(oldPass, newPass, reNewPass)) {
                changePassword(oldPass, newPass);
            }
        });
    }

    private boolean validateInputs(String oldPass, String newPass, String rePass) {
        if (oldPass.isEmpty() || newPass.isEmpty() || rePass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Server yêu cầu tối thiểu 6 ký tự
        if (newPass.length() < 6) {
            edtNewPass.setError("Mật khẩu mới phải từ 6 ký tự trở lên");
            edtNewPass.requestFocus();
            return false;
        }
        if (!newPass.equals(rePass)) {
            edtReNewPass.setError("Mật khẩu nhập lại không khớp");
            edtReNewPass.requestFocus();
            return false;
        }
        return true;
    }

    private void changePassword(String oldPass, String newPass) {
        // Tạo request body
        ChangePasswordRequest request = new ChangePasswordRequest(oldPass, newPass);

        // Gọi API
        userApiService.changePassword(request).enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChangePasswordResponse> call, @NonNull Response<ChangePasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 1. Thông báo thành công
                    Toast.makeText(DoiMatKhauActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();

                    // 2. Cập nhật Token mới (Quan trọng: để user không bị lỗi xác thực ở các thao tác sau)
                    String newToken = response.body().getToken();
                    if (newToken != null && !newToken.isEmpty()) {
                        saveNewToken(newToken);
                    }

                    // 3. Đóng màn hình
                    finish();
                } else {
                    // Xử lý lỗi từ Server (Ví dụ: Mật khẩu cũ sai)
                    try {
                        String errorMsg = "Đổi mật khẩu thất bại";
                        if (response.errorBody() != null) {
                            // Parse JSON lỗi từ server để lấy message
                            String errorStr = response.errorBody().string();
                            Gson gson = new Gson();
                            JsonObject errorJson = gson.fromJson(errorStr, JsonObject.class);
                            if (errorJson.has("message")) {
                                errorMsg = errorJson.get("message").getAsString();
                            }
                        }
                        Toast.makeText(DoiMatKhauActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(DoiMatKhauActivity.this, "Lỗi không xác định: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChangePasswordResponse> call, @NonNull Throwable t) {
                Toast.makeText(DoiMatKhauActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DoiMatKhau", "API Failure", t);
            }
        });
    }

    // Hàm lưu token mới vào SharedPreferences
    private void saveNewToken(String token) {
        // Sửa "AuthPrefs" thành tên file SharedPreferences bạn đang dùng trong project
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("jwt_token", token); // Key phải khớp với key bạn dùng lúc Login
        editor.apply();
        Log.d("DoiMatKhau", "Đã cập nhật token mới");
    }
}