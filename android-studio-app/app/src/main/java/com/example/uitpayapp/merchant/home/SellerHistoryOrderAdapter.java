package com.example.uitpayapp.merchant.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.home_model.SellerHistoryOrder;

import java.util.List;

public class SellerHistoryOrderAdapter extends RecyclerView.Adapter<SellerHistoryOrderAdapter.HistoryViewHolder> {

    private Context context;
    private List<SellerHistoryOrder> historyList;

    public SellerHistoryOrderAdapter(Context context, List<SellerHistoryOrder> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seller_history_order, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SellerHistoryOrder history = historyList.get(position);

        holder.tvId.setText(history.getId());
        holder.tvCustomerName.setText(history.getCustomerName());
        holder.tvStatusBadge.setText(history.getStatus());
        holder.tvPickupTime.setText(history.getPickupTime());
        holder.tvItemCount.setText(String.valueOf(history.getItemCount()));
        holder.tvDistance.setText(history.getDistance());
        holder.tvOrderDate.setText(history.getOrderDate());
        holder.tvFinishTime.setText(history.getFinishTime());
        holder.tvTotalPrice.setText(history.getTotalPrice());

        if ("Đã hủy".equalsIgnoreCase(history.getStatus())) {
            holder.tvStatusBadge.getBackground().setTint(context.getResources().getColor(R.color.black, null));
            holder.tvStatusBadge.setTextColor(context.getResources().getColor(R.color.black, null));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvCustomerName, tvStatusBadge, tvPickupTime, tvItemCount, tvDistance, tvOrderDate, tvFinishTime, tvTotalPrice;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_history_id);
            tvCustomerName = itemView.findViewById(R.id.tv_history_customer_name);
            tvStatusBadge = itemView.findViewById(R.id.tv_history_status_badge);
            tvPickupTime = itemView.findViewById(R.id.tv_pickup_time);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvFinishTime = itemView.findViewById(R.id.tv_order_finish_time);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
        }
    }
}
