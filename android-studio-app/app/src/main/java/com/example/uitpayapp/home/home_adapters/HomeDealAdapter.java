package com.example.uitpayapp.home.home_adapters;

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
import com.example.uitpayapp.home.StoreDetailActivity;
import com.example.uitpayapp.recommendeddeal.RecommendedDealDetailActivity;
import com.example.uitpayapp.recommendeddeal.RecommendedDealModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import com.bumptech.glide.Glide;

/**
 * Multi-viewtype adapter for the Home deals section.
 * Supports 3 view types: deal item, ad banner, and loading spinner.
 *
 * Items list uses null sentinel values:
 * - null at the end = loading indicator
 * - Internally, banners are inserted as items with a special wrapper.
 */
public class HomeDealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DEAL = 0;
    private static final int VIEW_TYPE_BANNER = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private final List<Object> items; // RecommendedDealModel, BannerItem, or null (loading)
    private final DecimalFormat currencyFormatter;

    public HomeDealAdapter(List<Object> items) {
        this.items = items;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        currencyFormatter = new DecimalFormat("#,###đ", symbols);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item == null) return VIEW_TYPE_LOADING;
        if (item instanceof BannerItem) return VIEW_TYPE_BANNER;
        return VIEW_TYPE_DEAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_BANNER:
                return new BannerViewHolder(inflater.inflate(R.layout.item_home_deal_banner, parent, false));
            case VIEW_TYPE_LOADING:
                return new LoadingViewHolder(inflater.inflate(R.layout.item_home_deal_loading, parent, false));
            default:
                return new DealViewHolder(inflater.inflate(R.layout.item_food_recommend_deal, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        if (holder instanceof DealViewHolder && item instanceof RecommendedDealModel) {
            bindDeal((DealViewHolder) holder, (RecommendedDealModel) item);
        } else if (holder instanceof BannerViewHolder && item instanceof BannerItem) {
            Glide.with(holder.itemView.getContext())
                .load(((BannerItem) item).imageUrl)
                .into(((BannerViewHolder) holder).ivBanner);
        }
        // LoadingViewHolder needs no binding
    }

    private void bindDeal(DealViewHolder holder, RecommendedDealModel deal) {
        holder.tvStoreName.setText(deal.getStoreName());
        holder.tvDistance.setText(deal.getDistance() + "km");
        holder.tvDeliveryTime.setText(deal.getDeliveryTime() + " phút");
        holder.ivFoodImage.clearAnimation();
        String imageUrl = deal.getImageUrl();
        android.graphics.drawable.ColorDrawable grayPlaceholder = new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E0E0E0"));
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            android.view.animation.AlphaAnimation blinkAnimation = new android.view.animation.AlphaAnimation(0.5f, 1.0f);
            blinkAnimation.setDuration(500);
            blinkAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
            blinkAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
            holder.ivFoodImage.startAnimation(blinkAnimation);

            com.bumptech.glide.request.RequestOptions options = new com.bumptech.glide.request.RequestOptions().placeholder(grayPlaceholder);
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .apply(options)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            holder.ivFoodImage.clearAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            holder.ivFoodImage.clearAnimation();
                            return false;
                        }
                    })
                    .into(holder.ivFoodImage);
        } else if (deal.getFoodImageResId() != 0) {
            holder.ivFoodImage.setImageResource(deal.getFoodImageResId());
        } else {
            holder.ivFoodImage.setImageDrawable(grayPlaceholder);
        }
        holder.tvDiscountTag.setText(deal.getDiscountTag());
        holder.tvFoodTitle.setText(deal.getFoodTitle());
        holder.tvSoldCount.setText(deal.getSoldCount() + " Đã bán");
        holder.tvOriginalPrice.setText(currencyFormatter.format(deal.getOriginalPrice()));
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvDiscountPrice.setText(currencyFormatter.format(deal.getDiscountPrice()));

        View.OnClickListener storeClickListener = v -> {
            Intent intent = new Intent(v.getContext(), StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, deal.getStoreName());
            v.getContext().startActivity(intent);
        };

        if (holder.layoutStoreInfo != null) {
            holder.layoutStoreInfo.setOnClickListener(storeClickListener);
        }

        View.OnClickListener clickListener = v -> {
            Intent intent = new Intent(v.getContext(), RecommendedDealDetailActivity.class);
            intent.putExtra("food_title", deal.getFoodTitle());
            intent.putExtra("store_name", deal.getStoreName());
            intent.putExtra("discount_price", deal.getDiscountPrice());
            intent.putExtra("original_price", deal.getOriginalPrice());
            intent.putExtra("distance", deal.getDistance());
            intent.putExtra("delivery_time", deal.getDeliveryTime());
            intent.putExtra("food_image", deal.getFoodImageResId());
            intent.putExtra("image_url", deal.getImageUrl());
            intent.putExtra("rating", deal.getRating());
            v.getContext().startActivity(intent);
        };

        // If user clicks the card outside the store info, or clicks Buy Now, it goes to Deal Detail
        holder.itemView.setOnClickListener(clickListener);
        holder.btnBuyNow.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // -- View Holders --

    public static class DealViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoreName, tvDistance, tvDeliveryTime, tvDiscountTag,
                tvFoodTitle, tvSoldCount, tvOriginalPrice, tvDiscountPrice, btnBuyNow;
        ImageView ivFoodImage;
        View layoutStoreInfo;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutStoreInfo = itemView.findViewById(R.id.layout_store_info);
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

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_deal_banner);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // -- Banner wrapper --

    public static class BannerItem {
        public final String imageUrl;

        public BannerItem(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
