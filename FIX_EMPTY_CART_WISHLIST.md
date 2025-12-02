# ❌ VẤN ĐỀ: Cart & Wishlist đang load trống

## Nguyên nhân:

### 1. Wishlist đang dùng LOCAL STORAGE
File: `XemChiTiet.java` line 118
```java
// ❌ ĐANG DÙNG LOCAL - KHÔNG ĐỒNG BỘ VỚI SERVER
favoriteManager.addFavorite(product);
```

**Vấn đề:** 
- `FavoriteManager` lưu vào SharedPreferences (local)
- `YeuThichFragment` load từ API server
- → Không đồng bộ → Luôn trống!

### 2. Cart chưa implement API
File: `XemChiTiet.java` line 139
```java
// TODO: Xử lý đặt hàng hoặc thêm vào giỏ hàng
```

**Vấn đề:**
- Chưa có code gọi `CartApiService.addToCart()`
- → Không thêm được vào giỏ → Luôn trống!

---

## ✅ GIẢI PHÁP:

### Option 1: Sửa XemChiTiet để dùng API (RECOMMENDED)

#### A. Update Wishlist Button (line 108-130)

**Thay thế:**
```java
private void setupFavoriteButton() {
    btnFavorite.setOnClickListener(v -> {
        btnFavorite.animate()
                .scaleX(1.3f).scaleY(1.3f).setDuration(120)
                .withEndAction(() ->
                        btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                ).start();

        if (!isFavorite) {
            // ✅ GỌI API THÊM VÀO WISHLIST
            addToWishlistAPI(product.getId());
        } else {
            // ✅ GỌI API XÓA KHỎI WISHLIST
            removeFromWishlistAPI(product.getId());
        }
    });
}

// ✅ THÊM METHOD MỚI
private void addToWishlistAPI(String productId) {
    WishlistApiService service = ApiClient.getClient().create(WishlistApiService.class);
    
    Map<String, String> body = new HashMap<>();
    body.put("productId", productId);
    
    service.addToWishlist(body).enqueue(new Callback<ApiResponse<WishlistItem>>() {
        @Override
        public void onResponse(Call<ApiResponse<WishlistItem>> call, Response<ApiResponse<WishlistItem>> response) {
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.heart_filled);
                Toast.makeText(XemChiTiet.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(XemChiTiet.this, "Không thể thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<WishlistItem>> call, Throwable t) {
            Toast.makeText(XemChiTiet.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}

private void removeFromWishlistAPI(String productId) {
    WishlistApiService service = ApiClient.getClient().create(WishlistApiService.class);
    
    service.removeFromWishlist(productId).enqueue(new Callback<ApiResponse<Void>>() {
        @Override
        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
            if (response.isSuccessful()) {
                isFavorite = false;
                btnFavorite.setImageResource(R.drawable.heart_empty);
                Toast.makeText(XemChiTiet.this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
            Toast.makeText(XemChiTiet.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

**Thêm imports:**
```java
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.WishlistItem;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.WishlistApiService;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
```

#### B. Update Add to Cart (line 132-143)

**Thay thế:**
```java
private void openSelectOptionsBottomSheet() {
    if (product == null) return;

    SelectOptionsBottomSheetFragment bottomSheet = new SelectOptionsBottomSheetFragment(product, (size, color, quantity) -> {
        // ✅ GỌI API THÊM VÀO GIỎ HÀNG
        addToCartAPI(product, size, color, quantity);
    });

    bottomSheet.show(getSupportFragmentManager(), "SelectOptionsBottomSheet");
}

// ✅ THÊM METHOD MỚI
private void addToCartAPI(Product product, String size, String color, int quantity) {
    CartApiService service = ApiClient.getClient().create(CartApiService.class);
    
    AddToCartRequest request = new AddToCartRequest(
        product.getId(),
        product.getName(),
        product.getImage(),
        size,
        color,
        quantity,
        product.getPrice()
    );
    
    service.addToCart(request).enqueue(new Callback<ApiResponse<Cart>>() {
        @Override
        public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                Toast.makeText(XemChiTiet.this, 
                    "Đã thêm vào giỏ hàng: Size " + size + ", Màu " + color + ", SL: " + quantity, 
                    Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(XemChiTiet.this, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
            Toast.makeText(XemChiTiet.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

**Thêm imports:**
```java
import com.example.md_08_ungdungfivestore.models.AddToCartRequest;
import com.example.md_08_ungdungfivestore.models.Cart;
import com.example.md_08_ungdungfivestore.services.CartApiService;
```

---

### Option 2: Migrate dữ liệu từ FavoriteManager sang API

Nếu user đã có wishlist local, cần migrate:

```java
// Trong onCreate() của MainActivity hoặc Application
private void migrateFavorites() {
    FavoriteManager favoriteManager = new FavoriteManager(this);
    List<Product> localFavorites = favoriteManager.getAllFavorites();
    
    WishlistApiService service = ApiClient.getClient().create(WishlistApiService.class);
    
    for (Product product : localFavorites) {
        Map<String, String> body = new HashMap<>();
        body.put("productId", product.getId());
        service.addToWishlist(body).enqueue(/* callback */);
    }
    
    // Clear local sau khi migrate
    favoriteManager.clearAll();
}
```

---

## 🎯 TÓM TẮT:

**Để Cart & Wishlist hoạt động:**

1. ✅ Sửa `XemChiTiet.java`:
   - Wishlist button → Gọi `WishlistApiService`
   - Add to Cart → Gọi `CartApiService`

2. ✅ Remove `FavoriteManager` (hoặc chỉ dùng cho cache)

3. ✅ Test flow:
   - Vào product detail
   - Nhấn nút tim → Thêm vào wishlist
   - Nhấn Add to Cart → Chọn size/color → Thêm vào giỏ
   - Vào tab Wishlist/Cart → Thấy data!

**Files cần sửa:**
- `XemChiTiet.java` - Thêm API calls
- (Optional) Remove `FavoriteManager.java` nếu không dùng nữa
