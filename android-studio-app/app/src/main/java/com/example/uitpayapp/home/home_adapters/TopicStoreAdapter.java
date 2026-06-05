package com.example.uitpayapp.home.home_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.TopicStore;

import java.util.List;

public class TopicStoreAdapter extends RecyclerView.Adapter<TopicStoreAdapter.ViewHolder> {

    private final List<TopicStore> stores;

    public TopicStoreAdapter(List<TopicStore> stores) {
        this.stores = stores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_store, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopicStore store = stores.get(position);
        holder.tvName.setText(store.getName());
        holder.ivImage.setImageResource(store.getImageResId());
    }

    @Override
    public int getItemCount() {
        return stores != null ? stores.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_topic_store_image);
            tvName = itemView.findViewById(R.id.tv_topic_store_name);
        }
    }
}
