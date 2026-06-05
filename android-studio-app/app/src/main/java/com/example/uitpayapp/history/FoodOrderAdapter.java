package com.example.uitpayapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.text.DecimalFormat;
import java.util.List;

public class FoodOrderAdapter extends RecyclerView.Adapter<FoodOrderAdapter.OrderViewHolder> {

    private List<FoodOrder> orderList;

    public FoodOrderAdapter(List<FoodOrder> orderList) {
        this.orderList = orderList;
    }

    public void setData(List<FoodOrder> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        FoodOrder order = orderList.get(position);

        holder.tvOrderHeader.setText("Đồ ăn #" + order.getOrderId());
        holder.tvOrderDate.setText(order.getDate());
        holder.tvMerchantName.setText(order.getMerchantName());
        holder.tvFoodItemName.setText(order.getItemName());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvFoodCount.setText(order.getItemCount() + " món ›");

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        holder.tvFoodPrice.setText(formatter.format(order.getTotalPrice()) + "đ");

        if (order.isFavorite()) {
            holder.tvFavoriteTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvFavoriteTag.setVisibility(View.GONE);
        }

        // Tải ảnh mẫu ngẫu nhiên hoặc ẩn nếu không cấu hình
        holder.ivFoodImage.setImageResource(order.getImageResId());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderHeader, tvOrderDate, tvFavoriteTag, tvMerchantName, tvFoodItemName, tvFoodPrice, tvFoodCount, tvOrderStatus;
        ImageView ivFoodImage;
        Button btnRate, btnReorder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderHeader = itemView.findViewById(R.id.tvOrderHeader);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvFavoriteTag = itemView.findViewById(R.id.tvFavoriteTag);
            tvMerchantName = itemView.findViewById(R.id.tvMerchantName);
            tvFoodItemName = itemView.findViewById(R.id.tvFoodItemName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodCount = itemView.findViewById(R.id.tvFoodCount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            btnRate = itemView.findViewById(R.id.btnRate);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}