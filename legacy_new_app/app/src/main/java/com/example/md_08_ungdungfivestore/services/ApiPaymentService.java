package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.OrderRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiPaymentService {


    @POST("api/orders/vnpay-order")
    Call<VNPayResponse> createVNPayOrder(@Body OrderRequest request);
}