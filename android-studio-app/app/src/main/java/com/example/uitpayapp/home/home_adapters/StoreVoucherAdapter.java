package com.example.uitpayapp.home.home_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class StoreVoucherAdapter extends RecyclerView.Adapter<StoreVoucherAdapter.ViewHolder> {
    private final List<String> vouchers;

    public StoreVoucherAdapter(List<String> vouchers) {
        this.vouchers = vouchers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvVoucherDesc.setText(vouchers.get(position));
    }

    @Override
    public int getItemCount() {
        return vouchers != null ? vouchers.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVoucherDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherDesc = itemView.findViewById(R.id.tv_voucher_desc);
        }
    }
}
