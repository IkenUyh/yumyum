package com.example.uitpayapp.gift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.ViewHolder> {

    private List<exchange> list;

    public ExchangeAdapter(List<exchange> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvCoin, tvNote;

        public ViewHolder(View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCoin = itemView.findViewById(R.id.tvCoin);
            tvNote = itemView.findViewById(R.id.tvNote);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gift_item_exchange, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        exchange item = list.get(position);

        holder.imgIcon.setImageResource(item.icon);
        holder.tvTitle.setText(item.title);
        holder.tvCoin.setText(item.coin);
        holder.tvNote.setText(item.note);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}