package com.example.md_08_ungdungfivestore.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.FilePrefs;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.LocalCartItem;
import com.example.md_08_ungdungfivestore.config.AppConfig;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.models.CartRequest;
import com.example.md_08_ungdungfivestore.models.CartResponse;
import com.example.md_08_ungdungfivestore.services.ApiClientCart;
import com.example.md_08_ungdungfivestore.services.CartService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectOptionsBottomSheetFragment extends BottomSheetDialogFragment {

    private ImageView ivProductImage;
    private TextView tvPriceNew, tvStock, tvQuantity, btnClose, btnBuyNow;
    private LinearLayout layoutColors, layoutSizes;
    private ImageButton btnDecrease, btnIncrease;
    private ProgressBar progressBar; // Thêm progress bar để báo đang tải

    private Product product;
    private int quantity = 1;
    private String selectedColor = null;
    private String selectedSize = null;
    private boolean isBuyNowAction;
    private int currentMaxStock = 0;

    private OnOptionSelectedListener listener;
    private CartService cartService;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String size, String color, int quantity);
    }

    public SelectOptionsBottomSheetFragment(Product product, boolean isBuyNowAction, OnOptionSelectedListener listener) {
        this.product = product;
        this.isBuyNowAction = isBuyNowAction;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_options, container, false);
        anhXa(view);

        cartService = ApiClientCart.getCartService(getContext());
        btnClose.setOnClickListener(v -> dismiss());

        if (product != null) {
            // ⭐ BƯỚC 1: Gọi API lấy dữ liệu tồn kho mới nhất ngay lập tức
            refreshProductDataFromServer(product.getId());
        }

        return view;
    }

    private void anhXa(View view) {
        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvPriceNew = view.findViewById(R.id.tvProductPriceNew);
        tvStock = view.findViewById(R.id.tvProductStock);
        layoutColors = view.findViewById(R.id.layoutColors);
        layoutSizes = view.findViewById(R.id.layoutSizes);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        btnDecrease = view.findViewById(R.id.btnDecrease);
        btnIncrease = view.findViewById(R.id.btnIncrease);
        btnBuyNow = view.findViewById(R.id.btnBuyNow);
        btnClose = view.findViewById(R.id.btnClose);
        progressBar = view.findViewById(R.id.progressBar); // Bạn nên thêm cái này vào XML
    }

    private void refreshProductDataFromServer(String productId) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Gọi API chi tiết sản phẩm (Đảm bảo CartService có hàm getProductDetail)
        cartService.getProductDetail(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body(); // Ghi đè dữ liệu cũ bằng dữ liệu mới nhất từ DB
                    updateUIWithFreshData();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                updateUIWithFreshData(); // Vẫn hiển thị dữ liệu cũ nếu lỗi mạng
            }
        });
    }

    private void updateUIWithFreshData() {
        loadProductImage(product.getImage());
        tvPriceNew.setText(formatPrice(product.getPrice()));
        tvPriceNew.setTextColor(0xFFD17842);

        currentMaxStock = product.getTotalQuantity();
        tvStock.setText("Kho: " + currentMaxStock);

        btnBuyNow.setText(isBuyNowAction ? "MUA NGAY" : "THÊM VÀO GIỎ");

        setupColors();
        setupSizes();
        setupQuantity();
        setupBuyNow();
    }

    private void updateStockDisplay() {
        if (product == null) return;

        if (selectedColor == null || selectedSize == null) {
            tvStock.setText("Kho: " + product.getTotalQuantity() + " (Chọn phân loại)");
            currentMaxStock = product.getTotalQuantity();
            return;
        }

        // ⭐ Lấy số lượng từ variations thực tế trong DB
        int variantStock = product.getStockForVariant(selectedSize, selectedColor);
        currentMaxStock = variantStock;

        if (variantStock > 0) {
            tvStock.setText("Kho: " + variantStock);
            tvStock.setTextColor(0xFF000000);
            btnBuyNow.setEnabled(true);
            btnBuyNow.setAlpha(1.0f);
        } else {
            tvStock.setText("Hết hàng");
            tvStock.setTextColor(0xFFFF0000);
            btnBuyNow.setEnabled(false);
            btnBuyNow.setAlpha(0.5f);
        }

        // Đồng bộ lại số lượng chọn nếu vượt quá kho mới
        if (quantity > currentMaxStock && currentMaxStock > 0) {
            quantity = currentMaxStock;
            tvQuantity.setText(String.valueOf(quantity));
        }
    }

    private void setupColors() {
        layoutColors.removeAllViews();
        List<String> colors = product.getAvailableColors();
        if (colors != null) {
            for (String color : colors) {
                TextView colorView = createOptionTextView(color);
                colorView.setOnClickListener(v -> {
                    selectedColor = color;
                    highlightSelected(layoutColors, colorView);
                    updateStockDisplay();
                });
                layoutColors.addView(colorView);
            }
        }
    }

    private void setupSizes() {
        layoutSizes.removeAllViews();
        List<String> sizes = product.getAvailableSizes();
        if (sizes != null) {
            for (String size : sizes) {
                TextView sizeView = createOptionTextView(size);
                sizeView.setOnClickListener(v -> {
                    selectedSize = size;
                    highlightSelected(layoutSizes, sizeView);
                    updateStockDisplay();
                });
                layoutSizes.addView(sizeView);
            }
        }
    }

    private TextView createOptionTextView(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setTextColor(0xFF000000);
        tv.setPadding(30, 15, 30, 15);
        tv.setBackgroundResource(R.drawable.bg_unselected_option); // Tạo file drawable viền xám
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        tv.setLayoutParams(params);
        return tv;
    }

    private void highlightSelected(LinearLayout parent, View selectedView) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            child.setBackgroundResource(R.drawable.bg_unselected_option);
            if (child instanceof TextView) ((TextView) child).setTextColor(0xFF000000);
        }
        selectedView.setBackgroundResource(R.drawable.bg_selected_option); // Viền màu cam/nâu
        if (selectedView instanceof TextView) ((TextView) selectedView).setTextColor(0xFFFFFFFF);
    }

    private void setupQuantity() {
        tvQuantity.setText(String.valueOf(quantity));
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });
        btnIncrease.setOnClickListener(v -> {
            if (selectedColor == null || selectedSize == null) {
                Toast.makeText(getContext(), "Vui lòng chọn phân loại", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity < currentMaxStock) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Kho chỉ còn: " + currentMaxStock, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBuyNow() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(FilePrefs.MyAppPrefs, Context.MODE_PRIVATE);
        String isLogin = sharedPreferences.getString("isLogin", "0");

        btnBuyNow.setOnClickListener(v -> {
            if (selectedColor == null || selectedSize == null) {
                Toast.makeText(getContext(), "Vui lòng chọn size và màu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentMaxStock <= 0) {
                Toast.makeText(getContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isLogin.equals("0")){
                // Xử lý giỏ hàng cục bộ (Local)
                List<LocalCartItem> cartList = getCart(getContext());
                boolean isExist = false;
                for (LocalCartItem item : cartList) {
                    if (item.getId().equals(product.getId()) && item.getSize().equals(selectedSize) && item.getColor().equals(selectedColor)) {
                        item.setQuantity(item.getQuantity() + quantity);
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    cartList.add(new LocalCartItem(product.getId(), product.getName(), product.getPrice(), quantity, product.getImage(), selectedSize, selectedColor));
                }
                saveCartList(getContext(), cartList);
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                // Xử lý gọi API Server
                CartRequest cartRequest = new CartRequest(product.getId(), product.getName(), selectedSize, selectedColor, quantity, product.getPrice());
                cartService.addToCart(cartRequest).enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            if (listener != null) listener.onOptionSelected(selectedSize, selectedColor, quantity);
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Lỗi từ máy chủ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadProductImage(String imagePath) {
        String fullUrl = imagePath.startsWith("http") ? imagePath : AppConfig.BASE_URL + imagePath;
        Glide.with(this).load(fullUrl).placeholder(R.drawable.ic_launcher_background).into(ivProductImage);
    }

    private String formatPrice(double price) {
        return String.format("%,.0fđ", price).replace(',', '.');
    }

    public static List<LocalCartItem> getCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FilePrefs.LocalCart, Context.MODE_PRIVATE);
        String json = prefs.getString(FilePrefs.KEY_CART, null);
        if (json == null) return new ArrayList<>();
        return new Gson().fromJson(json, new TypeToken<List<LocalCartItem>>(){}.getType());
    }

    private static void saveCartList(Context context, List<LocalCartItem> cartList) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FilePrefs.LocalCart, Context.MODE_PRIVATE).edit();
        editor.putString(FilePrefs.KEY_CART, new Gson().toJson(cartList));
        editor.apply();
    }

    @Override
    public int getTheme() { return R.style.BottomSheetDialogTheme; }
}