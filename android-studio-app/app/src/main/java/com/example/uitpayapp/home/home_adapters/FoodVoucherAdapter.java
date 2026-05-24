package com.example.uitpayapp.home.home_adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.FoodVoucher;

import java.util.List;

public class FoodVoucherAdapter extends RecyclerView.Adapter<FoodVoucherAdapter.ViewHolder> {

    private final List<FoodVoucher> vouchers;

    public FoodVoucherAdapter(List<FoodVoucher> vouchers) {
        this.vouchers = vouchers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodVoucher voucher = vouchers.get(position);
        holder.tvDiscount.setText(voucher.getDiscount());
        holder.tvCondition.setText(voucher.getCondition());
        holder.tvBrandInitial.setText(voucher.getBrandName().substring(0, 1));

        // Set brand color for icon
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(voucher.getBrandColor());
        holder.viewBrandBg.setBackground(bg);

        // Update collected state
        updateCollectedState(holder, voucher);

        holder.btnCollect.setOnClickListener(v -> {
            if (!voucher.isCollected()) {
                voucher.setCollected(true);
                updateCollectedState(holder, voucher);
                Toast.makeText(v.getContext(), "Đã thu thập voucher " + voucher.getDiscount(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCollectedState(ViewHolder holder, FoodVoucher voucher) {
        if (voucher.isCollected()) {
            holder.btnCollect.setText("Đã lưu ✓");
            holder.btnCollect.setTextColor(Color.parseColor("#4CAF50"));
            holder.btnCollect.setBackgroundResource(R.drawable.bg_food_collected_btn);
        } else {
            holder.btnCollect.setText("Thu thập");
            holder.btnCollect.setTextColor(Color.parseColor("#0034c8"));
            holder.btnCollect.setBackgroundResource(R.drawable.bg_food_collect_btn);
        }
    }

    @Override
    public int getItemCount() { return vouchers.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View viewBrandBg;
        TextView tvBrandInitial, tvDiscount, tvCondition, btnCollect;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewBrandBg = itemView.findViewById(R.id.view_voucher_brand_bg);
            tvBrandInitial = itemView.findViewById(R.id.tv_voucher_brand_initial);
            tvDiscount = itemView.findViewById(R.id.tv_voucher_discount);
            tvCondition = itemView.findViewById(R.id.tv_voucher_condition);
            btnCollect = itemView.findViewById(R.id.btn_collect_voucher);
        }
    }
}
