package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.OrderRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiPaymentService {

    // Gọi đến router.post("/vnpay-order", auth, orderController.createVNPayOrder);
    // Lưu ý: Nếu base URL của bạn đã có /api/orders/ thì chỉ cần "vnpay-order"
    @POST("api/orders/vnpay-order")
    Call<VNPayResponse> createVNPayOrder(@Body OrderRequest request);
}