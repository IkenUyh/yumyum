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
    private OnRateClickListener rateClickListener;
    private OnReorderClickListener reorderClickListener; // Thêm listener cho nút Đặt lại

    // Interface click vào toàn bộ item
    public interface OnItemClickListener {
        void onItemClick(FoodOrder order);
    }

    // Interface click nút Đánh giá
    public interface OnRateClickListener {
        void onRateClick(FoodOrder order);
    }

    // Interface click nút Đặt lại
    public interface OnReorderClickListener {
        void onReorderClick(FoodOrder order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnRateClickListener(OnRateClickListener rateClickListener) {
        this.rateClickListener = rateClickListener;
    }

    // Setter cho nút Đặt lại từ bên ngoài Activity
    public void setOnReorderClickListener(OnReorderClickListener reorderClickListener) {
        this.reorderClickListener = reorderClickListener;
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

        // Kiểm tra số lượng món để bật tắt giao diện thông minh
        if (order.getSubItems() != null && order.getSubItems().size() > 1) {
            holder.layoutSingleItem.setVisibility(View.GONE);
            holder.rvSubItemsList.setVisibility(View.VISIBLE);

            holder.rvSubItemsList.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.rvSubItemsList.setAdapter(new SubItemAdapter(order.getSubItems()));
        } else {
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

        // Xử lý sự kiện nút Đánh giá
        // 1. CLICK NÚT ĐÁNH GIÁ -> Nhảy thẳng sang màn hình Đánh giá tài xế
       /* holder.btnRate.setOnClickListener(v -> {
            if (position != RecyclerView.NO_POSITION) {
                // Lấy context trực tiếp từ view của item
                android.content.Context context = holder.itemView.getContext();

                // Khởi tạo và gắn thẳng Intent vào đây
                android.content.Intent intent = new android.content.Intent(context, RatingDriverActivity.class);
                intent.putExtra("DRIVER_ID", "DRV_" + order.getOrderId());
                intent.putExtra("DRIVER_NAME", "Võ Tấn Đạt");

                // Kích hoạt chuyển màn hình
                context.startActivity(intent);
            }
        });*/
        holder.btnRate.setOnClickListener(v -> {
            if (position != RecyclerView.NO_POSITION) {
                android.content.Context context = holder.itemView.getContext();

                // Tạo intent liên kết màn hình RatingMerchantActivity
                android.content.Intent intent = new android.content.Intent(context, RatingMerchantActivity.class);

                // Truyền mã ID đơn hàng để phục vụ việc submit dữ liệu lên endpoint API của Spring Boot
                intent.putExtra("ORDER_ID", order.getOrderId());

                context.startActivity(intent);
            }
        });

// 2. CLICK NÚT ĐẶT LẠI -> Bạn có thể chuyển sang CartActivity hoặc xử lý tùy ý
        holder.btnReorder.setOnClickListener(v -> {
            if (position != RecyclerView.NO_POSITION) {
                android.content.Context context = holder.itemView.getContext();

                // Ví dụ: Bắn Toast thông báo hoặc chuyển màn hình Giỏ hàng tùy bạn cấu hình
                android.widget.Toast.makeText(context, "Đang đặt lại đơn hàng #" + order.getOrderId(), android.widget.Toast.LENGTH_SHORT).show();

        /* android.content.Intent cartIntent = new android.content.Intent(context, CartActivity.class);
        context.startActivity(cartIntent);
        */
            }
        });

        // Xử lý sự kiện click vào toàn bộ item
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