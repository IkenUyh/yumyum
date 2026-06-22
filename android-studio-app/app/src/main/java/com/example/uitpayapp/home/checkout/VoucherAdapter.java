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
    private List<VoucherModel> selectedVouchers;
    private OnVoucherSelectedListener listener;

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(VoucherModel voucher);
    }

    public VoucherAdapter(List<VoucherModel> vouchers, OnVoucherSelectedListener listener) {
        this(vouchers, null, listener);
    }

    public VoucherAdapter(List<VoucherModel> vouchers, List<VoucherModel> selectedVouchers, OnVoucherSelectedListener listener) {
        this.selectedVouchers = selectedVouchers;
        this.listener = listener;

        if (selectedVouchers != null && !selectedVouchers.isEmpty() && vouchers != null) {
            java.util.Collections.sort(vouchers, new java.util.Comparator<VoucherModel>() {
                @Override
                public int compare(VoucherModel v1, VoucherModel v2) {
                    boolean v1Selected = false;
                    boolean v2Selected = false;
                    for (VoucherModel sv : selectedVouchers) {
                        if (sv.getId().equals(v1.getId())) v1Selected = true;
                        if (sv.getId().equals(v2.getId())) v2Selected = true;
                    }
                    if (v1Selected && !v2Selected) return -1;
                    if (!v1Selected && v2Selected) return 1;
                    return 0;
                }
            });
        }
        this.vouchers = vouchers;
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
        
        if ("SHIPPING_DISCOUNT".equalsIgnoreCase(voucher.getType())) {
            holder.llLeftSide.setBackgroundColor(android.graphics.Color.parseColor("#00BFA5"));
            holder.icTypeVoucher.setImageResource(R.drawable.ic_delivery);
            holder.tvTypeVoucher.setText("Miễn phí vận chuyển");
        } else {
            holder.llLeftSide.setBackgroundColor(android.graphics.Color.parseColor("#f24405"));
            holder.icTypeVoucher.setImageResource(R.drawable.ic_food);
            holder.tvTypeVoucher.setText("Giảm giá món");
        }

        holder.tvTitle.setText(voucher.getTitle());
        holder.tvDescription.setText(voucher.getDescription());
        holder.tvMinOrder.setText(String.format("Đơn tối thiểu: %,dđ", voucher.getMinOrderAmount()).replace(',', '.'));
        
        boolean isSelected = false;
        if (selectedVouchers != null) {
            for (VoucherModel v : selectedVouchers) {
                if (v.getId().equals(voucher.getId())) {
                    isSelected = true;
                    break;
                }
            }
        }

        if (isSelected) {
            holder.btnUse.setText("Bỏ chọn");
            holder.btnUse.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#757575")));
        } else {
            holder.btnUse.setText("Dùng");
            holder.btnUse.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0A46A6")));
        }

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
        android.widget.LinearLayout llLeftSide;
        android.widget.ImageView icTypeVoucher;
        TextView tvTitle, tvDescription, tvMinOrder, tvTypeVoucher, btnUse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llLeftSide = itemView.findViewById(R.id.ll_left_side);
            icTypeVoucher = itemView.findViewById(R.id.ic_type_voucher);
            tvTypeVoucher = itemView.findViewById(R.id.tv_type_voucher);
            tvTitle = itemView.findViewById(R.id.tv_voucher_title);
            tvDescription = itemView.findViewById(R.id.tv_voucher_description);
            tvMinOrder = itemView.findViewById(R.id.tv_min_order);
            btnUse = itemView.findViewById(R.id.btn_use_voucher);
        }
    }
}
