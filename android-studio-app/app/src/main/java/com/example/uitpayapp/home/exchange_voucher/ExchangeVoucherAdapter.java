package com.example.uitpayapp.home.exchange_voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class ExchangeVoucherAdapter extends RecyclerView.Adapter<ExchangeVoucherAdapter.ViewHolder> {

    private List<ExchangeVoucherItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ExchangeVoucherItem item);
    }

    public ExchangeVoucherAdapter(List<ExchangeVoucherItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exchange_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExchangeVoucherItem item = itemList.get(position);

        holder.ivIcon.setImageResource(item.getIconResource());
        holder.tvTitle.setText(item.getTitle());
        holder.tvCost.setText(String.valueOf(item.getCost()));

        holder.btnExchange.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvCost;
        TextView btnExchange;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_exchange_icon);
            tvTitle = itemView.findViewById(R.id.tv_exchange_title);
            tvCost = itemView.findViewById(R.id.tv_exchange_cost);
            btnExchange = itemView.findViewById(R.id.btn_exchange);
        }
    }
}