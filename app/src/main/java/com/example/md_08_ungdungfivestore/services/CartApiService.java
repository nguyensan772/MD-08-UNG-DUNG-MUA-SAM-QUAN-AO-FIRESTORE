package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.AddToCartRequest;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Cart;
import com.example.md_08_ungdungfivestore.models.UpdateCartRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * API Service cho Giỏ hàng
 */
public interface CartApiService {

    /**
     * Lấy giỏ hàng của user
     */
    @GET("api/cart")
    Call<ApiResponse<Cart>> getCart();

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @POST("api/cart")
    Call<ApiResponse<Cart>> addToCart(@Body AddToCartRequest request);

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    @PUT("api/cart/{id}")
    Call<ApiResponse<Cart>> updateCartItem(
            @Path("id") String cartItemId,
            @Body UpdateCartRequest request);

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @DELETE("api/cart/{id}")
    Call<ApiResponse<Cart>> removeCartItem(@Path("id") String cartItemId);

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @DELETE("api/cart")
    Call<ApiResponse<Void>> clearCart();
}
