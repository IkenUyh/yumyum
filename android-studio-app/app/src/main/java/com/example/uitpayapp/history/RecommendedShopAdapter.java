package com.example.uitpayapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class RecommendedShopAdapter extends RecyclerView.Adapter<RecommendedShopAdapter.ViewHolder> {

    private final List<RecommendedShop> list;

    public RecommendedShopAdapter(List<RecommendedShop> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tái sử dụng trực tiếp file layout vertical của ông ở đây
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_favorite_item_vertical, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendedShop item = list.get(position);

        holder.tvName.setText(item.getName());
        holder.tvInfo.setText("⭐ " + item.getRating() + "  |  " + item.getDistance() + "km  |  " + item.getDeliveryTime() + "phút");
        holder.tvDiscount.setText(item.getPromoText());
        holder.ivImage.setImageResource(item.getImageResId());

        // LOGIC TÁI SỬ DỤNG: Khối gợi ý thì ẩn tính năng yêu thích đi
        holder.tvFavoriteBadge.setVisibility(View.GONE);
        holder.ivFavoriteHeart.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo, tvDiscount, tvFavoriteBadge;
        ImageView ivImage, ivFavoriteHeart;

        ViewHolder(View itemView) {
            super(itemView);
            // Ánh xạ chuẩn chỉ theo đúng các ID trong file của ông
            tvName = itemView.findViewById(R.id.tvVerticalName);
            tvInfo = itemView.findViewById(R.id.tvVerticalInfo);
            tvDiscount = itemView.findViewById(R.id.tvVerticalDiscount);
            tvFavoriteBadge = itemView.findViewById(R.id.tvFavoriteBadge);
            ivFavoriteHeart = itemView.findViewById(R.id.ivVerticalFavoriteHeart);
            ivImage = itemView.findViewById(R.id.ivVerticalImage);
        }
    }
}