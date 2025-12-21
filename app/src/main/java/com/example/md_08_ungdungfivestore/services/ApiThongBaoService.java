package com.example.md_08_ungdungfivestore.services;


import com.example.md_08_ungdungfivestore.models.Notification;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiThongBaoService {

    // 1. Lấy danh sách thông báo của người dùng hiện tại
    @GET("api/notifications/my-notifications")
    Call<List<Notification>> getDanhSachThongBao();

    // 2. Đánh dấu tất cả thông báo là đã đọc (Khi người dùng nhấn vào nút "Đọc tất cả")
    @PUT("api/notifications/mark-all-read")
    Call<ResponseBody> danhDauDaDocHet();

    // 3. Đánh dấu một thông báo cụ thể là đã đọc (Khi người dùng click vào 1 item)
    @PUT("api/notifications/{id}/read")
    Call<ResponseBody> danhDauDaDocTheoId(@Path("id") String notificationId);
}