package com.example.uitpayapp.giftexchange;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class ExchangeVoucherAdapter extends RecyclerView.Adapter<ExchangeVoucherAdapter.VoucherViewHolder> {

    public interface OnExchangeClickListener {
        void onExchangeClick(ExchangeVoucherModel voucher);
    }

    private List<ExchangeVoucherModel> voucherList;
    private OnExchangeClickListener listener;

    public ExchangeVoucherAdapter(List<ExchangeVoucherModel> voucherList) {
        this(voucherList, null);
    }

    public ExchangeVoucherAdapter(List<ExchangeVoucherModel> voucherList, OnExchangeClickListener listener) {
        this.voucherList = voucherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_priority_exchange_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        ExchangeVoucherModel voucher = voucherList.get(position);
        if (voucher == null) return;

        ExchangeVoucherModel.ExchangeVoucherType type = voucher.getVoucherType();
        holder.ivExchangeType.setImageResource(type.getIconResId());
        holder.tvExchangeType.setText(type.getDisplayName());

        holder.tvTitle.setText(voucher.getTitle());
        if (!voucher.getCondition().isEmpty()) {
            holder.tvCondition.setVisibility(View.VISIBLE);
            holder.tvCondition.setText(voucher.getCondition());
        } else {
            holder.tvCondition.setVisibility(View.GONE);
        }
        holder.tvCoinCost.setText(voucher.getCoinCost());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExchangeClick(voucher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherList != null ? voucherList.size() : 0;
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExchangeType;
        TextView tvExchangeType,tvTitle, tvCondition, tvCoinCost, tvOriginalCost;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExchangeType = itemView.findViewById(R.id.iv_exchange_type);
            tvExchangeType= itemView.findViewById(R.id.tv_exchange_type);
            tvTitle = itemView.findViewById(R.id.tv_exchange_title);
            tvCondition = itemView.findViewById(R.id.tv_exchange_condition);
            tvCoinCost = itemView.findViewById(R.id.tv_coin_cost);
        }
    }
}
