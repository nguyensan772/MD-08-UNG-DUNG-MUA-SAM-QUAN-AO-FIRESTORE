package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.Address;
import com.example.md_08_ungdungfivestore.models.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * API Service cho Địa chỉ
 */
public interface AddressApiService {

    /**
     * Lấy danh sách địa chỉ của user
     */
    @GET("api/addresses")
    Call<ApiResponse<List<Address>>> getAddresses();

    /**
     * Thêm địa chỉ mới
     */
    @POST("api/addresses")
    Call<ApiResponse<Address>> addAddress(@Body Address address);

    /**
     * Cập nhật địa chỉ
     */
    @PUT("api/addresses/{id}")
    Call<ApiResponse<Address>> updateAddress(
            @Path("id") String addressId,
            @Body Address address);

    /**
     * Xóa địa chỉ
     */
    @DELETE("api/addresses/{id}")
    Call<ApiResponse<Void>> deleteAddress(@Path("id") String addressId);

    /**
     * Đặt địa chỉ mặc định
     */
    @PUT("api/addresses/{id}/set-default")
    Call<ApiResponse<Address>> setDefaultAddress(@Path("id") String addressId);
}
