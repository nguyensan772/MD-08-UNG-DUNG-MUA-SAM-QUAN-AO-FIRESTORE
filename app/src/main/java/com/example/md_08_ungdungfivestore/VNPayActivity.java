package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.md_08_ungdungfivestore.fragments.DangLayHangFragment;

public class VNPayActivity extends AppCompatActivity {

    private WebView webView;
    private boolean isHandled = false;

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

        // --- THÊM 2 DÒNG NÀY VÀO ---
        webView.getSettings().setAllowFileAccess(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // ---------------------------

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url.contains("vnpay-return")) {
                    Uri uri = Uri.parse(url);
                    String orderId = uri.getQueryParameter("order_id"); // Lấy mã đơn từ link

                    // Gọi hàm xử lý và truyền orderId vào
                    handlePaymentResult(url);
                    return false;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Backup nếu URL chứa mã thành công
                if (url.contains("vnp_ResponseCode=00") && !isHandled) {
                    handlePaymentResult(url);
                }
            }
        });
    }

    private void handlePaymentResult(String url) {
        if (isHandled) return;

        Uri uri = Uri.parse(url);
        String responseCode = uri.getQueryParameter("vnp_ResponseCode");

        // Tự động trích xuất order_id từ URL nếu có
        String orderIdFromUrl = uri.getQueryParameter("order_id");

        if ("00".equals(responseCode)) {
            isHandled = true;
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

            webView.postDelayed(() -> {
                Intent intent = new Intent(VNPayActivity.this,  MainActivity.class);
                // Gửi orderId sang màn hình thành công, nếu rỗng thì gửi chuỗi rỗng
                intent.putExtra("ORDER_ID", orderIdFromUrl != null ? orderIdFromUrl : "");
                startActivity(intent);
                finish();
            }, 2000);
        } else if (responseCode != null) {
            isHandled = true;
            Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}