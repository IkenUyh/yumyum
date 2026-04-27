package com.example.uitpayapp.gift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class LogoAdapter extends RecyclerView.Adapter<LogoAdapter.LogoViewHolder> {

    private List<Integer> logos;

    public LogoAdapter(List<Integer> logos) {
        this.logos = logos;
    }

    @NonNull
    @Override
    public LogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gift_item_logo, parent, false);
        return new LogoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogoViewHolder holder, int position) {
        holder.img.setImageResource(logos.get(position));
    }

    @Override
    public int getItemCount() {
        return logos.size();
    }

    static class LogoViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public LogoViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView;
        }
    }
}