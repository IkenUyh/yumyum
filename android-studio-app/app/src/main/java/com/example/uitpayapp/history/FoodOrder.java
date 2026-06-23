package com.example.uitpayapp.history;

import java.util.List;

public class FoodOrder {
    private String orderId;
    private String merchantName;
    private long totalPrice;
    private int itemCount;
    private String date;
    private String status;
    private String service;
    private boolean isFavorite;
    private String category;
    private List<SubItem> subItems; // ĐÃ THÊM: Danh sách các món ăn chi tiết bên trong đơn
    private boolean isReviewed;
    private boolean isReviewExpired;
    private String merchantImageUrl;

    // Class con đại diện cho từng món trong đơn hàng
    public static class SubItem {
        private String name;
        private int imageResId;
        private String imageUrl;

        public SubItem(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public SubItem(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.imageResId = 0;
        }

        public String getName() { return name; }
        public int getImageResId() { return imageResId; }
        public String getImageUrl() { return imageUrl; }
    }

    public FoodOrder(String orderId, String merchantName, long totalPrice, int itemCount, String date, String status, String service, boolean isFavorite, String category, List<SubItem> subItems) {
        this(orderId, merchantName, totalPrice, itemCount, date, status, service, isFavorite, category, subItems, false, false);
    }

    public FoodOrder(String orderId, String merchantName, long totalPrice, int itemCount, String date, String status, String service, boolean isFavorite, String category, List<SubItem> subItems, boolean isReviewed) {
        this(orderId, merchantName, totalPrice, itemCount, date, status, service, isFavorite, category, subItems, isReviewed, false);
    }

    public FoodOrder(String orderId, String merchantName, long totalPrice, int itemCount, String date, String status, String service, boolean isFavorite, String category, List<SubItem> subItems, boolean isReviewed, boolean isReviewExpired) {
        this.orderId = orderId;
        this.merchantName = merchantName;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
        this.date = date;
        this.status = status;
        this.service = service;
        this.isFavorite = isFavorite;
        this.category = category;
        this.subItems = subItems;
        this.isReviewed = isReviewed;
        this.isReviewExpired = isReviewExpired;
    }

    public String getOrderId() { return orderId; }
    public String getMerchantName() { return merchantName; }
    public long getTotalPrice() { return totalPrice; }
    public int getItemCount() { return itemCount; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getService() { return service; }
    public boolean isFavorite() { return isFavorite; }
    public String getCategory() { return category; }
    public List<SubItem> getSubItems() { return subItems; }
    public boolean isReviewed() { return isReviewed; }
    public void setReviewed(boolean reviewed) { this.isReviewed = reviewed; }
    public boolean isReviewExpired() { return isReviewExpired; }
    public void setReviewExpired(boolean reviewExpired) { this.isReviewExpired = reviewExpired; }
    public String getMerchantImageUrl() { return merchantImageUrl; }
    public void setMerchantImageUrl(String merchantImageUrl) { this.merchantImageUrl = merchantImageUrl; }
}