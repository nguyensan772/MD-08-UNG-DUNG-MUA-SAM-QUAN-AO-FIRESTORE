package com.example.md_08_ungdungfivestore.fragments;

import android.app.AlertDialog;
import android.content.Intent;
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

import com.example.md_08_ungdungfivestore.ManChiTietDonHang;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.OrderAdapter;
import com.example.md_08_ungdungfivestore.models.OrderResponse;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich;
import com.example.md_08_ungdungfivestore.services.OrderService;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaXacNhanFragment extends Fragment implements OrderAdapter.OnOrderActionListener {

    private RecyclerView rcvOrders;
    private View emptyViewContainer;
    private TextView tvEmptyText;
    private OrderAdapter orderAdapter;
    private final List<OrderResponse> orderList = new ArrayList<>();
    private OrderService orderService;

    // QUAN TRỌNG: Đổi status thành confirmed
    private static final String STATUS = "confirmed";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        rcvOrders = view.findViewById(R.id.rcvOrders);
        emptyViewContainer = view.findViewById(R.id.tvEmpty);
        tvEmptyText = emptyViewContainer.findViewById(R.id.tvEmptyText);

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
        fetchOrders(STATUS);
    }

    @Override
    public void onCancelClick(OrderResponse order, int position) {
        // Backend quy định: Đã xác nhận thì User không tự hủy được nữa
        new AlertDialog.Builder(getContext())
                .setTitle("Thông báo")
                .setMessage("Đơn hàng đã được xác nhận. Vui lòng liên hệ CSKH nếu bạn muốn hủy.")
                .setPositiveButton("Đóng", null)
                .show();
    }

    @Override
    public void onRateClick(OrderResponse order) {
        // Không làm gì
    }

    @Override
    public void onDetailClick(OrderResponse order) {
        Intent intent = new Intent(getContext(), ManChiTietDonHang.class);
        intent.putExtra("ORDER_ID", order.getOrderId());
        startActivity(intent);
    }

    private void fetchOrders(String status) {
        if (orderService == null) return;
        orderService.getMyOrdersByStatus(status).enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderResponse>> call, @NonNull Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();
                    updateEmptyView();
                } else {
                    updateEmptyView();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderResponse>> call, @NonNull Throwable t) {
                updateEmptyView();
            }
        });
    }

    private void updateEmptyView() {
        if (orderList.isEmpty()) {
            emptyViewContainer.setVisibility(View.VISIBLE);
            rcvOrders.setVisibility(View.GONE);
            tvEmptyText.setText("Không có đơn hàng nào đã xác nhận.");
        } else {
            emptyViewContainer.setVisibility(View.GONE);
            rcvOrders.setVisibility(View.VISIBLE);
        }
    }
}