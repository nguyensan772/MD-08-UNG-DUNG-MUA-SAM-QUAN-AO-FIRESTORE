package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.OrderResponse;
import com.example.md_08_ungdungfivestore.models.OrderItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    // ĐỊA CHỈ SERVER
    private static final String SERVER_BASE_URL = "http://10.0.2.2:5001";

    private final Context context;
    private final List<OrderResponse> orderList;
    private OnOrderActionListener actionListener;

    public interface OnOrderActionListener {
        void onCancelClick(OrderResponse order, int position);
        void onRateClick(OrderResponse order);
        void onDetailClick(OrderResponse order);
    }

    public OrderAdapter(Context context, List<OrderResponse> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);
        if (order == null) return;

        holder.tvTotalPrice.setText(formatCurrency(order.getTotalAmount()));
        holder.tvStatus.setText(getStatusVietnamese(order.getStatus()));

        int totalQuantity = 0;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                totalQuantity += item.getQuantity();
            }
        }
        holder.tvProductCount.setText("x " + totalQuantity + " sản phẩm");

        // --- XỬ LÝ ẢNH SẢN PHẨM ĐẦU TIÊN ---
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            holder.tvProductName.setText(firstItem.getProductName());

            String imagePath = firstItem.getImageUrl(); // Lấy đường dẫn ảnh
            String fullImageUrl;

            if (imagePath != null && !imagePath.isEmpty()) {
                // Logic xử lý URL thông minh
                if (imagePath.startsWith("http")) {
                    fullImageUrl = imagePath;
                } else {
                    String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
                    if (cleanPath.startsWith("uploads")) {
                        fullImageUrl = SERVER_BASE_URL + "/" + cleanPath;
                    } else {
                        fullImageUrl = SERVER_BASE_URL + "/uploads/" + cleanPath;
                    }
                }

                Glide.with(context)
                        .load(fullImageUrl)
                        .placeholder(R.drawable.avatar_img) // Nhớ đổi tên ảnh placeholder nếu cần
                        .error(R.drawable.ic_error)
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(R.drawable.avatar_img);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.avatar_img);
            holder.tvProductName.setText("Không có sản phẩm");
        }
        // -----------------------------------

        setupActionButtons(holder, order, position);

        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDetailClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String formatCurrency(double amount) {
        if (amount <= 0) return "0 VND";
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount).replace("₫", "VND").trim();
    }

    private String getStatusVietnamese(String status) {
        switch (status) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "processing": return "Đang lấy hàng";
            case "shipping": return "Đang giao hàng";
            case "delivered": return "Đã giao";
            case "cancelled": return "Đã hủy";
            default: return "Đang xử lý";
        }
    }

    private void setupActionButtons(OrderViewHolder holder, OrderResponse order, int position) {
        holder.btnAction1.setVisibility(View.GONE);
        switch (order.getStatus()) {
            case "pending":
                holder.btnAction1.setText("HỦY ĐƠN");
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction1.setOnClickListener(v -> {
                    if (actionListener != null) actionListener.onCancelClick(order, position);
                });
                break;
            case "delivered":
                holder.btnAction1.setText("ĐÁNH GIÁ");
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction1.setOnClickListener(v -> {
                    if (actionListener != null) actionListener.onRateClick(order);
                });
                break;
        }
    }

    public void removeItem(int position) {
        orderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductCount, tvStatus, tvTotalPrice, tvProductName;
        Button btnAction1;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnAction1 = itemView.findViewById(R.id.btnAction1);
            tvProductName = itemView.findViewById(R.id.tvProductName);
        }
    }
}