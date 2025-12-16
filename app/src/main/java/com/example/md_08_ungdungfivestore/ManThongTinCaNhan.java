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
import com.google.gson.Gson; // ⭐ IMPORT MỚI: Dùng để chuyển đổi đối tượng thành JSON

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManThongTinCaNhan extends AppCompatActivity {

    private static final String TAG = "ManThongTinCaNhan";
    private final Gson gson = new Gson(); // ⭐ KHAI BÁO GSON

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
                    Log.d(TAG, "User profile loaded: " + gson.toJson(currentUser)); // Log lúc tải
                    displayUserProfile(currentUser);
                } else {
                    Toast.makeText(ManThongTinCaNhan.this, "Không thể tải hồ sơ. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Lỗi tải hồ sơ: " + (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));
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

        // Thông tin cơ bản
        edtFullName.setText(user.getFullName());
        edtPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        edtEmail.setText(user.getEmail());

        // Thông tin địa chỉ (Dùng các trường phẳng trực tiếp từ User)
        // Kiểm tra null để tránh lỗi (dù khả năng thấp)
        edtProvince.setText(user.getProvince() != null ? user.getProvince() : "");
        edtDistrict.setText(user.getDistrict() != null ? user.getDistrict() : "");
        edtWard.setText(user.getWard() != null ? user.getWard() : "");
        edtStreet.setText(user.getStreet() != null ? user.getStreet() : "");
    }

    // 3. Gửi yêu cầu cập nhật thông tin
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

        if (newFullName.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(this, "Tên và Số điện thoại không được để trống.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật các trường mới lên đối tượng currentUser
        currentUser.setFullName(newFullName);
        currentUser.setPhoneNumber(newPhone);
        currentUser.setProvince(newProvince);
        currentUser.setDistrict(newDistrict);
        currentUser.setWard(newWard);
        currentUser.setStreet(newStreet);

        // Log đối tượng gửi đi (Request Body)
        Log.d(TAG, "JSON Request Body: " + gson.toJson(currentUser));

        // 3. Gọi API
        showLoading(true);
        userApiService.updateUserDetails(currentUser).enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileUpdateResponse> call, @NonNull Response<ProfileUpdateResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {

                    // ⭐ LOG QUAN TRỌNG: Dữ liệu Server trả về sau khi cập nhật ⭐
                    currentUser = response.body().getUser();
                    Log.d(TAG, "SERVER RESPONSE (User data): " + gson.toJson(currentUser));

                    Toast.makeText(ManThongTinCaNhan.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    displayUserProfile(currentUser);
                } else {
                    // ... (Xử lý lỗi)
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileUpdateResponse> call, @NonNull Throwable t) {
                // ... (Xử lý lỗi)
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnUpdateProfile.setEnabled(!isLoading);
    }
}