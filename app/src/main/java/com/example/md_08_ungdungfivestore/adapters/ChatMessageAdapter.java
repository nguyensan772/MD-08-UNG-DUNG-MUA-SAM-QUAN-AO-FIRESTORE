package com.example.md_08_ungdungfivestore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_SUPPORT = 2;

    private List<ChatMessage> messages = new ArrayList<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatMessageAdapter() {
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.isFromUser() ? VIEW_TYPE_USER : VIEW_TYPE_SUPPORT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_chat_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_message_support, parent, false);
            return new SupportMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        String formattedTime = timeFormat.format(new Date(message.getTimestamp()));

        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
            userHolder.tvMessage.setText(message.getMessage());
            userHolder.tvTime.setText(formattedTime);
        } else if (holder instanceof SupportMessageViewHolder) {
            SupportMessageViewHolder supportHolder = (SupportMessageViewHolder) holder;
            supportHolder.tvMessage.setText(message.getMessage());
            supportHolder.tvTime.setText(formattedTime);
            
            // Show sender type label (Bot or Admin)
            String senderType = message.getSenderType();
            if ("bot".equals(senderType)) {
                supportHolder.tvSenderLabel.setText("Bot");
                supportHolder.tvSenderLabel.setVisibility(View.VISIBLE);
            } else if ("admin".equals(senderType)) {
                supportHolder.tvSenderLabel.setText("Admin");
                supportHolder.tvSenderLabel.setVisibility(View.VISIBLE);
            } else {
                supportHolder.tvSenderLabel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTime;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageUser);
            tvTime = itemView.findViewById(R.id.tvTimeUser);
        }
    }

    static class SupportMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTime;
        TextView tvSenderLabel;

        SupportMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageSupport);
            tvTime = itemView.findViewById(R.id.tvTimeSupport);
            tvSenderLabel = itemView.findViewById(R.id.tvSenderLabel);
        }
    }
}

