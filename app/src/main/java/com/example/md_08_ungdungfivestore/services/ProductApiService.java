package com.example.md_08_ungdungfivestore.services;


import com.example.md_08_ungdungfivestore.models.Product;
import java.util.List;

import retrofit2.http.*;
import retrofit2.Call;
import retrofit2.http.Query;

public interface ProductApiService {

    // 🔍 Search sản phẩm
    @GET("products/search")
    Call<List<Product>> searchProducts(@Query("q") String keyword);

    // 📋 Danh sách sản phẩm
    @GET("products")
    Call<List<Product>> getAllProducts();

    // 🆕 Sản phẩm mới nhất
    @GET("products/newest")
    Call<List<Product>> getNewestProducts();

    // 🔗 Sản phẩm liên quan theo category
    @GET("products/related/by-category")
    Call<List<Product>> getRelatedProductsByCategory(@Query("category") String category);

    // 📄 Chi tiết sản phẩm theo id
    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    // ➕ Thêm sản phẩm (POST)
    @POST("products")
    Call<Product> createProduct(@Body Product product);

    // ✏️ Cập nhật sản phẩm
    @PUT("products/{id}")
    Call<Product> updateProduct(@Path("id") String id, @Body Product product);

    // ❌ Xóa sản phẩm
    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") String id);

    // ⭐ Toggle featured
    @PUT("products/{id}/featured")
    Call<Product> toggleFeatured(@Path("id") String id);

//    // 📦 Nhập hàng (restock)
//    @POST("products/{id}/restock")
//    Call<Product> restockProduct(@Path("id") String id, @Body RestockRequest request);
}
