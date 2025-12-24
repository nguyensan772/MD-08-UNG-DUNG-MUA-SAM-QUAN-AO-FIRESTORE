package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.ProfileUpdateResponse;
import com.example.md_08_ungdungfivestore.models.User;
import com.example.md_08_ungdungfivestore.services.ApiClientCaNhan;
import com.example.md_08_ungdungfivestore.services.UserApiService;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManThongTinCaNhan extends AppCompatActivity {

    private static final String TAG = "ManThongTinCaNhan";
    private final Gson gson = new Gson();

    private EditText edtFullName, edtPhone, edtEmail, edtProvince, edtDistrict, edtWard, edtStreet;
    private Button btnUpdateProfile;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private UserApiService userApiService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_thong_tin_ca_nhan);

        anhXa();
        setupListeners();

        userApiService = ApiClientCaNhan.getUserApiService(this);
        fetchUserProfile();
    }

    private void anhXa() {
        edtFullName = findViewById(R.id.edtProfileFullName);
        edtPhone = findViewById(R.id.edtProfilePhone);
        edtEmail = findViewById(R.id.edtProfileEmail);
        edtProvince = findViewById(R.id.edtProfileProvince);
        edtDistrict = findViewById(R.id.edtProfileDistrict);
        edtWard = findViewById(R.id.edtProfileWard);
        edtStreet = findViewById(R.id.edtProfileStreet);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.profileProgressBar);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    // 1. Tải thông tin người dùng
    private void fetchUserProfile() {
        showLoading(true);
        userApiService.getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    Log.d(TAG, "User profile loaded: " + gson.toJson(currentUser));
                    displayUserProfile(currentUser);
                } else {
                    Toast.makeText(ManThongTinCaNhan.this, "Không thể tải hồ sơ. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    try {
                        Log.e(TAG, "Lỗi tải hồ sơ: " + (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(ManThongTinCaNhan.this, "Lỗi kết nối mạng.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Lỗi mạng khi tải hồ sơ: ", t);
            }
        });
    }

    // 2. Hiển thị thông tin lên UI
    private void displayUserProfile(User user) {
        if (user == null) return;

        edtFullName.setText(user.getFullName());
        edtPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        edtEmail.setText(user.getEmail()); // Email thường là readonly (không sửa được)

        edtProvince.setText(user.getProvince() != null ? user.getProvince() : "");
        edtDistrict.setText(user.getDistrict() != null ? user.getDistrict() : "");
        edtWard.setText(user.getWard() != null ? user.getWard() : "");
        edtStreet.setText(user.getStreet() != null ? user.getStreet() : "");
    }

    // 3. Gửi yêu cầu cập nhật thông tin (ĐÃ THÊM VALIDATE)
    private void updateProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "Không có dữ liệu hồ sơ để cập nhật.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Lấy dữ liệu mới từ EditText
        String newFullName = edtFullName.getText().toString().trim();
        String newPhone = edtPhone.getText().toString().trim();
        String newProvince = edtProvince.getText().toString().trim();
        String newDistrict = edtDistrict.getText().toString().trim();
        String newWard = edtWard.getText().toString().trim();
        String newStreet = edtStreet.getText().toString().trim();

        // ========== BẮT ĐẦU VALIDATE (KIỂM TRA) ==========

        // Kiểm tra Tên
        if (newFullName.isEmpty()) {
            edtFullName.setError("Vui lòng nhập họ và tên");
            edtFullName.requestFocus();
            return;
        }

        // Kiểm tra Số điện thoại (Không trống + Đúng định dạng VN)
        if (newPhone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }
        // Regex: 10 số và bắt đầu bằng 0
        if (newPhone.length() != 10 || !newPhone.startsWith("0")) {
            edtPhone.setError("Số điện thoại không hợp lệ (Phải có 10 số và bắt đầu bằng 0)");
            edtPhone.requestFocus();
            return;
        }

        // Kiểm tra Địa chỉ (Bắt buộc nhập đủ để giao hàng)
        if (newProvince.isEmpty()) {
            edtProvince.setError("Vui lòng nhập Tỉnh/Thành phố");
            edtProvince.requestFocus();
            return;
        }
        if (newDistrict.isEmpty()) {
            edtDistrict.setError("Vui lòng nhập Quận/Huyện");
            edtDistrict.requestFocus();
            return;
        }
        if (newWard.isEmpty()) {
            edtWard.setError("Vui lòng nhập Phường/Xã");
            edtWard.requestFocus();
            return;
        }
        if (newStreet.isEmpty()) {
            edtStreet.setError("Vui lòng nhập Tên đường/Số nhà");
            edtStreet.requestFocus();
            return;
        }

        // ========== KẾT THÚC VALIDATE ==========

        // Cập nhật các trường mới lên đối tượng currentUser
        currentUser.setFullName(newFullName);
        currentUser.setPhoneNumber(newPhone);
        currentUser.setProvince(newProvince);
        currentUser.setDistrict(newDistrict);
        currentUser.setWard(newWard);
        currentUser.setStreet(newStreet);

        Log.d(TAG, "JSON Request Body: " + gson.toJson(currentUser));

        // 3. Gọi API
        showLoading(true);
        userApiService.updateUserDetails(currentUser).enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileUpdateResponse> call, @NonNull Response<ProfileUpdateResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body().getUser();
                    Log.d(TAG, "SERVER RESPONSE (User data): " + gson.toJson(currentUser));

                    Toast.makeText(ManThongTinCaNhan.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    displayUserProfile(currentUser);
                } else {
                    // Xử lý lỗi từ server (Ví dụ: SĐT trùng lặp)
                    String msg = "Cập nhật thất bại (" + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ManThongTinCaNhan.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileUpdateResponse> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "Update Failure: " + t.getMessage());
                Toast.makeText(ManThongTinCaNhan.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnUpdateProfile.setEnabled(!isLoading);
    }
}