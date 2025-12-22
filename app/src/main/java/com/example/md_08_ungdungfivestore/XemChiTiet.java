package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.md_08_ungdungfivestore.adapters.ImagePagerAdapter;
import com.example.md_08_ungdungfivestore.adapters.CommentAdapter;
import com.example.md_08_ungdungfivestore.fragments.SelectOptionsBottomSheetFragment;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.models.Comment;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich;
import com.example.md_08_ungdungfivestore.services.CommentApiService;
import com.example.md_08_ungdungfivestore.services.CommentResponse;
import com.example.md_08_ungdungfivestore.services.YeuThichManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XemChiTiet extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private ImageButton btnBack, btnFavorite;
    private TextView tvName, tvPrice, tvDesc, btnOrderNow, btnAddToCart;

    // THÊM MỚI CHO COMMENT
    private RecyclerView rcvComments;
    private TextView tvNoComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();

    private Product product;
    private List<String> imageUrls = new ArrayList<>();
    private boolean isFavorite = false;
    private YeuThichManager yeuThichManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_chi_tiet);

        anhXa();

        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Đổ dữ liệu sản phẩm cũ
        tvName.setText(product.getName());
        tvPrice.setText(String.format("%,.0f VND", product.getPrice()));
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            StringBuilder desc = new StringBuilder();
            for (Product.Description d : product.getDescription()) {
                desc.append(d.getField()).append(": ").append(d.getValue()).append("\n");
            }
            tvDesc.setText(desc.toString());
        } else {
            tvDesc.setText("Không có mô tả chi tiết.");
        }

        // Xử lý ảnh cũ
        imageUrls.clear();
        if (product.getImage() != null && !product.getImage().isEmpty()) imageUrls.add(product.getImage());
        if (product.getImages() != null && !product.getImages().isEmpty()) imageUrls.addAll(product.getImages());

        setupViewPager();
        setupBackButton();

        // Xử lý Yêu thích cũ
        yeuThichManager = new YeuThichManager(ApiClientYeuThich.getYeuThichService(this));
        if (product.getId() != null && !product.getId().isEmpty()) {
            checkFavoriteStatus(product.getId());
        }
        setupFavoriteButton();

        // Xử lý Giỏ hàng cũ
        setupCartButtons();

        // --- PHẦN THÊM MỚI: SETUP COMMENT ---
        setupCommentSection();
        fetchComments(product.getId());
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

        // Ánh xạ comment mới
        rcvComments = findViewById(R.id.rcvComments);
        tvNoComments = findViewById(R.id.tvNoComments);
    }

    private void setupCommentSection() {
        commentAdapter = new CommentAdapter(commentList);
        rcvComments.setLayoutManager(new LinearLayoutManager(this));
        rcvComments.setAdapter(commentAdapter);
    }

    private void fetchComments(String productId) {
        CommentApiService apiService = ApiClientYeuThich.getClient(this).create(CommentApiService.class);

        apiService.getCommentsByProduct(productId).enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy từ getItems() thay vì getData()
                    List<Comment> serverComments = response.body().getItems();

                    commentList.clear();
                    if (serverComments != null && !serverComments.isEmpty()) {
                        commentList.addAll(serverComments);
                        tvNoComments.setVisibility(View.GONE);
                    } else {
                        tvNoComments.setVisibility(View.VISIBLE);
                    }
                    commentAdapter.notifyDataSetChanged();

                    // Cập nhật rating trung bình lên UI (nếu bạn có TextView hiển thị tổng sao)
                    if (response.body().getSummary() != null) {
                        Log.d("DEBUG", "Rating trung bình: " + response.body().getSummary().ratingAvg);
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
            }
        });
    }

    // --- CÁC LOGIC CŨ CỦA BẠN (GIỮ NGUYÊN) ---
    private void setupViewPager() {
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUrls);
        viewPagerImages.setAdapter(adapter);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void checkFavoriteStatus(String productId) {
        yeuThichManager.checkWishlist(productId, new YeuThichManager.CheckCallback() {
            @Override
            public void onCheckResult(boolean isFavoriteServer) {
                isFavorite = isFavoriteServer;
                btnFavorite.setImageResource(isFavorite ? R.drawable.heart_filled : R.drawable.heart_empty);
            }
            @Override
            public void onError(String error) { Log.e("XemChiTiet", error); }
        });
    }

    private void setupFavoriteButton() {
        btnFavorite.setOnClickListener(v -> {
            btnFavorite.animate().scaleX(1.3f).scaleY(1.3f).setDuration(120)
                    .withEndAction(() -> btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(120).start()).start();
            if (!isFavorite) {
                yeuThichManager.addToWishlist(product.getId(), new YeuThichManager.ToggleCallback() {
                    @Override
                    public void onSuccess(String message, boolean isAdded) {
                        isFavorite = true;
                        btnFavorite.setImageResource(R.drawable.heart_filled);
                        Toast.makeText(XemChiTiet.this, message, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String error) {}
                });
            } else {
                yeuThichManager.removeFromWishlist(product.getId(), new YeuThichManager.ToggleCallback() {
                    @Override
                    public void onSuccess(String message, boolean isAdded) {
                        isFavorite = false;
                        btnFavorite.setImageResource(R.drawable.heart_empty);
                        Toast.makeText(XemChiTiet.this, message, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String error) {}
                });
            }
        });
    }

    private void setupCartButtons() {
        btnAddToCart.setOnClickListener(v -> openSelectOptionsBottomSheet(false));
        btnOrderNow.setOnClickListener(v -> openSelectOptionsBottomSheet(true));
    }

    private void openSelectOptionsBottomSheet(boolean navigateToCheckout) {
        if (product == null) return;
        SelectOptionsBottomSheetFragment bottomSheet = new SelectOptionsBottomSheetFragment(product, navigateToCheckout, (size, color, quantity) -> {
            if (navigateToCheckout) {
                Intent intent = new Intent(XemChiTiet.this, CheckoutActivity.class);
                intent.putExtra("PRODUCT_ID", product.getId());
                intent.putExtra("PRODUCT_NAME", product.getName());
                intent.putExtra("PRODUCT_PRICE", product.getPrice());
                intent.putExtra("PRODUCT_IMAGE", product.getImage());
                intent.putExtra("SELECTED_SIZE", size);
                intent.putExtra("SELECTED_COLOR", color);
                intent.putExtra("SELECTED_QUANTITY", quantity);
                intent.putExtra("IS_BUY_NOW", true);
                startActivity(intent);
            }
        });
        bottomSheet.show(getSupportFragmentManager(), "SelectOptionsBottomSheet");
    }
}