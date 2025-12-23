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

    private Product product;
    private int quantity = 1;
    private String selectedColor = null;
    private String selectedSize = null;
    private boolean isBuyNowAction;

    // ⭐ BIẾN MỚI: Theo dõi tồn kho hiện tại tối đa được phép mua
    private int currentMaxStock = 0;

    private OnOptionSelectedListener listener;

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
        btnClose.setOnClickListener(v -> dismiss());

        if (product != null) {
            loadProductImage(product.getImage());
            tvPriceNew.setText(formatPrice(product.getPrice()));
            tvPriceNew.setTextColor(0xFFD17842);

            // Khởi tạo mặc định
            currentMaxStock = product.getTotalQuantity();
            tvStock.setText("Kho: " + currentMaxStock);
            tvStock.setTextColor(0xFF000000);

            // Cập nhật text nút bấm dựa trên hành động
            if (isBuyNowAction) {
                btnBuyNow.setText("MUA NGAY");
            } else {
                btnBuyNow.setText("THÊM VÀO GIỎ");
            }

            setupColors();
            setupSizes();
            setupQuantity();
            setupBuyNow();
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
    }

    private String formatPrice(double price) {
        return String.format("%,.0fđ", price).replace(',', '.');
    }

    // ... (Giữ nguyên hàm loadProductImage) ...
    private void loadProductImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            ivProductImage.setImageResource(R.drawable.ic_launcher_background);
            return;
        }
        String fullUrl;
        final String BASE_URL = "https://bruce-brutish-duane.ngrok-free.dev";
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            fullUrl = imagePath;
        } else if (imagePath.startsWith("/uploads/")) {
            fullUrl = BASE_URL + imagePath;
        } else {
            // ... Logic local drawable cũ ...
            ivProductImage.setImageResource(R.drawable.ic_launcher_background);
            return;
        }
        Glide.with(this).load(fullUrl).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(ivProductImage);
    }

    // ⭐ LOGIC MỚI: Cập nhật hiển thị kho
    private void updateStockDisplay() {
        if (product == null) return;

        // Nếu chưa chọn đủ cả 2 -> Hiện tổng hoặc ẩn
        if (selectedColor == null || selectedSize == null) {
            tvStock.setText("Kho: " + product.getTotalQuantity() + " (Chọn phân loại)");
            currentMaxStock = product.getTotalQuantity(); // Hoặc set về 0 để bắt buộc chọn
            return;
        }

        // Khi đã chọn đủ Size và Màu -> Lấy số lượng thực tế
        // GIẢ ĐỊNH: Bạn đã thêm hàm getStockForVariant vào Product.java như Bước 1
        int variantStock = product.getStockForVariant(selectedSize, selectedColor);

        // Cập nhật biến toàn cục để dùng cho nút tăng giảm
        currentMaxStock = variantStock;

        // Hiển thị ra màn hình
        if (variantStock > 0) {
            tvStock.setText("Kho: " + variantStock);
            tvStock.setTextColor(0xFF000000);
            btnBuyNow.setEnabled(true);
            btnBuyNow.setAlpha(1.0f);
        } else {
            tvStock.setText("Hết hàng");
            tvStock.setTextColor(0xFFFF0000); // Màu đỏ báo động
            btnBuyNow.setEnabled(false); // Khóa nút mua
            btnBuyNow.setAlpha(0.5f); // Làm mờ nút mua
        }

        // Reset lại số lượng chọn về 1 (hoặc max) nếu số lượng đang chọn > kho mới
        if (quantity > currentMaxStock && currentMaxStock > 0) {
            quantity = currentMaxStock;
        } else if (currentMaxStock == 0) {
            quantity = 1; // Chỉ để hiển thị, nút mua đã bị khóa
        } else if (quantity < 1) {
            quantity = 1;
        }
        tvQuantity.setText(String.valueOf(quantity));
    }

    private void setupColors() {
        layoutColors.removeAllViews();
        List<String> colors = product.getAvailableColors();
        if (colors != null) {
            int margin = 8;
            for (String color : colors) {
                TextView colorView = new TextView(getContext());
                colorView.setText(color);
                colorView.setTextColor(0xFF000000);
                colorView.setTextSize(16f);
                colorView.setPadding(24,12,24,12);
                colorView.setBackgroundColor(0xFFFFFFFF);

                // Set viền để đẹp hơn (tùy chọn)
                // colorView.setBackgroundResource(R.drawable.bg_option_item);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin,0,0,0);
                colorView.setLayoutParams(params);
                colorView.setOnClickListener(v -> {
                    selectedColor = color;
                    highlightSelected(layoutColors, colorView);
                    updateStockDisplay(); // ⭐ GỌI HÀM CẬP NHẬT KHO
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
                sizeView.setTextColor(0xFF000000);
                sizeView.setTextSize(16f);
                sizeView.setPadding(24,12,24,12);
                sizeView.setBackgroundColor(0xFFFFFFFF);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin,0,0,0);
                sizeView.setLayoutParams(params);
                sizeView.setOnClickListener(v -> {
                    selectedSize = size;
                    highlightSelected(layoutSizes, sizeView);
                    updateStockDisplay(); // ⭐ GỌI HÀM CẬP NHẬT KHO
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
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        // ⭐ LOGIC MỚI: Không cho tăng quá currentMaxStock
        btnIncrease.setOnClickListener(v -> {
            if (selectedColor == null || selectedSize == null) {
                Toast.makeText(getContext(), "Vui lòng chọn size và màu trước", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity < currentMaxStock) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Đã đạt giới hạn tồn kho: " + currentMaxStock, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 1. Lấy toàn bộ danh sách giỏ hàng (Dùng GSON)
    public static List<LocalCartItem> getCart(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FilePrefs.LocalCart, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(FilePrefs.KEY_CART, null);

        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<LocalCartItem>>() {}.getType();
        return gson.fromJson(json, type);
    }
    private void setupBuyNow() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(FilePrefs.MyAppPrefs, Context.MODE_PRIVATE);
        SharedPreferences cart = getContext().getSharedPreferences(FilePrefs.LocalCart, Context.MODE_PRIVATE);
        String isLogin = sharedPreferences.getString("isLogin", "0");

        SharedPreferences.Editor cartEditor     = cart.edit();




        btnBuyNow.setOnClickListener(v -> {
            if (selectedColor == null || selectedSize == null) {
                Toast.makeText(getContext(), "Vui lòng chọn size và màu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentMaxStock == 0) {
                Toast.makeText(getContext(), "Sản phẩm này đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            if (product == null || product.getId() == null) {
                Toast.makeText(getContext(), "Lỗi sản phẩm: Không có ID", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isLogin.equals("0")){


                // Lấy list cũ ra trước
                List<LocalCartItem> cartList = getCart(getContext());
                boolean isExist = false;

                // Kiểm tra xem đã có sản phẩm này (cùng ID, Size, Màu) chưa
                for (LocalCartItem item : cartList) {
                    if (item.getId().equals(product.getId()) &&
                            item.getSize().equals(selectedSize) &&
                            item.getColor().equals(selectedColor)) {

                        item.setQuantity(item.getQuantity() + quantity); // Tăng số lượng
                        isExist = true;
                        break;
                    }
                }

                if (!isExist) {
                    product.setQuantity(quantity); // Mặc định lần đầu là 1
                    cartList.add(new LocalCartItem(product.getId(),product.getName(),product.getPrice(),quantity,product.getImage(),selectedSize,selectedColor));
                }

                // --- LƯU LẠI VÀO SHARED DƯỚI DẠNG GSON ---
                saveCartList(getContext(), cartList);



            }else {
                // ... Logic gọi API giữ nguyên ...
                CartRequest cartRequest = new CartRequest(product.getId(), product.getName(), selectedSize, selectedColor, quantity, product.getPrice());
                CartService cartService = ApiClientCart.getCartService(getContext());
                cartService.addToCart(cartRequest).enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.isSuccessful()) {
                            if (listener != null) {
                                listener.onOptionSelected(selectedSize, selectedColor, quantity);
                            }
                            dismiss();
                        } else {
                            Log.e("CartAPI", "Error Code: " + response.code());
                            Toast.makeText(getContext(), "Thêm vào giỏ thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        Log.e("CartAPI", "Failure: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
    // 3. Hàm phụ trợ để đóng gói List thành JSON và lưu
    // 3. Hàm phụ trợ để đóng gói List thành JSON và lưu
    private static void saveCartList(Context context, List<LocalCartItem> cartList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FilePrefs.LocalCart, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cartList); // Chuyển cả list mới thành 1 chuỗi JSON

        editor.putString(FilePrefs.KEY_CART, json);
        editor.apply();
    }

    @Override
    public int getTheme() { return R.style.BottomSheetDialogTheme; }
}