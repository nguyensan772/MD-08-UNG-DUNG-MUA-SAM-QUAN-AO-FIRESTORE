package com.example.md_08_ungdungfivestore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> list;

    public CommentAdapter(List<Comment> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = list.get(position);
        holder.tvUser.setText(comment.getUserName());
        holder.tvContent.setText(comment.getContent());
        holder.ratingBar.setRating(comment.getRating());

        // Cắt ngày tháng an toàn
        if (comment.getCreatedAt() != null && comment.getCreatedAt().length() >= 10) {
            holder.tvDate.setText(comment.getCreatedAt().substring(0, 10));
        } else {
            holder.tvDate.setText("Mới đây");
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser, tvContent, tvDate;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvCommentUser);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvDate = itemView.findViewById(R.id.tvCommentDate);
            ratingBar = itemView.findViewById(R.id.ratingBarComment);
        }
    }
}