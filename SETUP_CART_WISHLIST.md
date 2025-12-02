# Setup Guide - Cart & Wishlist API

## Vấn đề: Không load được giỏ hàng và danh sách yêu thích

### Checklist để fix:

#### 1. ✅ Khởi tạo ApiClient (QUAN TRỌNG!)

Trong `MainActivity.java` hoặc tạo `Application` class:

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // PHẢI GỌI DÒNG NÀY TRƯỚC KHI DÙNG BẤT KỲ API NÀO
        ApiClient.init(this);
        
        setContentView(R.layout.activity_main);
    }
}
```

#### 2. ✅ Lưu Token sau khi Login

Trong màn hình Login, sau khi nhận được response thành công:

```java
ApiService apiService = ApiClient.getClient().create(ApiService.class);
apiService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
    @Override
    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            AuthResponse authResponse = response.body();
            if (authResponse.isSuccess()) {
                // LƯU TOKEN VÀO TOKENMANAGER
                TokenManager tokenManager = new TokenManager(LoginActivity.this);
                tokenManager.saveToken(authResponse.getToken());
                
                // Chuyển màn
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }
    }
    
    @Override
    public void onFailure(Call<AuthResponse> call, Throwable t) {
        Log.e("LOGIN", "Error: " + t.getMessage());
    }
});
```

#### 3. ✅ Kiểm tra Server đang chạy

Server phải chạy ở port 5001:
```
http://10.0.2.2:5001/
```

Check trong `ApiClient.java`:
```java
private static final String BASE_URL = "http://10.0.2.2:5001/";
```

#### 4. ✅ Test API bằng Logcat

Thêm logging để debug:

```java
// Trong GioHangFragment
private void loadCart() {
    Log.d("CART", "Loading cart...");
    
    cartApiService.getCart().enqueue(new Callback<ApiResponse<Cart>>() {
        @Override
        public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
            Log.d("CART", "Response code: " + response.code());
            Log.d("CART", "Response body: " + response.body());
            
            if (response.isSuccessful() && response.body() != null) {
                // ...
            }
        }
        
        @Override
        public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
            Log.e("CART", "Error: " + t.getMessage(), t);
        }
    });
}
```

#### 5. ✅ Kiểm tra Token trong SharedPreferences

Debug token:
```java
TokenManager tokenManager = new TokenManager(this);
String token = tokenManager.getToken();
Log.d("TOKEN", "Current token: " + token);

if (token == null || token.isEmpty()) {
    Log.e("TOKEN", "NO TOKEN! User needs to login");
    // Redirect to login
}
```

### API Endpoints hiện tại:

**Cart API:**
- GET `/api/cart` - Lấy giỏ hàng
- POST `/api/cart` - Thêm vào giỏ
- PUT `/api/cart/:itemId` - Cập nhật số lượng
- DELETE `/api/cart/:itemId` - Xóa sản phẩm
- DELETE `/api/cart` - Xóa toàn bộ giỏ

**Wishlist API:**
- GET `/api/wishlists/me` - Lấy danh sách yêu thích
- POST `/api/wishlists` - Thêm vào yêu thích
- DELETE `/api/wishlists/:productId` - Xóa khỏi yêu thích
- GET `/api/wishlists/check/:productId` - Kiểm tra sản phẩm

### Common Errors:

**401 Unauthorized:**
- Token không hợp lệ hoặc chưa được lưu
- Kiểm tra `TokenManager.saveToken()` đã được gọi chưa

**404 Not Found:**
- Sai endpoint URL
- Server chưa chạy hoặc sai port

**Network Error:**
- Emulator không kết nối được `10.0.2.2`
- Firewall block port 5001
- Server chưa chạy

### Debug Steps:

1. Check server logs xem có request đến không
2. Check Logcat xem response code
3. Check token có được lưu không
4. Check `ApiClient.init()` đã được gọi chưa
5. Test API bằng Postman/HTTP file trước
