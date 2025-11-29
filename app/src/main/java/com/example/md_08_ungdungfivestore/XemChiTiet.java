package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // <-- Thêm import Log
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.md_08_ungdungfivestore.adapters.ImagePagerAdapter;
import com.example.md_08_ungdungfivestore.fragments.SelectOptionsBottomSheetFragment;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich; // <-- Thêm import ApiClientYeuThich
import com.example.md_08_ungdungfivestore.services.YeuThichManager; // <-- Thêm import YeuThichManager
import com.example.md_08_ungdungfivestore.utils.FavoriteManager; // <-- Giữ nguyên để tham chiếu ID R.id

import java.util.ArrayList;
import java.util.List;

public class XemChiTiet extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private ImageButton btnBack, btnFavorite;
    private TextView tvName, tvPrice, tvDesc, btnOrderNow, btnAddToCart;

    private Product product;
    private List<String> imageUrls = new ArrayList<>();

    private boolean isFavorite = false;
    private YeuThichManager yeuThichManager; // <-- THÊM: Quản lý yêu thích qua API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_chi_tiet);

        anhXa();

        // Lấy Product từ Intent
        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(product.getName());
        tvPrice.setText(String.format("%,.0f VND", product.getPrice())); // Định dạng lại giá tiền

        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            StringBuilder desc = new StringBuilder();
            for (Product.Description d : product.getDescription()) {
                desc.append(d.getField()).append(": ").append(d.getValue()).append("\n");
            }
            tvDesc.setText(desc.toString());
        } else {
            tvDesc.setText("Không có mô tả chi tiết.");
        }

        // Ảnh sản phẩm
        imageUrls.clear();
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            imageUrls.add(product.getImage());
        }
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageUrls.addAll(product.getImages());
        }

        setupViewPager();
        setupBackButton();

        // THAY THẾ: Khởi tạo YeuThichManager
        yeuThichManager = new YeuThichManager(ApiClientYeuThich.getYeuThichService(this));

        // 🛠️ ĐIỂM SỬA LỖI: KIỂM TRA product.getId() TRƯỚC KHI GỌI API
        if (product.getId() != null && !product.getId().isEmpty()) {
            checkFavoriteStatus(product.getId());
        } else {
            Log.e("XemChiTiet", "Lỗi: Product ID bị null hoặc rỗng, không thể kiểm tra trạng thái yêu thích.");
            btnFavorite.setImageResource(R.drawable.heart_empty);
            // Vô hiệu hóa nút yêu thích nếu không có ID
            btnFavorite.setEnabled(false);
        }

        setupFavoriteButton();

        // Mở BottomSheet khi nhấn 2 nút
        btnOrderNow.setOnClickListener(v -> openSelectOptionsBottomSheet());
        btnAddToCart.setOnClickListener(v -> openSelectOptionsBottomSheet());
    }

    private void anhXa() {
        viewPagerImages = findViewById(R.id.viewPagerImages);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);

        tvName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvProductPrice);
        tvDesc = findViewById(R.id.tvProductDesc);

        btnOrderNow = findViewById(R.id.btnOrderNow);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void setupViewPager() {
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUrls);
        viewPagerImages.setAdapter(adapter);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    // THÊM: Phương thức kiểm tra trạng thái yêu thích từ Server
    private void checkFavoriteStatus(String productId) {
        yeuThichManager.checkWishlist(productId, new YeuThichManager.CheckCallback() {
            @Override
            public void onCheckResult(boolean isFavoriteServer) {
                isFavorite = isFavoriteServer;
                btnFavorite.setImageResource(isFavorite ? R.drawable.heart_filled : R.drawable.heart_empty);
            }

            @Override
            public void onError(String error) {
                // Log lỗi rõ ràng hơn
                Log.e("XemChiTiet", "Lỗi kiểm tra yêu thích: " + error);
                // Giữ nguyên trạng thái mặc định (heart_empty) hoặc trạng thái hiện tại
            }
        });
    }

    private void setupFavoriteButton() {
        // Chỉ thêm listener nếu nút Favorite được bật (có ID hợp lệ)
        if (product.getId() == null || product.getId().isEmpty()) return;

        btnFavorite.setOnClickListener(v -> {
            btnFavorite.animate()
                    .scaleX(1.3f).scaleY(1.3f).setDuration(120)
                    .withEndAction(() ->
                            btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                    ).start();

            if (!isFavorite) {
                // GỌI API THÊM VÀO YÊU THÍCH
                yeuThichManager.addToWishlist(product.getId(), new YeuThichManager.ToggleCallback() {
                    @Override
                    public void onSuccess(String message, boolean isAdded) {
                        isFavorite = true;
                        btnFavorite.setImageResource(R.drawable.heart_filled);
                        Toast.makeText(XemChiTiet.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(XemChiTiet.this, "Lỗi thêm yêu thích: " + error, Toast.LENGTH_SHORT).show();
                        // Nếu thất bại, giữ nguyên trạng thái cũ
                    }
                });

            } else {
                // GỌI API XÓA KHỎI YÊU THÍCH
                yeuThichManager.removeFromWishlist(product.getId(), new YeuThichManager.ToggleCallback() {
                    @Override
                    public void onSuccess(String message, boolean isAdded) {
                        isFavorite = false;
                        btnFavorite.setImageResource(R.drawable.heart_empty);
                        Toast.makeText(XemChiTiet.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(XemChiTiet.this, "Lỗi bỏ yêu thích: " + error, Toast.LENGTH_SHORT).show();
                        // Nếu thất bại, giữ nguyên trạng thái cũ
                    }
                });
            }
        });
    }

    private void openSelectOptionsBottomSheet() {
        if (product == null) return;

        SelectOptionsBottomSheetFragment bottomSheet = new SelectOptionsBottomSheetFragment(product, (size, color, quantity) -> {
            Toast.makeText(XemChiTiet.this,
                    "Bạn chọn: Size " + size + ", Màu " + color + ", Số lượng: " + quantity,
                    Toast.LENGTH_LONG).show();
            // TODO: Xử lý đặt hàng hoặc thêm vào giỏ hàng
        });

        bottomSheet.show(getSupportFragmentManager(), "SelectOptionsBottomSheet");
    }
}