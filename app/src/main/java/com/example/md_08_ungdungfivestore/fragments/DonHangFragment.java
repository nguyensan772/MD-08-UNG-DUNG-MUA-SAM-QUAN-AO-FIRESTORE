package com.example.md_08_ungdungfivestore.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.DonHangAdapter;
import com.example.md_08_ungdungfivestore.models.ApiResponse;
import com.example.md_08_ungdungfivestore.models.Order;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.OrderApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonHangFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;
    private RecyclerView recyclerView;
    private DonHangAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private OrderApiService orderApiService;

    public DonHangFragment() {
        // Required empty public constructor
    }

    public static DonHangFragment newInstance(String status) {
        DonHangFragment fragment = new DonHangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_don_hang, container, false);

        recyclerView = view.findViewById(R.id.rcvDonHang);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DonHangAdapter(getContext(), orderList, this::showCancelConfirmation, this::handleReorder);
        recyclerView.setAdapter(adapter);

        orderApiService = ApiClient.getClient().create(OrderApiService.class);

        loadOrders();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        // For "shipping" tab, we need to get all orders and filter client-side
        // because backend might have different status names (confirmed, processing,
        // shipping)
        orderApiService.getMyOrders().enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    if (response.body().getData() != null) {
                        List<Order> allOrders = response.body().getData();

                        // Filter based on tab
                        for (Order order : allOrders) {
                            if (shouldShowOrder(order)) {
                                orderList.add(order);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();

                    Log.d("DonHangFragment", "Status filter: " + status + ", Orders count: " + orderList.size());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi tải đơn hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean shouldShowOrder(Order order) {
        if (status == null || status.isEmpty()) {
            return true; // Show all
        }

        String orderStatus = order.getStatus();
        if (orderStatus == null)
            return false;

        switch (status) {
            case "pending":
                return orderStatus.equals("pending");
            case "shipping":
                // Tab "Đang giao" includes: confirmed, processing, shipping
                return orderStatus.equals("confirmed") ||
                        orderStatus.equals("processing") ||
                        orderStatus.equals("shipping");
            case "delivered":
                return orderStatus.equals("delivered");
            case "cancelled":
                return orderStatus.equals("cancelled");
            default:
                return orderStatus.equals(status);
        }
    }

    private void showCancelConfirmation(Order order) {
        if (getContext() == null)
            return;

        new AlertDialog.Builder(getContext())
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelOrder(order.get_id()))
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void cancelOrder(String orderId) {
        orderApiService.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (getContext() == null)
                    return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                    loadOrders(); // Reload list
                } else {
                    Toast.makeText(getContext(), "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleReorder(Order order) {
        if (getContext() == null)
            return;

        new AlertDialog.Builder(getContext())
                .setTitle("Mua lại")
                .setMessage("Bạn có muốn mua lại đơn hàng này?")
                .setPositiveButton("Mua lại", (dialog, which) -> {
                    // TODO: Add items to cart and navigate to checkout
                    Toast.makeText(getContext(), "Đang thêm vào giỏ hàng...", Toast.LENGTH_SHORT).show();
                    addOrderItemsToCart(order);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addOrderItemsToCart(Order order) {
        // This would add all items from the order back to cart
        // For now, just show a message
        if (getContext() != null) {
            Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        }
    }
}
