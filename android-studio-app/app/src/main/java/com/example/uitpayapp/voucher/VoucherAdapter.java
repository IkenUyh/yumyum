package com.example.uitpayapp.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<VoucherModel> listVoucher;
    private onVoucherClickListener Listener;

    public interface onVoucherClickListener {
        void onVoucherClick(VoucherModel item);
    }

    public VoucherAdapter(List<VoucherModel> listVoucher, onVoucherClickListener Listener) {
        this.listVoucher = listVoucher;
        this.Listener = Listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        VoucherModel voucher = listVoucher.get(position);
        if (voucher == null) return;
        if (voucher.getIcon() > 0) {
            holder.imgIcon.setImageResource(voucher.getIcon());
        }
        holder.tvType.setText(voucher.getType());
        holder.tvMainTitle.setText(voucher.getMainTitle());
        if (voucher.getSubTitle().length()>0) {
            holder.tvSubTitle.setVisibility(View.VISIBLE);
            holder.tvSubTitle.setText(voucher.getSubTitle());
        } else {
            holder.tvSubTitle.setVisibility(View.GONE);
        }
        if (voucher.getVoucherExpiration().length()>0) {
            holder.tvExpiration.setVisibility(View.VISIBLE);
            holder.tvExpiration.setText(voucher.getVoucherExpiration());
        } else {
            holder.tvExpiration.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (Listener != null) {
                Listener.onVoucherClick(voucher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listVoucher.size();
    }
    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvType, tvMainTitle, tvSubTitle, tvExpiration;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.ic_type_voucher);
            tvType = itemView.findViewById(R.id.tv_type_voucher);
            tvMainTitle = itemView.findViewById(R.id.tv_main_title_voucher);
            tvSubTitle = itemView.findViewById(R.id.tv_sub_title_voucher);
            tvExpiration = itemView.findViewById(R.id.tv_voucher_expiration);
        }
    }
}
