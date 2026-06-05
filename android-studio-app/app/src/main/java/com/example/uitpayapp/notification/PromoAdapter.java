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

    public PromoAdapter(List<PromoNotification> promoList) {
        this.promoList = promoList;
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