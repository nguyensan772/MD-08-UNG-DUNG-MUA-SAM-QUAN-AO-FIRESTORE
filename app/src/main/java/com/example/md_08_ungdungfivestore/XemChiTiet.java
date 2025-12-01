package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.md_08_ungdungfivestore.adapters.ImagePagerAdapter;
import com.example.md_08_ungdungfivestore.fragments.SelectOptionsBottomSheetFragment;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.utils.FavoriteManager;

import java.util.ArrayList;
import java.util.List;

public class XemChiTiet extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private ImageButton btnBack, btnFavorite;
    private TextView tvName, tvPrice, tvDesc, btnOrderNow, btnAddToCart;

    private Product product;
    private List<String> imageUrls = new ArrayList<>();

    private boolean isFavorite = false;
    private FavoriteManager favoriteManager;

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
        tvPrice.setText(String.format("%.0f VND", product.getPrice()));

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

        // Khởi tạo FavoriteManager
        favoriteManager = new FavoriteManager(this);

        // Kiểm tra trạng thái yêu thích và cập nhật nút tim
        isFavorite = favoriteManager.isFavorite(product.getId());
        btnFavorite.setImageResource(isFavorite ? R.drawable.heart_filled : R.drawable.heart_empty);

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

    private void setupFavoriteButton() {
        btnFavorite.setOnClickListener(v -> {
            btnFavorite.animate()
                    .scaleX(1.3f).scaleY(1.3f).setDuration(120)
                    .withEndAction(() ->
                            btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                    ).start();

            if (!isFavorite) {
                // Thêm vào favorites
                favoriteManager.addFavorite(product);
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.heart_filled);
                Toast.makeText(XemChiTiet.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                // Xoá khỏi favorites
                favoriteManager.removeFavorite(product);
                isFavorite = false;
                btnFavorite.setImageResource(R.drawable.heart_empty);
                Toast.makeText(XemChiTiet.this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
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
