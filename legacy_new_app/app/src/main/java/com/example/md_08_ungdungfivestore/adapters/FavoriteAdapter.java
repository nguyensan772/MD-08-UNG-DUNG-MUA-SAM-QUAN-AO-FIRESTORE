package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.config.AppConfig;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.YeuThichManager;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private Context context;
    private List<Product> productList;
    private YeuThichManager yeuThichManager;
    private OnFavoriteClickListener onFavoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product);
    }

    public FavoriteAdapter(Context context, List<Product> productList, YeuThichManager yeuThichManager, OnFavoriteClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.yeuThichManager = yeuThichManager;
        this.onFavoriteClickListener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());

        // Định dạng giá tiền
        holder.tvPrice.setText(String.format("%,d đ", (int) product.getPrice()));

        // --- XỬ LÝ ẢNH (LOGIC MỚI) ---
        String imagePath = product.getImage();

        if (imagePath != null && !imagePath.isEmpty()) {
            String fullImageUrl;

            // Trường hợp 1: Ảnh là link online (Cloudinary, Firebase...)
            if (imagePath.startsWith("http")) {
                fullImageUrl = imagePath;
            }
            // Trường hợp 2: Ảnh lưu local server
            else {
                // Xóa dấu gạch chéo đầu nếu có để tránh thành //uploads
                String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;

                // Kiểm tra xem trong DB lưu có chữ "uploads" chưa
                if (cleanPath.startsWith("uploads")) {
                    // Nếu có rồi: http://...:5001/uploads/anh.jpg
                    fullImageUrl = AppConfig.BASE_URL + "/" + cleanPath;
                } else {
                    // Nếu chưa có: http://...:5001/uploads/anh.jpg
                    fullImageUrl = AppConfig.BASE_URL + "/uploads/" + cleanPath;
                }
            }

            // In Log để kiểm tra xem đường dẫn đúng chưa (Xem trong Logcat)
            Log.d("FavoriteAdapter", "Link ảnh cuối cùng: " + fullImageUrl);

            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.avatar_img) // Đổi thành ảnh chờ của bạn
                    .error(R.drawable.ic_error)           // Đổi thành icon lỗi của bạn
                    .into(holder.ivProduct);
        } else {
            // Không có link ảnh trong DB
            holder.ivProduct.setImageResource(R.drawable.avatar_img);
        }

        // Sự kiện click nút tim (Xóa yêu thích)
        holder.btnFavorite.setOnClickListener(v -> {
            if (onFavoriteClickListener != null) {
                onFavoriteClickListener.onFavoriteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice;
        ImageButton btnFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}