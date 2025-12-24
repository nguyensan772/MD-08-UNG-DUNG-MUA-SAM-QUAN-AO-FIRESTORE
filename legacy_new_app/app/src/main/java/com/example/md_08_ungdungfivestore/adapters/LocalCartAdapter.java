package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.CartItem;
import com.example.md_08_ungdungfivestore.models.LocalCartItem;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.LocalCartClient;
import com.example.md_08_ungdungfivestore.services.ProductApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalCartAdapter extends RecyclerView.Adapter<LocalCartAdapter.LocalCartViewHolder> {

    private final Context context;
    private final List<LocalCartItem> cartItemList;
    private final LocalCartItemActionListener actionListener;

    // Địa chỉ Server
    private static final String BASE_URL = "https://bruce-brutish-duane.ngrok-free.dev";

    public interface LocalCartItemActionListener {
        void onQuantityLocalChange(LocalCartItem item, int newQuantity);
        void onDeleteLocal(LocalCartItem item);
    }

    public LocalCartAdapter(Context context, List<LocalCartItem> cartItemList, LocalCartItemActionListener actionListener) {
        this.context = context;
        this.cartItemList = cartItemList;

        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public LocalCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new LocalCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalCartViewHolder holder, int position) {
        LocalCartItem item = cartItemList.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(String.format("%,.0f VNĐ", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSize.setText(String.format("Size: %s | Màu: %s", item.getSize(), item.getColor()));
        fetchProductDetail(item.getId(),holder.imgProduct);

        holder.btnIncrease.setOnClickListener(v ->
                actionListener.onQuantityLocalChange(item, item.getQuantity() + 1)
        );

        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity > 1) {
                actionListener.onQuantityLocalChange(item, currentQuantity - 1);
            } else {
                actionListener.onDeleteLocal(item);
            }
        });


    }



    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class LocalCartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice, tvSize, tvQuantity;
        ImageButton btnIncrease, btnDecrease;

        public LocalCartViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }

    public void fetchProductDetail(String productId,ImageView imgView) {
        ProductApiService apiService = LocalCartClient.getClient().create(ProductApiService.class);

        apiService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();

                    // 1. Hiển thị lên giao diện (Ví dụ)
                    // tvName.setText(product.getProductName());
                    Glide.with(context)
                            .load(product.getImage())
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_background)
                                    .into(imgView);

                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi kết nối ngrok: " + t.getMessage());
            }
        });
    }
}