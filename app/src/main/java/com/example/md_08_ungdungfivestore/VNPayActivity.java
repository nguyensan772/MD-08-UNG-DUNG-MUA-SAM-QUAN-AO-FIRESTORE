package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.CartResponse;
import com.example.md_08_ungdungfivestore.services.ApiClientCart;
import com.example.md_08_ungdungfivestore.services.CartService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VNPayActivity extends AppCompatActivity {

    private WebView webView;
    private boolean isHandled = false; // Cờ kiểm tra để tránh xử lý 2 lần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        webView = findViewById(R.id.webViewVNPay);

        String paymentUrl = getIntent().getStringExtra("PAYMENT_URL");

        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy đường dẫn thanh toán!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupWebView();
        webView.loadUrl(paymentUrl);
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("vnp_ResponseCode")) {
                    if (!isHandled) {
                        isHandled = true;
                        handlePaymentResult(url);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("vnp_ResponseCode")) {
                    if (!isHandled) {
                        isHandled = true;
                        handlePaymentResult(url);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void handlePaymentResult(String url) {
        Uri uri = Uri.parse(url);
        String vnp_ResponseCode = uri.getQueryParameter("vnp_ResponseCode");
        String vnp_TxnRef = uri.getQueryParameter("vnp_TxnRef"); // Order ID

        if ("00".equals(vnp_ResponseCode)) {
            // --- TRƯỜNG HỢP THÀNH CÔNG (00) ---
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
            // Chỉ xóa giỏ hàng khi thành công
            clearCartAndFinish(vnp_TxnRef);

        } else {
            // --- TRƯỜNG HỢP HỦY HOẶC LỖI (24, 99...) ---
            Toast.makeText(this, "Thanh toán bị hủy hoặc thất bại", Toast.LENGTH_LONG).show();
            // Trả về kết quả HỦY cho CheckoutActivity
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    // Hàm xóa giỏ hàng và trả về kết quả OK
    private void clearCartAndFinish(String orderId) {
        CartService cartService = ApiClientCart.getCartService(this);

        cartService.getCartItems().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> items = response.body().getItems();
                    if (items != null && !items.isEmpty()) {
                        for (CartItem item : items) {
                            String itemIdToDelete = item.getId();
                            if (itemIdToDelete != null) {
                                deleteSingleItem(cartService, itemIdToDelete);
                            }
                        }
                    }
                }
                // Sau khi gọi lệnh xóa (hoặc ko có gì để xóa), báo về Checkout thành công
                finishWithSuccess(orderId);
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                // Lỗi mạng khi xóa giỏ nhưng tiền đã trừ -> Vẫn báo thành công
                finishWithSuccess(orderId);
            }
        });
    }

    private void deleteSingleItem(CartService service, String itemId) {
        service.deleteItem(itemId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {}
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {}
        });
    }

    private void finishWithSuccess(String orderId) {
        Intent intent = new Intent();
        intent.putExtra("ORDER_ID", orderId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Nếu bấm Back cứng trên điện thoại -> Xem như Hủy
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }
}