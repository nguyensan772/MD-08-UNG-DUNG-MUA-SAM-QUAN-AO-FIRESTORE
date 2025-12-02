package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.WishlistItem;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * API Service cho Wishlist (Danh sách yêu thích)
 */
public interface WishlistApiService {

    /**
     * Lấy danh sách yêu thích của user
     */
    @GET("api/wishlists/me")
    Call<ApiResponse<List<WishlistItem>>> getWishlists();

    /**
     * Thêm sản phẩm vào wishlist
     */
    @POST("api/wishlists")
    Call<ApiResponse<WishlistItem>> addToWishlist(@Body Map<String, String> body);

    /**
     * Xóa sản phẩm khỏi wishlist (dùng productId)
     */
    @DELETE("api/wishlists/{productId}")
    Call<ApiResponse<Void>> removeFromWishlist(@Path("productId") String productId);

    /**
     * Kiểm tra sản phẩm có trong wishlist không
     */
    @GET("api/wishlists/check/{productId}")
    Call<ApiResponse<Map<String, Boolean>>> checkWishlist(@Path("productId") String productId);
}
