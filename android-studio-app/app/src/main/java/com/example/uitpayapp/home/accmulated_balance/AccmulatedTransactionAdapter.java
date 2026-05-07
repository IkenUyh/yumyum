package com.example.uitpayapp.home.accmulated_balance;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class AccmulatedTransactionAdapter extends RecyclerView.Adapter<AccmulatedTransactionAdapter.ViewHolder> {

    private final List<AccmulatedTransaction> transactions;

    public AccmulatedTransactionAdapter(List<AccmulatedTransaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accmulated_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccmulatedTransaction item = transactions.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDate.setText(item.getDate());
        holder.tvAmount.setText(item.getAmount());

        if (item.isDeposit()) {
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
            holder.ivIcon.setImageResource(R.drawable.ic_receive);
            holder.ivIcon.setColorFilter(Color.parseColor("#0034c8"));
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#FF9800"));
            holder.ivIcon.setImageResource(R.drawable.ic_payment);
            holder.ivIcon.setColorFilter(Color.parseColor("#FF9800"));
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDate, tvAmount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_transaction_icon);
            tvTitle = itemView.findViewById(R.id.tv_transaction_title);
            tvDate = itemView.findViewById(R.id.tv_transaction_date);
            tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
        }
    }
}
