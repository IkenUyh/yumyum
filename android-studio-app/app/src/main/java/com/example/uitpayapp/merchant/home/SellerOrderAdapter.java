package com.example.uitpayapp.merchant.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.home_model.OrderItem;
import com.example.uitpayapp.merchant.home.home_model.SellerOrder;

import java.util.List;

public class SellerOrderAdapter extends RecyclerView.Adapter<SellerOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<SellerOrder> orderList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onAccept(SellerOrder order);
        void onSeeMore(SellerOrder order);
    }

    public SellerOrderAdapter(Context context, List<SellerOrder> orderList, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    public void updateData(List<SellerOrder> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seller_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        SellerOrder order = orderList.get(position);
        
        holder.tvOrderShortId.setText(order.getId());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvNumberOfDishes.setText(order.getNumberOfDishes() + " Món");
        holder.tvTotalPrice.setText(order.getTotalPrice());
        holder.tvGuestNote.setText("💬 " + order.getGuestNote());
        
        holder.llDishesContainer.removeAllViews();
        for (OrderItem item : order.getDishes()) {
            View dishView = LayoutInflater.from(context).inflate(R.layout.item_order_sub_item, holder.llDishesContainer, false);
            TextView tvDish = dishView.findViewById(R.id.tv_sub_item_name);
            TextView tvPrice = dishView.findViewById(R.id.tv_sub_item_price);
            
            tvDish.setText(item.getQuantity() + " x " + item.getDishName());
            tvPrice.setVisibility(View.GONE);

            holder.llDishesContainer.addView(dishView);
        }
        
        String status = order.getStatus() != null ? order.getStatus().toUpperCase() : "";
        if ("PREPARING".equals(status) || "CONFIRMED".equals(status)) {
            holder.btnAccept.setText("Bàn giao cho Shipper");
            holder.btnAccept.setVisibility(View.VISIBLE);
        } else if ("DELIVERING".equals(status)) {
            holder.btnAccept.setText("Hoàn thành");
            holder.btnAccept.setVisibility(View.VISIBLE);
        } else if ("PENDING".equals(status)) {
            holder.btnAccept.setText("Xác nhận");
            holder.btnAccept.setVisibility(View.VISIBLE);
        } else {
            holder.btnAccept.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(order));
        holder.btnSeeMore.setOnClickListener(v -> listener.onSeeMore(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderShortId, tvCustomerName, tvNumberOfDishes, tvTotalPrice, tvGuestNote;
        LinearLayout llDishesContainer;
        Button btnSeeMore, btnAccept;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderShortId = itemView.findViewById(R.id.tv_order_short_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvNumberOfDishes = itemView.findViewById(R.id.tv_numberof_dishes);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvGuestNote = itemView.findViewById(R.id.tv_guest_note);
            llDishesContainer = itemView.findViewById(R.id.ll_dishes_container);
            btnSeeMore = itemView.findViewById(R.id.btn_see_more);
            btnAccept = itemView.findViewById(R.id.btn_accept_order);
        }
    }
}
