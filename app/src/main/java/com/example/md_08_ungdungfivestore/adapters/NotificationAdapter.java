package com.example.md_08_ungdungfivestore.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.ManChiTietDonHang;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.Notification;
import com.example.md_08_ungdungfivestore.services.ThongBaoApiClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> list;
    private Context context;
    private final String BASE_URL_IMAGE = "https://bruce-brutish-duane.ngrok-free.dev";

    public NotificationAdapter(List<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());

        // ⭐ GIỮ NGUYÊN LOGIC LOAD ẢNH CỦA ÔNG
        String imageUrl = item.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String fullUrl;
            if (imageUrl.startsWith("http")) {
                fullUrl = imageUrl;
            } else {
                if (imageUrl.contains("uploads")) {
                    fullUrl = "https://bruce-brutish-duane.ngrok-free.dev/" + (imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl);
                } else {
                    fullUrl = "https://bruce-brutish-duane.ngrok-free.dev/uploads/" + (imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl);
                }
            }
            Log.e("CHECK_ANH", "Link cuoi cung: " + fullUrl);
            Glide.with(context)
                    .load(fullUrl)
                    .placeholder(R.drawable.avatar_img)
                    .error(R.drawable.ic_error)
                    .into(holder.imgNotif);
        } else {
            holder.imgNotif.setImageResource(R.drawable.avatar_img);
        }

        // ⭐ GIỮ NGUYÊN LOGIC THỜI GIAN CỦA ÔNG
        if (item.getCreatedAt() != null) {
            try {
                SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                sdfInput.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdfInput.parse(item.getCreatedAt());
                SimpleDateFormat sdfOutput = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                holder.tvTime.setText(sdfOutput.format(date));
            } catch (Exception e) {
                holder.tvTime.setText(item.getCreatedAt());
            }
        }

        // ⭐ HIỂN THỊ DẤU CHẤM XANH (viewUnread)
        if (item.isRead()) {
            holder.viewUnread.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.WHITE);
        } else {
            holder.viewUnread.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }

        // ⭐ CLICK: XÓA DẤU CHẤM + MỞ CHI TIẾT ĐƠN HÀNG
        holder.itemView.setOnClickListener(v -> {
            // 1. Nếu chưa đọc thì gọi API đánh dấu đã đọc
            if (!item.isRead()) {
                ThongBaoApiClient.getClient(context).danhDauDaDocTheoId(item.get_id()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            item.setRead(true);
                            notifyItemChanged(holder.getAdapterPosition()); // Vẽ lại item để mất dấu chấm ngay
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("API_READ", "Lỗi: " + t.getMessage());
                    }
                });
            }

            // 2. Mở màn hình Chi Tiết Đơn Hàng (Sử dụng ORDER_ID)
            String orderId = item.getOrder_id();
            if (orderId != null && !orderId.isEmpty()) {
                Intent intent = new Intent(context, ManChiTietDonHang.class);
                intent.putExtra("ORDER_ID", orderId);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return list != null ? list.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView imgNotif;
        View viewUnread;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitleNotif);
            tvMessage = itemView.findViewById(R.id.tvMessageNotif);
            tvTime = itemView.findViewById(R.id.tvTimeNotif);
            imgNotif = itemView.findViewById(R.id.imgNotif);
            viewUnread = itemView.findViewById(R.id.viewUnread);
        }
    }
}