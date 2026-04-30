package com.example.uitpayapp.home.deposit_withdraw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    private List<BankItem> bankList;
    private OnBankClickListener listener;

    public interface OnBankClickListener {
        void onBankClick(BankItem bank);
    }

    public BankAdapter(List<BankItem> bankList, OnBankClickListener listener) {
        this.bankList = bankList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        BankItem bank = bankList.get(position);
        holder.tvName.setText(bank.getName());
        holder.tvFullName.setText(bank.getFullName());

        holder.itemView.setOnClickListener(v -> listener.onBankClick(bank));
    }

    @Override
    public int getItemCount() {
        return bankList.size();
    }

    static class BankViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFullName;
        ImageView ivLogo;

        BankViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_bank_name);
            tvFullName = itemView.findViewById(R.id.tv_bank_fullname);
            ivLogo = itemView.findViewById(R.id.iv_bank_logo);
        }
    }
}