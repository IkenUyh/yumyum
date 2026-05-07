package com.example.uitpayapp.gift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class RecommendServiceAdapter extends RecyclerView.Adapter<RecommendServiceAdapter.ViewHolder> {

    List<recommendservice> list;

    public RecommendServiceAdapter(List<recommendservice> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gift_item_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        recommendservice item = list.get(position);
        holder.imgIcon.setImageResource(item.icon);
        holder.tvTitle.setText(item.title);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}