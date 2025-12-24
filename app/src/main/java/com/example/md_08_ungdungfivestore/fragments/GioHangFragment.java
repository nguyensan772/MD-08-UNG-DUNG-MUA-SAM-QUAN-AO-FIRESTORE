package com.example.md_08_ungdungfivestore.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.CheckoutActivity;
import com.example.md_08_ungdungfivestore.DangNhap;
import com.example.md_08_ungdungfivestore.FilePrefs;
import com.example.md_08_ungdungfivestore.MainActivity;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.CartAdapter;
import com.example.md_08_ungdungfivestore.adapters.LocalCartAdapter;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.CartResponse;
import com.example.md_08_ungdungfivestore.models.LocalCartItem;
import com.example.md_08_ungdungfivestore.models.QuantityUpdate;
import com.example.md_08_ungdungfivestore.services.CartService;
import com.example.md_08_ungdungfivestore.services.LocalCartClient;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.example.md_08_ungdungfivestore.services.ApiClientCart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GioHangFragment extends Fragment implements CartAdapter.CartItemActionListener, LocalCartAdapter.LocalCartItemActionListener {

    private RecyclerView rcvCart;
    private TextView tvTotal;
    private MaterialButton thanhToanBtn;
    // private TextView tvEmptyCart; // Bỏ comment nếu muốn dùng

    private final List<CartItem> cartItems = new ArrayList<>();
    private List<LocalCartItem> localCartItems = new ArrayList<>();
    private CartAdapter cartAdapter;
    private LocalCartAdapter localCartAdapter;
    private CartService cartService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gio_hang, container, false);

        rcvCart = view.findViewById(R.id.rcvCart);
        tvTotal = view.findViewById(R.id.tvTotal);
        thanhToanBtn = view.findViewById(R.id.thanhToanBtn);
        // tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(FilePrefs.MyAppPrefs, getContext().MODE_PRIVATE);
        String isLogin = sharedPreferences.getString("isLogin","0");


        // Kiểm tra context an toàn
        if (getContext() != null) {



            rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));



            if (isLogin.equals("0")){
                localCartItems = getCart(getContext());
                localCartAdapter = new LocalCartAdapter(getContext(), localCartItems, (LocalCartAdapter.LocalCartItemActionListener) this);
                rcvCart.setAdapter(localCartAdapter);
                updateTotalLocalPrice(localCartItems);
            }else {
                cartService = ApiClientCart.getCartService(getContext());
                cartAdapter = new CartAdapter(getContext(), cartItems, (CartAdapter.CartItemActionListener) this);
                rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));
                rcvCart.setAdapter(cartAdapter);
                // Tải giỏ hàng lần đầu
                fetchCartItems();
            }

        }

        thanhToanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin.equals("0")){
                    showLogoutDialog();

                }else {
                    navigateToCheckout();
                }

            }
        });





        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Không gọi fetch ở đây để tránh load chồng chéo
    }

    private void navigateToCheckout() {
        if (cartItems.isEmpty()) {
            // Kiểm tra getContext() trước khi Toast
            if (getContext() != null) {
                Toast.makeText(getContext(), "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (getActivity() instanceof MainActivity) {
            Intent intent = new Intent(getContext(), CheckoutActivity.class);
            ((MainActivity) getActivity()).getCheckoutResultLauncher().launch(intent);
        } else if (getContext() != null) {
            Intent intent = new Intent(getContext(), CheckoutActivity.class);
            startActivity(intent);
        }
    }

    // --- LOGIC TẢI DỮ LIỆU TỪ API ---
    public void fetchCartItems() {
        if (cartService == null) return;

        cartService.getCartItems().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                // ⭐ SỬA LỖI CRASH: Nếu Fragment đã bị đóng, dừng ngay lập tức
                if (getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> newItems = response.body().getItems();

                    cartItems.clear();
                    if (newItems != null) {
                        cartItems.addAll(newItems);
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice(cartItems);
                } else {
                    Toast.makeText(getContext(), "Lỗi tải giỏ hàng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                // ⭐ SỬA LỖI CRASH: Kiểm tra context null trước khi Toast
                if (getContext() == null) return;

                Toast.makeText(getContext(), "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onQuantityChange(CartItem item, int newQuantity) {
        if (cartService == null || newQuantity < 1) return;

        QuantityUpdate requestBody = new QuantityUpdate(newQuantity);

        cartService.updateQuantity(item.getId(), requestBody).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                // ⭐ SỬA LỖI CRASH
                if (getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> updatedItems = response.body().getItems();

                    if (updatedItems != null) {
                        cartItems.clear();
                        cartItems.addAll(updatedItems);
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice(cartItems);
                    } else {
                        Toast.makeText(getContext(), "Lỗi dữ liệu. Đang đồng bộ lại...", Toast.LENGTH_SHORT).show();
                        fetchCartItems();
                    }
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại. Tải lại.", Toast.LENGTH_SHORT).show();
                    fetchCartItems();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                // ⭐ SỬA LỖI CRASH
                if (getContext() == null) return;
                Toast.makeText(getContext(), "Lỗi mạng khi cập nhật số lượng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDelete(CartItem item) {
        if (cartService == null) return;

        cartService.deleteItem(item.getId()).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(@NonNull Call<CartResponse> call, @NonNull Response<CartResponse> response) {
                // ⭐ SỬA LỖI CRASH
                if (getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<CartItem> updatedItems = response.body().getItems();

                    if (updatedItems != null) {
                        cartItems.clear();
                        cartItems.addAll(updatedItems);
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice(cartItems);

                        if (response.body().getMessage() != null) {
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi dữ liệu. Đang đồng bộ lại...", Toast.LENGTH_SHORT).show();
                        fetchCartItems();
                    }
                } else {
                    Toast.makeText(getContext(), "Xóa sản phẩm thất bại. Tải lại.", Toast.LENGTH_SHORT).show();
                    fetchCartItems();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartResponse> call, @NonNull Throwable t) {
                // ⭐ SỬA LỖI CRASH
                if (getContext() == null) return;
                Toast.makeText(getContext(), "Lỗi mạng khi xóa sản phẩm.", Toast.LENGTH_SHORT).show();
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



    //Thêm sản phẩm giỏ hàng Vãng lai
    @Override
    public void onQuantityLocalChange(LocalCartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        saveCart(getContext(), localCartItems);

        localCartAdapter.notifyDataSetChanged();
        updateTotalLocalPrice(localCartItems);
    }
    public static void saveCart(Context context, List<LocalCartItem> cartList) {
        // 1. Mở file SharedPreferences tên là "local_cart_prefs" ở chế độ riêng tư
        SharedPreferences sharedPreferences = context.getSharedPreferences(FilePrefs.LocalCart, Context.MODE_PRIVATE);

        // 2. Mở trình chỉnh sửa (Editor) để bắt đầu ghi dữ liệu
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 3. Sử dụng GSON để biến toàn bộ danh sách (List) thành 1 chuỗi JSON duy nhất
        Gson gson = new Gson();
        String json = gson.toJson(cartList);

        // 4. Lưu chuỗi JSON đó vào Key có tên là "cart_list"
        editor.putString(FilePrefs.KEY_CART, json);

        // 5. Xác nhận lưu (apply sẽ chạy ngầm, không làm lag máy)
        editor.apply();
    }

    @Override
    public void onDeleteLocal(LocalCartItem item) {
        localCartItems.remove(item);
        saveCart(getContext(), localCartItems);

        localCartAdapter.notifyDataSetChanged();
        updateTotalLocalPrice(localCartItems);
    }
    public void updateTotalPrice(List<CartItem> list) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }

        String formattedTotal = String.format("%,.0f VNĐ", total);
        tvTotal.setText(formattedTotal);
    }
    public void updateTotalLocalPrice(List<LocalCartItem> list) {
        double total = 0;
        for (LocalCartItem item : localCartItems) {
            total += item.getPrice() * item.getQuantity();
        }

        String formattedTotal = String.format("%,.0f VNĐ", total);
        tvTotal.setText(formattedTotal);
    }

    private void showLogoutDialog() {
        DialogDangNhap dialog = DialogDangNhap.newInstance(
                "Đăng nhập",
                "Bạn có chắc chắn muốn đăng nhập không?",
                new DialogDangNhap.OnDialogAction() {
                    @Override
                    public void onConfirm() {
                        startActivity(new Intent(getContext(), DangNhap.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                }
        );

        // Hiển thị Dialog
        dialog.show(getActivity().getSupportFragmentManager(), "custom_dialog");
    }
}