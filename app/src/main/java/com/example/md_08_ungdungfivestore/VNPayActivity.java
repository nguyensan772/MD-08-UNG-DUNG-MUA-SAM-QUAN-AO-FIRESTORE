package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VNPayActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay);

        webView = findViewById(R.id.webViewVNPay);

        // Lấy URL thanh toán từ Intent truyền sang
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
        webView.getSettings().setJavaScriptEnabled(true); // Quan trọng để VNPay hoạt động
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Kiểm tra nếu URL chứa đường dẫn Return của bạn
                // Lưu ý: Chuỗi này phải khớp với VNP_RETURN_URL trong file Node.js của bạn
                if (url.contains("vnpay-return")) {
                    handlePaymentResult(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    private void handlePaymentResult(String url) {
        // Kiểm tra mã phản hồi vnp_ResponseCode=00 là thành công
        if (url.contains("vnp_ResponseCode=00")) {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

            // Chuyển sang màn hình Success
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            // Bạn có thể parse thêm orderId từ URL nếu cần
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Thanh toán không thành công hoặc bị hủy", Toast.LENGTH_LONG).show();
            finish(); // Quay lại màn hình Checkout để thử lại
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}