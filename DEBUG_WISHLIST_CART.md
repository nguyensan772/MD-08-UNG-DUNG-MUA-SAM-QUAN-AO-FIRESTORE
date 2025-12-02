# 🔍 DEBUG: Không thể thêm vào Wishlist/Cart

## Bước 1: Thêm Logging vào XemChiTiet

### A. Add logging cho Wishlist

Trong `addToWishlistAPI()`, thêm logs:

```java
private void addToWishlistAPI(String productId) {
    // ✅ LOG 1: Check productId
    Log.d("WISHLIST", "Adding product: " + productId);
    
    Map<String, String> body = new HashMap<>();
    body.put("productId", productId);
    
    // ✅ LOG 2: Check request body
    Log.d("WISHLIST", "Request body: " + body.toString());

    wishlistApiService.addToWishlist(body).enqueue(new Callback<ApiResponse<WishlistItem>>() {
        @Override
        public void onResponse(Call<ApiResponse<WishlistItem>> call, Response<ApiResponse<WishlistItem>> response) {
            // ✅ LOG 3: Check response
            Log.d("WISHLIST", "Response code: " + response.code());
            Log.d("WISHLIST", "Response body: " + response.body());
            
            if (response.isSuccessful() && response.body() != null) {
                // ✅ LOG 4: Check success field
                Log.d("WISHLIST", "Success: " + response.body().isSuccess());
                Log.d("WISHLIST", "Message: " + response.body().getMessage());
                
                if (response.body().isSuccess()) {
                    isFavorite = true;
                    btnFavorite.setImageResource(R.drawable.heart_filled);
                    Toast.makeText(XemChiTiet.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    // ✅ LOG 5: Server said not success
                    Log.e("WISHLIST", "Server error: " + response.body().getMessage());
                    Toast.makeText(XemChiTiet.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // ✅ LOG 6: Response not successful
                try {
                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                    Log.e("WISHLIST", "Error response: " + errorBody);
                    Toast.makeText(XemChiTiet.this, "Lỗi HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("WISHLIST", "Error reading error body", e);
                }
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<WishlistItem>> call, Throwable t) {
            // ✅ LOG 7: Network error
            Log.e("WISHLIST", "Network error: " + t.getMessage(), t);
            Toast.makeText(XemChiTiet.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

### B. Add logging cho Cart

Trong `addToCartAPI()`, thêm logs:

```java
private void addToCartAPI(Product product, String size, String color, int quantity) {
    // ✅ LOG 1: Check input
    Log.d("CART", "Product ID: " + product.getId());
    Log.d("CART", "Size: " + size + ", Color: " + color + ", Qty: " + quantity);
    
    AddToCartRequest request = new AddToCartRequest(
            product.getId(),
            product.getName(),
            product.getImage(),
            size,
            color,
            quantity,
            product.getPrice()
    );
    
    // ✅ LOG 2: Check request
    Log.d("CART", "Request created");

    cartApiService.addToCart(request).enqueue(new Callback<ApiResponse<Cart>>() {
        @Override
        public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
            // ✅ LOG 3: Check response
            Log.d("CART", "Response code: " + response.code());
            
            if (response.isSuccessful() && response.body() != null) {
                Log.d("CART", "Success: " + response.body().isSuccess());
                Log.d("CART", "Message: " + response.body().getMessage());
                
                if (response.body().isSuccess()) {
                    Toast.makeText(XemChiTiet.this,
                            "Đã thêm vào giỏ hàng: Size " + size + ", Màu " + color + ", SL: " + quantity,
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.e("CART", "Server error: " + response.body().getMessage());
                    Toast.makeText(XemChiTiet.this, "Lỗi: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                    Log.e("CART", "Error response: " + errorBody);
                    Toast.makeText(XemChiTiet.this, "Lỗi HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("CART", "Error reading error body", e);
                }
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
            Log.e("CART", "Network error: " + t.getMessage(), t);
            Toast.makeText(XemChiTiet.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

**Thêm import:**
```java
import android.util.Log;
```

---

## Bước 2: Check Server Logs

Mở terminal server, xem có log gì khi nhấn nút:

```bash
# Nên thấy:
POST /api/wishlists 200
POST /api/cart 200

# Hoặc lỗi:
POST /api/wishlists 400 (validation error)
POST /api/cart 404 (product not found)
```

---

## Bước 3: Common Issues

### Issue 1: Product ID = null
**Triệu chứng:** Logcat show `productId: null`

**Fix:** Check product có được pass đúng vào XemChiTiet không:
```java
// Trong activity trước đó
Intent intent = new Intent(this, XemChiTiet.class);
intent.putExtra("product", product); // ✅ Phải có dòng này
startActivity(intent);
```

### Issue 2: Token không được gửi
**Triệu chứng:** Server log show `401 Unauthorized`

**Fix:** Check `ApiClient.init()` đã được gọi:
```java
// Trong MainActivity.onCreate()
ApiClient.init(this);
```

### Issue 3: Server validation error
**Triệu chứng:** Response code 400, message "Thiếu thông tin..."

**Fix:** Check Product có đủ fields không:
- `id` (hoặc `_id`)
- `name`
- `image`
- `price`

### Issue 4: Product variations empty
**Triệu chứng:** Cart error "không đủ hàng trong kho"

**Fix:** Sản phẩm phải có variations với size/color match:
```json
{
  "variations": [
    { "size": "M", "color": "Đỏ", "quantity": 10 }
  ]
}
```

---

## Bước 4: Test với Postman/HTTP File

Test API trực tiếp để đảm bảo server hoạt động:

### Test Wishlist:
```http
### Add to wishlist
POST http://localhost:5001/api/wishlists
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "productId": "674a1234567890abcdef1234"
}
```

### Test Cart:
```http
### Add to cart
POST http://localhost:5001/api/cart
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "product_id": "674a1234567890abcdef1234",
  "name": "Áo thun",
  "image": "https://...",
  "size": "M",
  "color": "Đỏ",
  "quantity": 1,
  "price": 200000
}
```

---

## Bước 5: Check Response Format

Đảm bảo server trả về đúng format:

**Expected:**
```json
{
  "success": true,
  "message": "Đã thêm...",
  "data": {...}
}
```

**NOT:**
```json
{
  "message": "...",
  "cart": {...}
}
```

---

## 🎯 Action Items:

1. ✅ Add logging vào `XemChiTiet.java`
2. ✅ Build & run app
3. ✅ Nhấn nút tim/add to cart
4. ✅ Check Logcat (filter: "WISHLIST" hoặc "CART")
5. ✅ Check server terminal logs
6. ✅ Share logs với tôi để debug tiếp

**Logcat filter:**
```
adb logcat | grep -E "WISHLIST|CART"
```

Hoặc trong Android Studio Logcat, filter: `WISHLIST|CART`
