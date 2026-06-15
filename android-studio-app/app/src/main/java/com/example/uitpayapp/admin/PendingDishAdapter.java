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

public class PendingDishAdapter extends RecyclerView.Adapter<PendingDishAdapter.ViewHolder> {

    private Context context;
    private List<PendingDish> dishList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PendingDish dish);
    }

    public PendingDishAdapter(Context context, List<PendingDish> dishList, OnItemClickListener listener) {
        this.context = context;
        this.dishList = dishList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_dish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingDish dish = dishList.get(position);

        holder.tvDishName.setText(dish.getDishName());
        holder.tvStoreName.setText("Cửa hàng: " + dish.getStoreName());
        holder.tvCategory.setText("Danh mục: " + dish.getCategory());
        holder.tvPrice.setText(String.format("Giá: %,.0fđ", dish.getPrice()));
        holder.tvDate.setText("Ngày gửi: " + dish.getSubmittedDate());
        
        if (dish.getImageRes() != 0) {
            holder.ivImage.setImageResource(dish.getImageRes());
        }

        switch (dish.getStatus()) {
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
                listener.onItemClick(dish);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvDishName, tvStoreName, tvCategory, tvPrice, tvDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_dish_image);
            tvDishName = itemView.findViewById(R.id.tv_dish_name);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvPrice = itemView.findViewById(R.id.tv_dish_price);
            tvDate = itemView.findViewById(R.id.tv_date_submitted);
            tvStatus = itemView.findViewById(R.id.tv_status_badge);
        }
    }
}
