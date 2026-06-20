package com.example.uitpayapp.favorite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class FavoriteHorizontalAdapter extends RecyclerView.Adapter<FavoriteHorizontalAdapter.HorizontalViewHolder> {

    public interface OnFavoriteRemoveListener {
        void onRemove(FavoriteShop shop);
    }

    private List<FavoriteShop> shopList;
    private OnFavoriteRemoveListener removeListener;

    public FavoriteHorizontalAdapter(List<FavoriteShop> shopList, OnFavoriteRemoveListener removeListener) {
        this.shopList = shopList;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_favorite_item_shop, parent,
                false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        FavoriteShop shop = shopList.get(position);

        holder.tvHorizontalName.setText(shop.getName());
        com.example.uitpayapp.utils.ImageLoadHelper.loadImageWithFlashingPlaceholder(holder.ivHorizontalImage, shop.getImageUrl());

        if (shop.isFavorited()) {
            holder.ivHorizontalFavoriteHeart.setImageResource(R.drawable.favorite_filled_24px);
        } else {
            holder.ivHorizontalFavoriteHeart.setImageResource(R.drawable.favorite_border_24px);
        }

        holder.ivHorizontalFavoriteHeart.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(shop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList != null ? shopList.size() : 0;
    }

    static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        TextView tvHorizontalName;
        ImageView ivHorizontalImage, ivHorizontalFavoriteHeart;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHorizontalName = itemView.findViewById(R.id.tvHorizontalName);
            ivHorizontalImage = itemView.findViewById(R.id.ivHorizontalImage);
            ivHorizontalFavoriteHeart = itemView.findViewById(R.id.ivHorizontalFavoriteHeart);
        }
    }
}