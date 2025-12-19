package com.example.md_08_ungdungfivestore.services;

// ✅ THÊM DÒNG IMPORT NÀYimport com.example.md_08_ungdungfivestore.models.ApiResponse;

import com.example.md_08_ungdungfivestore.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApiService {

    // 🔍 Search sản phẩm
    @GET("api/products/search")
    Call<List<Product>> searchProducts(@Query("name") String keyword);

    // 📋 Danh sách sản phẩm (có thể lọc)
    @GET("api/products")
    Call<List<Product>> getAllProducts();

    @GET("api/products")
    Call<List<Product>> getFilteredProducts(@Query("category") String category, @Query("brand") String brand);

    // 🆕 Sản phẩm mới nhất
    @GET("api/products/newest")
    Call<List<Product>> getNewestProducts();

    // 🔗 Sản phẩm liên quan theo category
    @GET("api/products/related/by-category")
    Call<List<Product>> getRelatedProductsByCategory(@Query("category") String category);

    // 📄 Chi tiết sản phẩm theo id
    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    // ➕ Thêm sản phẩm (POST)
    @POST("api/products")
    Call<Product> createProduct(@Body Product product);

    // ✏️ Cập nhật sản phẩm
    @PUT("api/products/{id}")
    Call<Product> updateProduct(@Path("id") String id, @Body Product product);

    // ❌ Xóa sản phẩm
    @DELETE("api/products/{id}")
    Call<Void> deleteProduct(@Path("id") String id);

    // ⭐ Toggle featured
    @PUT("api/products/{id}/featured")
    Call<Product> toggleFeatured(@Path("id") String id);

    // Đánh Giá
    // Thêm method này vào interface ProductApiService hoặc OrderApiService tùy backend của bạn

}
