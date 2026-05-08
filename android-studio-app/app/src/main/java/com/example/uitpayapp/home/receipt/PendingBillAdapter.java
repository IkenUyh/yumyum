package com.example.uitpayapp.home.receipt;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class PendingBillAdapter extends RecyclerView.Adapter<PendingBillAdapter.ViewHolder> {

    public interface OnBillClickListener {
        void onBillClick(PendingBill bill, int position);
    }

    public interface OnAutoPayChangedListener {
        void onAutoPayChanged(PendingBill bill, int position, boolean isEnabled);
    }

    private final List<PendingBill> bills;
    private final OnBillClickListener clickListener;
    private final OnAutoPayChangedListener autoPayListener;

    public PendingBillAdapter(List<PendingBill> bills, OnBillClickListener clickListener, OnAutoPayChangedListener autoPayListener) {
        this.bills = bills;
        this.clickListener = clickListener;
        this.autoPayListener = autoPayListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingBill bill = bills.get(position);
        holder.tvName.setText(bill.getName());
        holder.tvProvider.setText(bill.getProvider());
        holder.tvAmount.setText(bill.getAmount());
        holder.ivIcon.setImageResource(bill.getIconResId());

        if (bill.isPaid()) {
            holder.tvStatus.setText("Đã thanh toán ✓");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvDue.setText("Hoàn thành");
            holder.tvDue.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvAmount.setTextColor(Color.parseColor("#757575"));
            // Ẩn phần auto-pay khi đã thanh toán
            holder.layoutAutoPay.setVisibility(View.GONE);
            holder.btnPay.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setText("Chưa thanh toán");
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
            holder.tvDue.setText("Hạn: " + bill.getDueDate());
            holder.tvDue.setTextColor(Color.parseColor("#FF9800"));
            holder.tvAmount.setTextColor(Color.parseColor("#D32F2F"));
            // Hiện phần auto-pay
            holder.layoutAutoPay.setVisibility(View.VISIBLE);
            holder.btnPay.setVisibility(View.VISIBLE);
        }

        // Xử lý switch tự động thanh toán
        holder.switchAutoPay.setOnCheckedChangeListener(null); // Tránh trigger khi recycle
        holder.switchAutoPay.setChecked(bill.isAutoPay());

        if (bill.isAutoPay() && !bill.isPaid()) {
            holder.tvAutoPayLabel.setText("Tự động thanh toán: BẬT");
            holder.tvAutoPayLabel.setTextColor(Color.parseColor("#0034c8"));
        } else {
            holder.tvAutoPayLabel.setText("Tự động thanh toán");
            holder.tvAutoPayLabel.setTextColor(Color.parseColor("#757575"));
        }

        holder.switchAutoPay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            bill.setAutoPay(isChecked);
            if (isChecked) {
                holder.tvAutoPayLabel.setText("Tự động thanh toán: BẬT");
                holder.tvAutoPayLabel.setTextColor(Color.parseColor("#0034c8"));
            } else {
                holder.tvAutoPayLabel.setText("Tự động thanh toán");
                holder.tvAutoPayLabel.setTextColor(Color.parseColor("#757575"));
            }
            if (autoPayListener != null) {
                autoPayListener.onAutoPayChanged(bill, holder.getAdapterPosition(), isChecked);
            }
        });

        holder.btnPay.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onBillClick(bill, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvProvider, tvDue, tvAmount, tvStatus, tvAutoPayLabel;
        SwitchCompat switchAutoPay;
        LinearLayout layoutAutoPay;
        Button btnPay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_bill_icon);
            tvName = itemView.findViewById(R.id.tv_bill_name);
            tvProvider = itemView.findViewById(R.id.tv_bill_provider);
            tvDue = itemView.findViewById(R.id.tv_bill_due);
            tvAmount = itemView.findViewById(R.id.tv_bill_amount);
            tvStatus = itemView.findViewById(R.id.tv_bill_status);
            switchAutoPay = itemView.findViewById(R.id.switch_auto_pay);
            tvAutoPayLabel = itemView.findViewById(R.id.tv_auto_pay_label);
            layoutAutoPay = itemView.findViewById(R.id.layout_auto_pay);
            btnPay = itemView.findViewById(R.id.btn_pay);
        }
    }
}
