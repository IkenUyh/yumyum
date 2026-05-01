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

    private List<ExchangeVoucherModel> voucherList;

    public ExchangeVoucherAdapter(List<ExchangeVoucherModel> voucherList) {
        this.voucherList = voucherList;
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
        if (voucher.getBrandLogo()>0)
            holder.ivBrandLogo.setImageResource(voucher.getBrandLogo());
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvCondition.setText(voucher.getCondition());
        holder.tvCoinCost.setText(voucher.getCoinCost());
        holder.tvOriginalCost.setText(voucher.getOriginalCost());
    }

    @Override
    public int getItemCount() {
        return voucherList != null ? voucherList.size() : 0;
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBrandLogo;
        TextView tvTitle, tvCondition, tvCoinCost, tvOriginalCost;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBrandLogo = itemView.findViewById(R.id.iv_brand_logo);
            tvTitle = itemView.findViewById(R.id.tv_exchange_title);
            tvCondition = itemView.findViewById(R.id.tv_exchange_condition);
            tvCoinCost = itemView.findViewById(R.id.tv_coin_cost);
            tvOriginalCost = itemView.findViewById(R.id.tv_original_cost);
        }
    }
}
