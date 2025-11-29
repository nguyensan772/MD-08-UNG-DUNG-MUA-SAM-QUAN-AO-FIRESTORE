package com.example.md_08_ungdungfivestore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.List;

public class SelectOptionsBottomSheetFragment extends BottomSheetDialogFragment {

    private ImageView ivProductImage;
    private TextView tvPriceNew, tvStock, tvQuantity, btnClose, btnBuyNow;
    private LinearLayout layoutColors, layoutSizes;
    private ImageButton btnDecrease, btnIncrease;

    private Product product;
    private int quantity = 1;
    private String selectedColor = null;
    private String selectedSize = null;

    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String size, String color, int quantity);
    }

    public SelectOptionsBottomSheetFragment(Product product, OnOptionSelectedListener listener) {
        this.product = product;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_options, container, false);

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

        btnClose.setOnClickListener(v -> dismiss());

        loadProductImage(product.getImage());

        // Giá tiền hiển thị 123.000đ, màu cam
        tvPriceNew.setText(formatPrice(product.getPrice()));
        tvPriceNew.setTextColor(0xFFD17842);

        // Kho, chữ màu đen
        tvStock.setText("Kho: " + product.getTotalQuantity());
        tvStock.setTextColor(0xFF000000);

        setupColors();
        setupSizes();
        setupQuantity();
        setupBuyNow();

        return view;
    }

    // Format giá tiền dạng 123.000đ
    private String formatPrice(double price) {
        return String.format("%,.0fđ", price).replace(',', '.');
    }

    // Load ảnh từ URL hoặc drawable trong project
// SelectOptionsBottomSheetFragment.java
// ...
// Load ảnh từ URL hoặc drawable trong project
    private void loadProductImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            ivProductImage.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        // 💡 Thêm Base URL cho Local Upload images
        String fullUrl;

        // Giả định bạn có BASE_URL được định nghĩa trong một file Constants
        // Thay BASE_URL bằng địa chỉ Server của bạn, ví dụ: "http://10.0.2.2:5001" (cho emulator)
        final String BASE_URL = "http://10.0.2.2:5001"; // <<< CẦN THAY ĐỔI DÒNG NÀY

        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            // Cloudinary hoặc URL đầy đủ
            fullUrl = imagePath;
        } else if (imagePath.startsWith("/uploads/")) {
            // Local Upload: Nối Base URL vào
            fullUrl = BASE_URL + imagePath;
        } else {
            // thử tìm drawable theo tên (Giữ lại logic cũ cho ảnh nội bộ/fallback)
            int resId = getResources().getIdentifier(imagePath.replace(".jpg","").replace(".png",""), "drawable", getContext().getPackageName());
            if(resId != 0){
                ivProductImage.setImageResource(resId);
                return; // Đã tìm thấy drawable, thoát
            } else {
                // fallback (Chỉ hiển thị ảnh lỗi nếu không phải URL hay drawable hợp lệ)
                ivProductImage.setImageResource(R.drawable.ic_launcher_background);
                return; // Thoát
            }
        }

        // Tải ảnh bằng Glide
        Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivProductImage);
    }
// ...

    private void setupColors() {
        layoutColors.removeAllViews();
        List<String> colors = product.getAvailableColors();
        if (colors != null) {
            int margin = 8;
            for (String color : colors) {
                TextView colorView = new TextView(getContext());
                colorView.setText(color);
                colorView.setTextColor(0xFF000000); // chữ đen
                colorView.setTextSize(16f);
                colorView.setPadding(24,12,24,12);
                colorView.setBackgroundColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin,0,0,0);
                colorView.setLayoutParams(params);

                colorView.setOnClickListener(v -> {
                    selectedColor = color;
                    highlightSelected(layoutColors, colorView);
                });
                layoutColors.addView(colorView);
            }
        }
    }

    private void setupSizes() {
        layoutSizes.removeAllViews();
        List<String> sizes = product.getAvailableSizes();
        if (sizes != null) {
            int margin = 8;
            for (String size : sizes) {
                TextView sizeView = new TextView(getContext());
                sizeView.setText(size);
                sizeView.setTextColor(0xFF000000); // chữ đen
                sizeView.setTextSize(16f);
                sizeView.setPadding(24,12,24,12);
                sizeView.setBackgroundColor(0xFFFFFFFF);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin,0,0,0);
                sizeView.setLayoutParams(params);

                sizeView.setOnClickListener(v -> {
                    selectedSize = size;
                    highlightSelected(layoutSizes, sizeView);
                });
                layoutSizes.addView(sizeView);
            }
        }
    }

    private void highlightSelected(LinearLayout parent, View selectedView) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            child.setBackgroundColor(0xFFFFFFFF);
            if (child instanceof TextView) ((TextView) child).setTextColor(0xFF000000);
        }
        selectedView.setBackgroundColor(0xFFD17842);
        if (selectedView instanceof TextView) ((TextView) selectedView).setTextColor(0xFFFFFFFF);
    }

    private void setupQuantity() {
        tvQuantity.setText(String.valueOf(quantity));
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) quantity--;
            tvQuantity.setText(String.valueOf(quantity));
        });
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });
    }

    private void setupBuyNow() {
        btnBuyNow.setOnClickListener(v -> {
            if (selectedColor == null || selectedSize == null) {
                Toast.makeText(getContext(), "Vui lòng chọn size và màu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) {
                listener.onOptionSelected(selectedSize, selectedColor, quantity);
            }
            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
