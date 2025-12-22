package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.Address;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.CartResponse;
import com.example.md_08_ungdungfivestore.models.OrderItemRequest;
import com.example.md_08_ungdungfivestore.models.OrderRequest;
import com.example.md_08_ungdungfivestore.models.OrderResponse;
import com.example.md_08_ungdungfivestore.models.User;
import com.example.md_08_ungdungfivestore.services.ApiClientCart;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich;
import com.example.md_08_ungdungfivestore.services.ApiPaymentService;
import com.example.md_08_ungdungfivestore.services.CartService;
import com.example.md_08_ungdungfivestore.services.OrderService;
import com.example.md_08_ungdungfivestore.services.UserApiService;
import com.example.md_08_ungdungfivestore.services.VNPayResponse;
import com.example.md_08_ungdungfivestore.utils.OrderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";

    private EditText edtName, edtPhone, edtStreet, edtProvince, edtDistrict, edtWard;
    private Button btnPlaceOrder;
    private TextView tvSubtotal, tvShippingFee, tvTotalAmount;

    private RadioGroup rgPaymentMethod;
    private RadioButton rbCash, rbVNPay;

    private OrderManager orderManager;
    private CartService cartService;
    private UserApiService userService;
    private ApiPaymentService apiPaymentService;

    private List<OrderItemRequest> orderItemsToSubmit = new ArrayList<>();
    private final double SHIPPING_FEE = 30000;
    private double currentTotalAmount = 0;
    private boolean isBuyNow = false;

    // --- KHAI BÁO LAUNCHER ĐỂ NHẬN KẾT QUẢ TỪ VNPAY ---
    private final ActivityResultLauncher<Intent> vnpayLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Server đã tạo Order và xóa Cart rồi
                    // Chuyển đến màn hình thành công chung
                    Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                    // Lưu ý: Lúc này vnpay trả về, bạn có thể load lại danh sách đơn hàng
                    // thay vì truyền OrderID nếu Server chưa trả về kịp ID đơn mới ở Client.
                    startActivity(intent);
                    finish();
                } else {
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(this, "Thanh toán thất bại hoặc đã hủy", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        anhXa();

        OrderService orderService = ApiClientYeuThich.getClient(this).create(OrderService.class);
        orderManager = new OrderManager(orderService);
        cartService = ApiClientCart.getCartService(this);
        userService = ApiClientYeuThich.getClient(this).create(UserApiService.class);
        apiPaymentService = ApiClientYeuThich.getClient(this).create(ApiPaymentService.class);

        btnPlaceOrder.setEnabled(false);
        isBuyNow = getIntent().getBooleanExtra("IS_BUY_NOW", false);

        if (isBuyNow) {
            setupBuyNowItem();
        } else {
            fetchCartItems();
        }

        fetchUserProfile();
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void anhXa() {
        edtName = findViewById(R.id.edtReceiverName);
        edtPhone = findViewById(R.id.edtPhone);
        edtStreet = findViewById(R.id.edtStreet);
        edtProvince = findViewById(R.id.edtProvince);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtWard = findViewById(R.id.edtWard);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCash = findViewById(R.id.rbCash);
        rbVNPay = findViewById(R.id.rbVNPay);
    }

    private void setupBuyNowItem() {
        String productId = getIntent().getStringExtra("PRODUCT_ID");
        String size = getIntent().getStringExtra("SELECTED_SIZE");
        String color = getIntent().getStringExtra("SELECTED_COLOR");
        int quantity = getIntent().getIntExtra("SELECTED_QUANTITY", 1);
        double price = getIntent().getDoubleExtra("PRODUCT_PRICE", 0);

        if (productId != null) {
            OrderItemRequest singleItem = new OrderItemRequest(productId, color, size, quantity, price);
            orderItemsToSubmit.add(singleItem);
            double subtotal = price * quantity;
            currentTotalAmount = subtotal + SHIPPING_FEE;
            updateUI(subtotal);
            btnPlaceOrder.setEnabled(true);
        } else {
            finish();
        }
    }

    private void fetchCartItems() {
        cartService.getCartItems().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> items = response.body().getItems();
                    if (items == null || items.isEmpty()) { finish(); return; }
                    double subtotal = 0;
                    for (CartItem item : items) {
                        subtotal += item.getPrice() * item.getQuantity();
                        orderItemsToSubmit.add(new OrderItemRequest(
                                item.getProductDetail().getId(), item.getColor(), item.getSize(), item.getQuantity(), item.getPrice()
                        ));
                    }
                    currentTotalAmount = subtotal + SHIPPING_FEE;
                    updateUI(subtotal);
                    btnPlaceOrder.setEnabled(true);
                }
            }
            @Override public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) { finish(); }
        });
    }

    private void updateUI(double subtotal) {
        tvSubtotal.setText(String.format(Locale.US, "%,.0f VNĐ", subtotal));
        tvShippingFee.setText(String.format(Locale.US, "%,.0f VNĐ", SHIPPING_FEE));
        tvTotalAmount.setText(String.format(Locale.US, "%,.0f VNĐ", currentTotalAmount));
    }

    private void fetchUserProfile() {
        userService.getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    edtName.setText(user.getFullName() != null ? user.getFullName() : "");
                    edtPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
                    edtStreet.setText(user.getStreet() != null ? user.getStreet() : "");
                    edtWard.setText(user.getWard() != null ? user.getWard() : "");
                    edtDistrict.setText(user.getDistrict() != null ? user.getDistrict() : "");
                    edtProvince.setText(user.getProvince() != null ? user.getProvince() : "");
                }
            }
            @Override public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {}
        });
    }

    private void placeOrder() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String province = edtProvince.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();
        String street = edtStreet.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || street.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        String method = "cash";
        if (rgPaymentMethod.getCheckedRadioButtonId() == R.id.rbVNPay) {
            method = "vnpay";
        }

        Address address = new Address(name, phone, province, district, ward, street);
        OrderRequest request = new OrderRequest(
                address,
                method,
                isBuyNow ? "Mua ngay trực tiếp" : "Thanh toán giỏ hàng",
                orderItemsToSubmit,
                SHIPPING_FEE,
                currentTotalAmount
        );

        btnPlaceOrder.setEnabled(false);

        if (method.equals("vnpay")) {
            // GỌI API VNPAY
            apiPaymentService.createVNPayOrder(request).enqueue(new Callback<VNPayResponse>() {
                @Override
                public void onResponse(Call<VNPayResponse> call, Response<VNPayResponse> response) {
                    // Chưa bật lại nút PlaceOrder vội, chờ kết quả từ Activity con
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                        // Thay vì startActivity, dùng vnpayLauncher
                        Intent intent = new Intent(CheckoutActivity.this, VNPayActivity.class);
                        intent.putExtra("PAYMENT_URL", response.body().getPaymentUrl());
                        vnpayLauncher.launch(intent);

                    } else {
                        btnPlaceOrder.setEnabled(true);
                        Toast.makeText(CheckoutActivity.this, "Lỗi tạo đơn hàng VNPay", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<VNPayResponse> call, Throwable t) {
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(CheckoutActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // GỌI API TIỀN MẶT
            orderManager.createOrder(request, new OrderManager.OrderCallback() {
                @Override
                public void onSuccess(OrderResponse orderResponse) {
                    Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                    intent.putExtra("orderId", orderResponse.getOrderId());
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onError(String error) {
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(CheckoutActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}