package com.example.uitpayapp.favorite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import com.example.uitpayapp.R;
import com.example.uitpayapp.home.StoreDetailActivity;
import java.util.List;

public class FavoriteMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TOP_HORIZONTAL = 0;
    private static final int TYPE_VERTICAL_SHOP = 1;
    private static final int TYPE_FOOTER = 2;

    private List<FavoriteShop> verticalShops;
    private List<FavoriteShop> horizontalShops;
    private FavoriteHorizontalAdapter.OnFavoriteRemoveListener removeListener;

    public FavoriteMainAdapter(List<FavoriteShop> verticalShops, List<FavoriteShop> horizontalShops,
            FavoriteHorizontalAdapter.OnFavoriteRemoveListener removeListener) {
        this.verticalShops = verticalShops;
        this.horizontalShops = horizontalShops;
        this.removeListener = removeListener;
    }

    @Override
    public int getItemViewType(int position) {
        // Vị trí đầu tiên luôn là danh sách cuộn ngang "Đặt Nhiều Nhất"
        if (position == 0)
            return TYPE_TOP_HORIZONTAL;
        // Vị trí cuối cùng luôn là dòng thông báo chân trang
        if (position == verticalShops.size() + 1)
            return TYPE_FOOTER;
        // Các vị trí còn lại là danh sách quán ăn hàng dọc
        return TYPE_VERTICAL_SHOP;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_TOP_HORIZONTAL) {
            // Nạp khung chứa danh sách cuộn ngang của bạn
            View v = inflater.inflate(R.layout.activity_favorite_horizontal_holder, parent, false);
            return new HorizontalContainerViewHolder(v);

        } else if (viewType == TYPE_FOOTER) {
            // Tạo trực tiếp một TextView làm chân trang để tránh phải tạo thêm file XML
            // layout thừa
            TextView tv = new TextView(parent.getContext());
            tv.setText("Đã hiển thị tất cả kết quả");
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setTextColor(0xFF9E9E9E); // Màu xám nhạt chuẩn mẫu ShopeeFood
            tv.setTextSize(13);

            // Đặt khoảng cách đệm trên dưới cho thoáng chữ
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 45, 0, 45);
            tv.setLayoutParams(lp);

            return new FooterViewHolder(tv);

        } else {
            // Nạp layout từng dòng quán ăn yêu thích hiển thị theo chiều dọc
            View v = inflater.inflate(R.layout.activity_favorite_item_vertical, parent, false);
            return new VerticalShopViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HorizontalContainerViewHolder) {
            HorizontalContainerViewHolder hvh = (HorizontalContainerViewHolder) holder;

            // Nếu không có quán nào đặt nhiều, ẩn toàn bộ cụm hàng ngang này đi
            if (horizontalShops.isEmpty()) {
                hvh.itemView.setVisibility(View.GONE);
                hvh.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                hvh.itemView.setVisibility(View.VISIBLE);
                hvh.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                // Khởi tạo và gắn Adapter con xử lý cuộn ngang
                FavoriteHorizontalAdapter subAdapter = new FavoriteHorizontalAdapter(horizontalShops, removeListener);
                hvh.rvHorizontal.setAdapter(subAdapter);
                hvh.rvHorizontal.setLayoutManager(
                        new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            }

        } else if (holder instanceof VerticalShopViewHolder) {
            // Do vị trí position = 0 đã bị chiếm bởi cụm hàng ngang,
            // nên data lấy từ list verticalShops phải lùi lại 1 index: (position - 1)
            FavoriteShop shop = verticalShops.get(position - 1);
            VerticalShopViewHolder vsh = (VerticalShopViewHolder) holder;

            // Đổ dữ liệu thật vào giao diện hàng dọc
            vsh.tvName.setText(shop.getName());
            vsh.tvInfo.setText("⭐ " + shop.getRating() + "  |  " + shop.getDistance() + "km  |  "
                    + shop.getDeliveryTime() + "phút");
            vsh.tvDiscount.setVisibility(View.GONE);
            String formattedUrl = getFormattedImageUrl(shop.getImageUrl());
            if (formattedUrl != null) {
                com.bumptech.glide.Glide.with(vsh.ivImage.getContext())
                        .load(formattedUrl)
                        .placeholder(R.drawable.img_food_chicken)
                        .error(shop.getImageResId() != 0 ? shop.getImageResId() : R.drawable.img_food_chicken)
                        .into(vsh.ivImage);
            } else {
                vsh.ivImage.setImageResource(shop.getImageResId() != 0 ? shop.getImageResId() : R.drawable.img_food_chicken);
            }

            if (shop.isFavorited()) {
                vsh.ivFavoriteHeart.setImageResource(R.drawable.favorite_filled_24px);
            } else {
                vsh.ivFavoriteHeart.setImageResource(R.drawable.favorite_border_24px);
            }

            vsh.ivFavoriteHeart.setOnClickListener(v -> {
                if (removeListener != null) {
                    removeListener.onRemove(shop);
                }
            });

            vsh.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StoreDetailActivity.class);
                intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, shop.getName());
                intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_ID, Long.parseLong(shop.getId()));
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        // Tổng số item = Số lượng quán dọc + 1 (Khối ngang đầu trang) + 1 (Dòng chữ
        // Footer chân trang)
        return verticalShops.size() + 2;
    }

    // --- Định nghĩa các ViewHolder quản lý ánh xạ ID ---

    static class HorizontalContainerViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvHorizontal;

        public HorizontalContainerViewHolder(@NonNull View itemView) {
            super(itemView);
            rvHorizontal = itemView.findViewById(R.id.rvHorizontal);
        }
    }

    static class VerticalShopViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo, tvDiscount;
        ImageView ivImage, ivFavoriteHeart;

        public VerticalShopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVerticalName);
            tvInfo = itemView.findViewById(R.id.tvVerticalInfo);
            tvDiscount = itemView.findViewById(R.id.tvVerticalDiscount);
            ivImage = itemView.findViewById(R.id.ivVerticalImage);
            ivFavoriteHeart = itemView.findViewById(R.id.ivVerticalFavoriteHeart);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private String getFormattedImageUrl(String path) {
        if (path == null || path.isEmpty() || "null".equalsIgnoreCase(path)) {
            return null;
        }
        String imageUrl = path;
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            String baseUrl = com.example.uitpayapp.network.RetrofitClient.getBaseUrl();
            if (baseUrl != null) {
                if (baseUrl.endsWith("/") && imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + imageUrl.substring(1);
                } else if (!baseUrl.endsWith("/") && !imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + "/" + imageUrl;
                } else {
                    imageUrl = baseUrl + imageUrl;
                }
            }
        }
        if (imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }
        return imageUrl;
    }
}