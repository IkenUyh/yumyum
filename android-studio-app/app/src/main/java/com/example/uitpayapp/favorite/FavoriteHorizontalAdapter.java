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

    private List<FavoriteShop> shopList;

    public FavoriteHorizontalAdapter(List<FavoriteShop> shopList) {
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_favorite_item_shop, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder holder, int position) {
        FavoriteShop shop = shopList.get(position);

        holder.tvHorizontalName.setText(shop.getName());
        holder.tvHorizontalDiscount.setText(shop.getDiscountTag());
        holder.ivHorizontalImage.setImageResource(shop.getImageResId());
    }

    @Override
    public int getItemCount() {
        return shopList != null ? shopList.size() : 0;
    }

    static class HorizontalViewHolder extends RecyclerView.ViewHolder {
        TextView tvHorizontalName, tvHorizontalDiscount;
        ImageView ivHorizontalImage;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHorizontalName = itemView.findViewById(R.id.tvHorizontalName);
            tvHorizontalDiscount = itemView.findViewById(R.id.tvHorizontalDiscount);
            ivHorizontalImage = itemView.findViewById(R.id.ivHorizontalImage);
        }
    }
}