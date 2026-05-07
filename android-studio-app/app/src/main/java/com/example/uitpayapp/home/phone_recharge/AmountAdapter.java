package com.example.uitpayapp.home.phone_recharge;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.example.uitpayapp.R;
import java.util.List;

public class AmountAdapter extends RecyclerView.Adapter<AmountAdapter.AmountViewHolder> {

    public interface OnAmountSelectedListener {
        void onAmountSelected(String amount);
    }

    private List<String> amounts;
    private int selectedPosition = 0;
    private OnAmountSelectedListener listener;

    public AmountAdapter(List<String> amounts, OnAmountSelectedListener listener) {
        this.amounts = amounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recharge_amount, parent, false);
        return new AmountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmountViewHolder holder, int position) {
        holder.tvAmount.setText(amounts.get(position));

        if (selectedPosition == position) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F4F8FF"));
            holder.cardView.setStrokeColor(Color.parseColor("#0A46A6"));
            holder.cardView.setStrokeWidth(4);
            holder.tvAmount.setTextColor(Color.parseColor("#0A46A6"));
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.cardView.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.cardView.setStrokeWidth(2);
            holder.tvAmount.setTextColor(Color.BLACK);
            holder.ivCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousItem = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousItem);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onAmountSelected(amounts.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return amounts.size();
    }

    public static class AmountViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount;
        ImageView ivCheck;
        MaterialCardView cardView;

        public AmountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            ivCheck = itemView.findViewById(R.id.iv_check);
            cardView = (MaterialCardView) itemView;
        }
    }
}