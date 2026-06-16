package com.example.uitpayapp.history;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class DealHistoryAdapter extends RecyclerView.Adapter<DealHistoryAdapter.DealViewHolder> {

    private final List<DealHistory> dealList;

    public DealHistoryAdapter(List<DealHistory> dealList) {
        this.dealList = dealList;
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_history_item_deal, parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        DealHistory model = dealList.get(position);

        holder.txtMerchantName.setText(model.getMerchantName());
        holder.txtPurchaseDate.setText(model.getPurchaseDate());
        holder.txtDealTitle.setText(model.getDealTitle());
        holder.txtPrice.setText(model.getPrice());
        holder.txtExpiry.setText(model.getExpiryText());
        holder.txtQuantity.setText(model.getQuantityText());
        holder.txtStatus.setText(model.getStatusText());

        // CLICK ITEM -> Chuyển trực tiếp sang DealDetailActivity bằng Context
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, DealDetailActivity.class);
            intent.putExtra("MERCHANT_NAME", model.getMerchantName());
            intent.putExtra("DEAL_TITLE", model.getDealTitle());
            intent.putExtra("APPLIED_ORDER_ID", model.getAppliedOrderId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return dealList.size(); }

    static class DealViewHolder extends RecyclerView.ViewHolder {
        TextView txtMerchantName, txtPurchaseDate, txtDealTitle, txtPrice, txtExpiry, txtQuantity, txtStatus;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMerchantName = itemView.findViewById(R.id.txtDealMerchantName);
            txtPurchaseDate = itemView.findViewById(R.id.txtDealPurchaseDate);
            txtDealTitle = itemView.findViewById(R.id.txtDealTitle);
            txtPrice = itemView.findViewById(R.id.txtDealPrice);
            txtExpiry = itemView.findViewById(R.id.txtDealExpiry);
            txtQuantity = itemView.findViewById(R.id.txtDealQuantity);
            txtStatus = itemView.findViewById(R.id.txtDealStatus);
        }
    }
}