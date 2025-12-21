package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.ChangePasswordRequest;
import com.example.md_08_ungdungfivestore.models.ChangePasswordResponse;
import com.example.md_08_ungdungfivestore.models.ProfileUpdateResponse;
import com.example.md_08_ungdungfivestore.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApiService {

    /**
     * Lấy thông tin cá nhân.
     * Route: GET /api/user/me
     * ⭐ ĐÃ SỬA: Đổi tên phương thức thành getMe() ⭐
     */
    @GET("/api/user/me")
    Call<User> getMe(); // Đã đổi tên từ getUserDetails()

    /**
     * Cập nhật thông tin cá nhân.
     * Route: PUT /api/user/update-profile
     */
    @PUT("/api/user/update-profile")
    Call<ProfileUpdateResponse> updateUserDetails(@Body User user);

    /**
     * Đổi mật khẩu.
     * Route: PUT /api/user/change-password
     */
    @PUT("/api/user/change-password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest request);
}