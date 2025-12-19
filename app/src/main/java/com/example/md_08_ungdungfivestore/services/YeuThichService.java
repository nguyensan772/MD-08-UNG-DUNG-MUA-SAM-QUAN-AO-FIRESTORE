package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.CheckResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface YeuThichService {

    @GET("wishlist/check/{productId}")
    Call<CheckResponse> checkWishlist(@Path("productId") String productId);

    @POST("wishlist/add")
    Call<Map<String, Object>> addToWishlist(@Body Map<String, String> body);

    @DELETE("wishlist/remove/{productId}")
    Call<Map<String, Object>> removeFromWishlist(@Path("productId") String productId);

    @GET("wishlist/my")
    Call<Map<String, Object>> getMyWishlist();
}
