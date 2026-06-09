package com.example.uitpayapp.home.checkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private List<VoucherModel> vouchers;
    private OnVoucherSelectedListener listener;

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(VoucherModel voucher);
    }

    public VoucherAdapter(List<VoucherModel> vouchers, OnVoucherSelectedListener listener) {
        this.vouchers = vouchers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoucherModel voucher = vouchers.get(position);
        
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvDescription.setText(voucher.getDescription());
        holder.tvMinOrder.setText(String.format("Đơn tối thiểu: %,dđ", voucher.getMinOrderAmount()).replace(',', '.'));
        
        holder.btnUse.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVoucherSelected(voucher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vouchers != null ? vouchers.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvMinOrder, btnUse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_voucher_title);
            tvDescription = itemView.findViewById(R.id.tv_voucher_description);
            tvMinOrder = itemView.findViewById(R.id.tv_min_order);
            btnUse = itemView.findViewById(R.id.btn_use_voucher);
        }
    }
}
