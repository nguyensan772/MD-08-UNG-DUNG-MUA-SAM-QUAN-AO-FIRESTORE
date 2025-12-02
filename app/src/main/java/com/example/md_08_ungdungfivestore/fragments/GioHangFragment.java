package com.example.md_08_ungdungfivestore.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.ManDatHang;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.GioHangAdapter;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Cart;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.UpdateCartRequest;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.CartApiService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GioHangFragment extends Fragment {

    private RecyclerView recyclerView;
    private GioHangAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private Set<String> selectedItemIds = new HashSet<>();
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private CartApiService cartApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gio_hang, container, false);

        // Initialize views - Match với XML IDs
        recyclerView = view.findViewById(R.id.rcvCart);
        tvTotalAmount = view.findViewById(R.id.tvTotal);
        btnCheckout = view.findViewById(R.id.thanhToanBtn);
        TextView btnClearCart = view.findViewById(R.id.btnClearCart);

        // Clear Cart button
        btnClearCart.setOnClickListener(v -> showClearCartConfirmation());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GioHangAdapter(getContext(), cartItems, new GioHangAdapter.CartListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                // Optimistic Update: Update UI immediately
                int oldQuantity = item.getQuantity();
                item.setQuantity(newQuantity);
                adapter.notifyItemChanged(cartItems.indexOf(item));
                updateTotalForSelectedItems();

                // Call API in background
                updateCartItemQuantity(item.get_id(), newQuantity, item, oldQuantity);
            }

            @Override
            public void onRemoveItem(CartItem item) {
                removeCartItem(item.get_id());
            }

            @Override
            public void onItemSelected(CartItem item, boolean isSelected) {
                if (isSelected) {
                    selectedItemIds.add(item.get_id());
                } else {
                    selectedItemIds.remove(item.get_id());
                }
                updateTotalForSelectedItems();
            }

            @Override
            public void onDeleteClick(CartItem item) {
                showDeleteConfirmation(item);
            }
        });
        recyclerView.setAdapter(adapter);

        // Initialize API
        cartApiService = ApiClient.getClient().create(CartApiService.class);

        // Load cart
        loadCart();

        // Checkout button
        btnCheckout.setOnClickListener(v -> {
            if (selectedItemIds.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            } else {
                navigateToCheckout();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        cartApiService.getCart().enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Cart> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Cart cart = apiResponse.getData();
                        cartItems.clear();
                        if (cart.getItems() != null) {
                            cartItems.addAll(cart.getItems());
                        }
                        adapter.notifyDataSetChanged();
                        updateUI();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartItemQuantity(String itemId, int newQuantity, CartItem item, int oldQuantity) {
        UpdateCartRequest request = new UpdateCartRequest(newQuantity);

        cartApiService.updateCartItem(itemId, request).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (!response.isSuccessful()) {
                    android.util.Log.e("GioHangFragment", "Update failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                android.util.Log.e("GioHangFragment", "Update error: " + t.getMessage());
            }
        });
    }

    private void showClearCartConfirmation() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa giỏ hàng")
                .setMessage("Bạn có chắc muốn xóa tất cả sản phẩm?")
                .setPositiveButton("Xóa", (dialog, which) -> clearCartAPI())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void clearCartAPI() {
        cartApiService.clearCart().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    cartItems.clear();
                    selectedItemIds.clear();
                    adapter.notifyDataSetChanged();
                    updateUI();
                    Toast.makeText(getContext(), "Đã xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCartItem(String itemId) {
        cartApiService.removeCartItem(itemId).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    loadCart(); // Reload cart
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
            tvTotalAmount.setText("0 VND");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            updateTotalForSelectedItems();
        }
    }

    private void updateTotalForSelectedItems() {
        double total = 0;
        boolean hasSelection = false;

        for (CartItem item : cartItems) {
            if (selectedItemIds.contains(item.get_id())) {
                total += item.getSubtotal();
                hasSelection = true;
            }
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalAmount.setText(formatter.format(total) + " VND");
        btnCheckout.setEnabled(hasSelection);
    }

    private void showDeleteConfirmation(CartItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa \"" + item.getName() + "\" khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    removeCartItem(item.get_id());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void navigateToCheckout() {
        // Get selected items
        ArrayList<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (selectedItemIds.contains(item.get_id())) {
                selectedItems.add(item);
            }
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to ManDatHang
        Intent intent = new Intent(getContext(), ManDatHang.class);
        intent.putExtra("selectedItems", selectedItems);
        startActivity(intent);
    }
}
