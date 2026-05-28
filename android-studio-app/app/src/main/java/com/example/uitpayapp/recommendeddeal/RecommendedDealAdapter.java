package com.example.uitpayapp.recommendeddeal;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class RecommendedDealAdapter extends RecyclerView.Adapter<RecommendedDealAdapter.DealViewHolder> {

    private List<RecommendedDealModel> dealList;
    private final DecimalFormat currencyFormatter;

    public RecommendedDealAdapter(List<RecommendedDealModel> dealList) {
        this.dealList = dealList;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        currencyFormatter = new DecimalFormat("#,###đ", symbols);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_recommend_deal, parent, false);
        return new DealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        RecommendedDealModel deal = dealList.get(position);
        if (deal == null) return;

        holder.tvStoreName.setText(deal.getStoreName());
        holder.tvDistance.setText(deal.getDistance() + "km");
        holder.tvDeliveryTime.setText(deal.getDeliveryTime() + " phút");
        holder.ivFoodImage.setImageResource(deal.getFoodImageResId());
        holder.tvDiscountTag.setText(deal.getDiscountTag());
        holder.tvFoodTitle.setText(deal.getFoodTitle());

        holder.tvSoldCount.setText(deal.getSoldCount() + " Đã bán");
        holder.tvOriginalPrice.setText(currencyFormatter.format(deal.getOriginalPrice()));
        //Gach ngang gia cu
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.tvDiscountPrice.setText(currencyFormatter.format(deal.getDiscountPrice()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecommendedDealDetailActivity.class);
            intent.putExtra("food_title", deal.getFoodTitle());
            intent.putExtra("store_name", deal.getStoreName());
            intent.putExtra("discount_price", deal.getDiscountPrice());
            intent.putExtra("original_price", deal.getOriginalPrice());
            intent.putExtra("distance", deal.getDistance());
            intent.putExtra("delivery_time", deal.getDeliveryTime());
            intent.putExtra("food_image", deal.getFoodImageResId());
            v.getContext().startActivity(intent);
        });

        holder.btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecommendedDealDetailActivity.class);
            intent.putExtra("food_title", deal.getFoodTitle());
            intent.putExtra("store_name", deal.getStoreName());
            intent.putExtra("discount_price", deal.getDiscountPrice());
            intent.putExtra("original_price", deal.getOriginalPrice());
            intent.putExtra("distance", deal.getDistance());
            intent.putExtra("delivery_time", deal.getDeliveryTime());
            intent.putExtra("food_image", deal.getFoodImageResId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dealList != null ? dealList.size() : 0;
    }

    public static class DealViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoreName, tvDistance, tvDeliveryTime, tvDiscountTag, tvFoodTitle, tvSoldCount, tvOriginalPrice, tvDiscountPrice, btnBuyNow;
        ImageView ivFoodImage;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvDiscountTag = itemView.findViewById(R.id.tv_discount_tag);
            tvFoodTitle = itemView.findViewById(R.id.tv_food_title);
            tvSoldCount = itemView.findViewById(R.id.tv_sold_count);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvDiscountPrice = itemView.findViewById(R.id.tv_discount_price);
            btnBuyNow = itemView.findViewById(R.id.btn_buy_now);
        }
    }
}
