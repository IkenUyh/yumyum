package com.example.uitpayapp.merchant.notification;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class SellerNotificationAdapter extends RecyclerView.Adapter<SellerNotificationAdapter.NotificationViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<SellerNotification> notifications;
    private final OnItemClickListener listener;

    public SellerNotificationAdapter(List<SellerNotification> notifications, OnItemClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        SellerNotification notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvContent.setText(notification.getContent());
        holder.tvTime.setText(notification.getTime());

        if (notification.isRead()) {
            holder.unreadIndicator.setVisibility(View.GONE);
            holder.rootLayout.setBackgroundColor(Color.WHITE);
        } else {
            holder.unreadIndicator.setVisibility(View.VISIBLE);
            holder.rootLayout.setBackgroundColor(Color.parseColor("#FDF2F0"));
        }

        // Set icon dựa theo loại thông báo
        switch (notification.getType()) {
            case 1: // Order
                holder.ivIcon.setImageResource(R.drawable.list_alt_24px);
                break;
            case 2: // Promo
                holder.ivIcon.setImageResource(R.drawable.ic_discount_voucher);
                break;
            case 4: // Review
                holder.ivIcon.setImageResource(android.R.drawable.btn_star_big_on);
                break;
            default: // System
                holder.ivIcon.setImageResource(R.drawable.notifications_24px);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        ImageView ivIcon;
        View unreadIndicator, rootLayout;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }
}
