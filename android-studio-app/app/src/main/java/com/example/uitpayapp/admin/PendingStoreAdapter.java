package com.example.uitpayapp.admin;

import android.content.Context;
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

public class PendingStoreAdapter extends RecyclerView.Adapter<PendingStoreAdapter.ViewHolder> {

    private Context context;
    private List<PendingStore> storeList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PendingStore store);
    }

    public PendingStoreAdapter(Context context, List<PendingStore> storeList, OnItemClickListener listener) {
        this.context = context;
        this.storeList = storeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingStore store = storeList.get(position);

        holder.tvStoreName.setText(store.getStoreName());
        holder.tvOwnerName.setText("Chủ: " + store.getOwnerName());
        holder.tvAddress.setText("Địa chỉ: " + store.getAddress());
        holder.tvStoreType.setText("Loại hình: " + store.getStoreType());
        holder.tvDate.setText("Ngày gửi: " + store.getSubmittedDate());
        
        if (store.getImageRes() != 0) {
            holder.ivImage.setImageResource(store.getImageRes());
        }

        switch (store.getStatus()) {
            case "pending":
                holder.tvStatus.setText("Chờ duyệt");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_pending);
                holder.tvStatus.setTextColor(Color.parseColor("#F57C00"));
                break;
            case "approved":
                holder.tvStatus.setText("Đã duyệt");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_approved);
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "rejected":
                holder.tvStatus.setText("Từ chối");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_rejected);
                holder.tvStatus.setTextColor(Color.parseColor("#E53935"));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(store);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvStoreName, tvOwnerName, tvAddress, tvStoreType, tvDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_store_image);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvOwnerName = itemView.findViewById(R.id.tv_owner_name);
            tvAddress = itemView.findViewById(R.id.tv_store_address);
            tvStoreType = itemView.findViewById(R.id.tv_store_type);
            tvDate = itemView.findViewById(R.id.tv_date_submitted);
            tvStatus = itemView.findViewById(R.id.tv_status_badge);
        }
    }
}
