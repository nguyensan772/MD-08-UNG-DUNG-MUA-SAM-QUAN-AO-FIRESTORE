# ✅ Setup Hoàn Tất - Cart, Wishlist & Auto-Login

## Những gì đã hoàn thiện:

### 1. ✅ API Integration
- **CartApiService** - Giỏ hàng API
- **WishlistApiService** - Danh sách yêu thích API
- **TokenManager** - Quản lý JWT token
- **ApiClient** - Auto thêm token vào header

### 2. ✅ Fragments với API
- **GioHangFragment** - Load, update, delete cart items
- **YeuThichFragment** - Load, remove wishlist items
- **GioHangAdapter** - Adapter cho cart RecyclerView

### 3. ✅ Auto-Login Setup
- **ManChao** - Check token khi mở app
  - Nếu có token → MainActivity
  - Nếu không có → DangNhap
- **DangNhap** - Lưu token sau khi login thành công
- **MainActivity** - Init ApiClient khi khởi động

## Flow hoạt động:

```
App Start → ManChao (3s)
    ↓
Check Token?
    ├─ Có token → MainActivity (Đã login)
    └─ Không có → DangNhap
                    ↓
              Login thành công
                    ↓
              TokenManager.saveToken()
                    ↓
              MainActivity
```

## Files đã sửa/tạo:

### Modified:
1. `ManChao.java` - Thêm check token & auto-login
2. `DangNhap.java` - Dùng TokenManager thay vì SharedPreferences
3. `MainActivity.java` - Thêm `ApiClient.init(this)`

### Created:
1. `GioHangFragment.java` - Màn giỏ hàng với API
2. `GioHangAdapter.java` - Adapter cho cart items
3. `YeuThichFragment.java` - Màn yêu thích với API
4. `WishlistItem.java` - Model wishlist
5. `WishlistApiService.java` - API service wishlist
6. `SplashActivity.java` - (Optional, không dùng)

## API Endpoints:

### Cart:
- `GET /api/cart` - Lấy giỏ hàng
- `POST /api/cart` - Thêm sản phẩm
- `PUT /api/cart/:itemId` - Cập nhật số lượng
- `DELETE /api/cart/:itemId` - Xóa sản phẩm

### Wishlist:
- `GET /api/wishlists/me` - Lấy danh sách yêu thích
- `POST /api/wishlists` - Thêm vào yêu thích
- `DELETE /api/wishlists/:productId` - Xóa khỏi yêu thích

## Cách test:

1. **Chạy server:**
   ```bash
   cd MD-08-FIRESTORE-SERVER
   npm run dev
   ```

2. **Build & Run app:**
   - Server phải chạy ở port 5001
   - Emulator sẽ connect qua `http://10.0.2.2:5001`

3. **Test flow:**
   - Mở app lần đầu → Màn chào → DangNhap
   - Login thành công → MainActivity
   - Đóng app và mở lại → Màn chào → MainActivity (auto-login)
   - Vào tab Giỏ hàng/Yêu thích → Load data từ API

## Troubleshooting:

### Không load được cart/wishlist:
1. Check server đang chạy: `http://10.0.2.2:5001`
2. Check Logcat xem response code
3. Check token đã được lưu: `TokenManager.getToken()`

### Không auto-login:
1. Check `ManChao` đã gọi `ApiClient.init(this)`
2. Check `DangNhap` đã lưu token bằng `TokenManager`
3. Check Logcat xem token value

### 401 Unauthorized:
- Token không hợp lệ hoặc đã hết hạn
- Logout và login lại

## Next Steps:

1. ✅ Test cart operations (add, update, delete)
2. ✅ Test wishlist operations (add, remove)
3. ✅ Test auto-login flow
4. 🔲 Implement logout functionality
5. 🔲 Add loading indicators
6. 🔲 Handle empty states with proper UI
7. 🔲 Add error handling với user-friendly messages
