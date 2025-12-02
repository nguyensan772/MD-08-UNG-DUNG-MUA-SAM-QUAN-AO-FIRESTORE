# 🔧 FIX LỖI 400 - Cart & Wishlist

## Vấn đề: Server trả về 400 Bad Request

Lỗi 400 nghĩa là server validation failed. Có thể do:
1. ❌ Product ID null hoặc undefined
2. ❌ Product ID format sai (không phải MongoDB ObjectId)
3. ❌ Thiếu required fields
4. ❌ Product không tồn tại trong database

---

## ✅ GIẢI PHÁP:

### Fix 1: Check Product ID trong Android

Thêm validation trong `XemChiTiet.java`:

```java
private void addToWishlistAPI(String productId) {
    // ✅ VALIDATE PRODUCT ID
    if (productId == null || productId.isEmpty()) {
        Log.e("WISHLIST", "Product ID is NULL or EMPTY!");
        Toast.makeText(this, "Lỗi: Product ID không hợp lệ", Toast.LENGTH_SHORT).show();
        return;
    }
    
    Log.d("WISHLIST", "Product ID: " + productId);
    
    // Continue with API call...
}

private void addToCartAPI(Product product, String size, String color, int quantity) {
    // ✅ VALIDATE ALL FIELDS
    if (product == null || product.getId() == null || product.getId().isEmpty()) {
        Log.e("CART", "Product or Product ID is NULL!");
        Toast.makeText(this, "Lỗi: Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (size == null || color == null) {
        Log.e("CART", "Size or Color is NULL!");
        Toast.makeText(this, "Vui lòng chọn size và màu", Toast.LENGTH_SHORT).show();
        return;
    }
    
    Log.d("CART", "All fields valid, proceeding...");
    
    // Continue with API call...
}
```

### Fix 2: Đảm bảo Product có ID khi load

Trong activity/fragment load products, check:

```java
// Khi parse JSON response
Product product = response.body().getData();
Log.d("PRODUCT", "ID: " + product.getId()); // ✅ Phải có giá trị!

// Nếu ID = null, có thể do:
// 1. Server trả về "_id" thay vì "id"
// 2. Gson không parse đúng
```

### Fix 3: Update Product Model nếu cần

Nếu server trả về `_id` thay vì `id`:

```java
public class Product implements Serializable {
    @SerializedName("_id") // ✅ Map "_id" từ server
    private String id;
    
    // ... rest of fields
    
    public String getId() { return id; }
}
```

### Fix 4: Server - Add Better Error Messages

Update `cart.controller.js`:

```javascript
exports.addToCart = async (req, res) => {
    try {
        const userId = req.user.userId;
        const { product_id, name, image, size, color, quantity, price } = req.body;

        // ✅ DETAILED VALIDATION
        console.log('=== ADD TO CART REQUEST ===');
        console.log('User ID:', userId);
        console.log('Product ID:', product_id);
        console.log('Name:', name);
        console.log('Size:', size);
        console.log('Color:', color);
        console.log('Quantity:', quantity);
        console.log('Price:', price);

        if (!product_id) {
            console.error('Missing product_id');
            return res.status(400).json({
                success: false,
                message: 'Thiếu product_id'
            });
        }
        
        if (!name) {
            console.error('Missing name');
            return res.status(400).json({
                success: false,
                message: 'Thiếu name'
            });
        }
        
        if (!size || !color) {
            console.error('Missing size or color');
            return res.status(400).json({
                success: false,
                message: 'Thiếu size hoặc color'
            });
        }
        
        if (!quantity || quantity < 1) {
            console.error('Invalid quantity');
            return res.status(400).json({
                success: false,
                message: 'Số lượng không hợp lệ'
            });
        }
        
        if (!price) {
            console.error('Missing price');
            return res.status(400).json({
                success: false,
                message: 'Thiếu price'
            });
        }

        // Check product exists
        const product = await Product.findById(product_id);
        if (!product) {
            console.error('Product not found:', product_id);
            return res.status(404).json({
                success: false,
                message: 'Không tìm thấy sản phẩm với ID: ' + product_id
            });
        }
        
        console.log('Product found:', product.name);
        
        // Continue...
    } catch (error) {
        console.error('Error in addToCart:', error);
        res.status(500).json({
            success: false,
            message: error.message
        });
    }
};
```

### Fix 5: Test với Product ID thật

1. **Lấy Product ID thật từ database:**
   ```javascript
   // Trong MongoDB hoặc server logs
   db.products.findOne({}, {_id: 1, name: 1})
   // Copy _id value
   ```

2. **Test bằng HTTP file:**
   ```http
   POST http://localhost:5001/api/cart
   Authorization: Bearer YOUR_TOKEN
   Content-Type: application/json

   {
     "product_id": "674a1234567890abcdef1234", // ✅ ID thật từ DB
     "name": "Áo thun",
     "image": "https://...",
     "size": "M",
     "color": "Đỏ",
     "quantity": 1,
     "price": 200000
   }
   ```

---

## 🔍 DEBUG STEPS:

### Step 1: Check Android Logs
```
adb logcat | grep -E "CART|WISHLIST|XemChiTiet"
```

Tìm:
- `Product ID: null` → Product không có ID
- `Product ID: 674a...` → ID hợp lệ

### Step 2: Check Server Logs
Xem terminal server, tìm:
```
=== ADD TO CART REQUEST ===
Product ID: undefined  ← ❌ VẤN ĐỀ!
```

Hoặc:
```
Product not found: 674a1234567890abcdef1234  ← Product không tồn tại
```

### Step 3: Test Direct API Call
Dùng file `TEST_CART_WISHLIST.http` để test trực tiếp:
1. Thay YOUR_TOKEN bằng token thật
2. Thay product_id bằng ID thật từ DB
3. Send request
4. Check response

---

## 🎯 COMMON ISSUES & FIXES:

### Issue 1: Product ID = null
**Nguyên nhân:** Server trả về `_id` nhưng model expect `id`

**Fix:** Add `@SerializedName("_id")` vào Product model

### Issue 2: Product không tồn tại
**Nguyên nhân:** Dùng fake ID để test

**Fix:** Lấy ID thật từ database

### Issue 3: Thiếu variations
**Nguyên nhân:** Product không có size/color trong variations

**Fix:** Đã remove validation này ở server (user đã làm rồi)

### Issue 4: Token expired
**Nguyên nhân:** Token hết hạn

**Fix:** Login lại để lấy token mới

---

## ✅ CHECKLIST:

- [ ] Product model có `@SerializedName("_id")`
- [ ] Product ID không null khi load
- [ ] Server logs show request với đầy đủ fields
- [ ] Product ID tồn tại trong database
- [ ] Token còn hạn
- [ ] Size và Color được chọn từ BottomSheet
- [ ] All required fields có giá trị

---

## 📝 NEXT STEPS:

1. ✅ Thêm logging vào XemChiTiet (đã làm)
2. ✅ Build & run app
3. ✅ Vào product detail
4. ✅ Nhấn nút tim/add to cart
5. ✅ Check Logcat: `adb logcat | grep WISHLIST`
6. ✅ Check server terminal
7. ✅ Share logs để debug tiếp
