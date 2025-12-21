package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.OrderAdapter;
import com.example.md_08_ungdungfivestore.models.OrderResponse;
import com.example.md_08_ungdungfivestore.models.OrderItem;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich;
import com.example.md_08_ungdungfivestore.services.OrderService;
import com.example.md_08_ungdungfivestore.ManDanhGiaSanPham;
import com.example.md_08_ungdungfivestore.ManChiTietDonHang;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaGiaoFragment extends Fragment implements OrderAdapter.OnOrderActionListener {

    private RecyclerView rcvOrders;
    private LinearLayout tvEmpty;
    private TextView tvEmptyText;
    private OrderAdapter orderAdapter;
    private final List<OrderResponse> orderList = new ArrayList<>();
    private OrderService orderService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        rcvOrders = view.findViewById(R.id.rcvOrders);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvEmptyText = view.findViewById(R.id.tvEmptyText);

        orderService = ApiClientYeuThich.getClient(getContext()).create(OrderService.class);
        orderAdapter = new OrderAdapter(getContext(), orderList);
        orderAdapter.setOnOrderActionListener(this);

        rcvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvOrders.setAdapter(orderAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchOrders("delivered");
    }

    // --- PHẦN XEM CHI TIẾT ĐƠN HÀNG (GIỮ LẠI CHO BẠN) ---
    @Override
    public void onDetailClick(OrderResponse order) {
        Intent intent = new Intent(getContext(), ManChiTietDonHang.class);
        intent.putExtra("ORDER_ID", order.getOrderId()); // Truyền mã đơn hàng sang
        startActivity(intent);
    }

    // --- PHẦN ĐÁNH GIÁ SẢN PHẨM ---
    @Override
    public void onRateClick(OrderResponse order) {
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);

            Intent intent = new Intent(getContext(), ManDanhGiaSanPham.class);

            // Sử dụng getProductId() mới đã xử lý Object ở bước trước
            intent.putExtra("PRODUCT_ID", firstItem.getProductId());
            intent.putExtra("PRODUCT_NAME", firstItem.getProductName());

            String imagePath = firstItem.getImageUrl();
            String fullImageUrl = "";
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("http")) {
                    fullImageUrl = imagePath;
                } else {
                    String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
                    fullImageUrl = "http://10.0.2.2:5001/" + cleanPath;
                }
            }
            intent.putExtra("PRODUCT_IMAGE", fullImageUrl);

            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Đơn hàng không có sản phẩm để đánh giá", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onCancelClick(OrderResponse order, int position) {}

    private void fetchOrders(String status) {
        orderService.getMyOrdersByStatus(status).enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderResponse>> call, @NonNull Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponse> responseList = response.body();
                    android.util.Log.d("ORDER_DEBUG", "Trạng thái: " + status + " | Số lượng: " + responseList.size());

                    orderList.clear();
                    orderList.addAll(responseList);
                    orderAdapter.notifyDataSetChanged();
                } else {
                    android.util.Log.e("ORDER_DEBUG", "Lỗi Server: " + response.code());
                }
                updateEmptyView();
            }

            @Override public void onFailure(@NonNull Call<List<OrderResponse>> call, @NonNull Throwable t) {
                android.util.Log.e("ORDER_DEBUG", "Lỗi Mạng: " + t.getMessage());
                updateEmptyView();
            }
        });
    }

    private void updateEmptyView() {
        if (isAdded()) { // Đảm bảo fragment vẫn còn tồn tại
            tvEmpty.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
            rcvOrders.setVisibility(orderList.isEmpty() ? View.GONE : View.VISIBLE);
            if(orderList.isEmpty()) tvEmptyText.setText("Không có đơn hàng nào đã được giao.");
        }
    }
}