package com.example.uitpayapp.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.PromoViewHolder> {

    private List<PromoNotification> promoList;
    private Runnable onReadCallback;

    public PromoAdapter(List<PromoNotification> promoList, Runnable onReadCallback) {
        this.promoList = promoList;
        this.onReadCallback = onReadCallback;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notification_item_promo, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        PromoNotification item = promoList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());
        holder.tvTime.setText(item.getTimestamp());
        holder.ivImage.setImageResource(item.getImageResId());

        // Thay đổi giao diện dựa trên trạng thái đã đọc
        if (item.isRead()) {
            holder.tvTitle.setTypeface(null, android.graphics.Typeface.NORMAL);
            holder.tvTitle.setTextColor(android.graphics.Color.parseColor("#757575"));
            holder.itemView.setBackgroundColor(android.graphics.Color.WHITE);
        } else {
            holder.tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            holder.tvTitle.setTextColor(android.graphics.Color.parseColor("#212121"));
            holder.itemView.setBackgroundColor(android.graphics.Color.parseColor("#FFF5F2")); // Nền cam nhạt cho thông báo chưa đọc
        }

        holder.itemView.setOnClickListener(v -> {
            if (!item.isRead()) {
                new com.example.uitpayapp.modules.notification.NotificationRepository().markAsRead(Long.parseLong(item.getId()), new com.example.uitpayapp.network.ApiCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        item.setRead(true);
                        int currentPos = holder.getAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            notifyItemChanged(currentPos);
                        }
                        if (onReadCallback != null) {
                            onReadCallback.run();
                        }
                        com.example.uitpayapp.utils.NotificationBadgeHelper.sendUpdateBroadcast(v.getContext());
                    }

                    @Override
                    public void onError(String errorMessage) {}
                });
            }
        });
    }

    @Override
    public int getItemCount() { return promoList.size(); }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvTime;
        ImageView ivImage;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPromoTitle);
            tvBody = itemView.findViewById(R.id.tvPromoBody);
            tvTime = itemView.findViewById(R.id.tvPromoTime);
            ivImage = itemView.findViewById(R.id.ivPromoImage);
        }
    }
}