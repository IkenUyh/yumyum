package com.example.uitpayapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.text.DecimalFormat;
import java.util.List;

public class FoodOrderAdapter extends RecyclerView.Adapter<FoodOrderAdapter.OrderViewHolder> {

    private List<FoodOrder> orderList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FoodOrder order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

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
        holder.tvFoodCount.setText(order.getItemCount() + " món ›");

        DecimalFormat formatter = new DecimalFormat("###,###,###");
        holder.tvFoodPrice.setText(formatter.format(order.getTotalPrice()) + "đ");

        if (order.isFavorite()) {
            holder.tvFavoriteTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvFavoriteTag.setVisibility(View.GONE);
        }

        // FIX LỖI: Kiểm tra số lượng món để bật tắt giao diện thông minh
        if (order.getSubItems() != null && order.getSubItems().size() > 1) {
            // Trường hợp nhiều món: Ẩn cụm món đơn, bật RecyclerView ngang lên
            holder.layoutSingleItem.setVisibility(View.GONE);
            holder.rvSubItemsList.setVisibility(View.VISIBLE);

            holder.rvSubItemsList.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.rvSubItemsList.setAdapter(new SubItemAdapter(order.getSubItems()));
        } else {
            // Trường hợp chỉ có 1 món: Bật cụm món đơn, ẩn RecyclerView ngang
            holder.layoutSingleItem.setVisibility(View.VISIBLE);
            holder.rvSubItemsList.setVisibility(View.GONE);

            if (order.getSubItems() != null && !order.getSubItems().isEmpty()) {
                FoodOrder.SubItem singleItem = order.getSubItems().get(0);
                holder.tvFoodItemName.setText(singleItem.getName());
                holder.ivFoodImage.setImageResource(singleItem.getImageResId());
            }
        }

        if ("Đang đến".equalsIgnoreCase(order.getCategory())) {
            holder.tvPickupStatus.setVisibility(View.VISIBLE);
            holder.tvPickupStatus.setText("Đã lấy");
            holder.tvPickupStatus.setTextColor(0xFF666666);

            holder.layoutActiveStatusBlock.setVisibility(View.VISIBLE);
            holder.layoutButtons.setVisibility(View.GONE);
            holder.layoutWarningBanner.setVisibility(View.GONE);

            holder.tvOrderStatus.setText("Đơn đang được giao...");
            holder.tvEstimatedTime.setText("🕒 Thời gian giao dự kiến: 18:04");
        } else {
            holder.tvPickupStatus.setVisibility(View.VISIBLE);
            holder.tvPickupStatus.setText(order.getStatus());

            if ("Hoàn thành".equalsIgnoreCase(order.getStatus())) {
                holder.tvPickupStatus.setTextColor(0xFF222222);
                holder.layoutWarningBanner.setVisibility(View.VISIBLE);
            } else {
                holder.tvPickupStatus.setTextColor(0xFFFF0000);
                holder.layoutWarningBanner.setVisibility(View.GONE);
            }

            holder.layoutActiveStatusBlock.setVisibility(View.GONE);
            holder.layoutButtons.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(order);
            }
        });
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderHeader, tvOrderDate, tvFavoriteTag, tvMerchantName, tvFoodItemName, tvFoodPrice, tvFoodCount, tvOrderStatus, tvEstimatedTime, tvPickupStatus;
        ImageView ivFoodImage;
        Button btnRate, btnReorder;
        LinearLayout layoutActiveStatusBlock, layoutButtons, layoutSingleItem;
        RecyclerView rvSubItemsList;
        View layoutWarningBanner;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderHeader = itemView.findViewById(R.id.tvOrderHeader);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvFavoriteTag = itemView.findViewById(R.id.tvFavoriteTag);
            tvMerchantName = itemView.findViewById(R.id.tvMerchantName);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvFoodCount = itemView.findViewById(R.id.tvFoodCount);

            tvPickupStatus = itemView.findViewById(R.id.tvPickupStatus);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvEstimatedTime = itemView.findViewById(R.id.tvEstimatedTime);
            layoutActiveStatusBlock = itemView.findViewById(R.id.layoutActiveStatusBlock);
            layoutButtons = itemView.findViewById(R.id.layoutButtons);
            btnRate = itemView.findViewById(R.id.btnRate);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            layoutWarningBanner = itemView.findViewById(R.id.layoutWarningBanner);

            // Ánh xạ chính xác các cụm Hybrid mới thêm
            layoutSingleItem = itemView.findViewById(R.id.layoutSingleItem);
            rvSubItemsList = itemView.findViewById(R.id.rvSubItemsList);
            tvFoodItemName = itemView.findViewById(R.id.tvFoodItemName);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
        }
    }

    private static class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubViewHolder> {
        private final List<FoodOrder.SubItem> subItemsList;

        public SubItemAdapter(List<FoodOrder.SubItem> subItemsList) {
            this.subItemsList = subItemsList;
        }

        @NonNull
        @Override
        public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_item_food_sub, parent, false);
            return new SubViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {
            FoodOrder.SubItem item = subItemsList.get(position);
            holder.tvSubName.setText(item.getName());
            holder.ivSubThumb.setImageResource(item.getImageResId());
        }

        @Override
        public int getItemCount() { return subItemsList.size(); }

        static class SubViewHolder extends RecyclerView.ViewHolder {
            ImageView ivSubThumb;
            TextView tvSubName;
            public SubViewHolder(@NonNull View itemView) {
                super(itemView);
                ivSubThumb = itemView.findViewById(R.id.ivSubProductThumb);
                tvSubName = itemView.findViewById(R.id.tvSubProductName);
            }
        }
    }
}