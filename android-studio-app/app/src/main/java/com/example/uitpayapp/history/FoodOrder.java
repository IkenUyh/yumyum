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

    // Class con đại diện cho từng món trong đơn hàng
    public static class SubItem {
        private String name;
        private int imageResId;

        public SubItem(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public String getName() { return name; }
        public int getImageResId() { return imageResId; }
    }

    public FoodOrder(String orderId, String merchantName, long totalPrice, int itemCount, String date, String status, String service, boolean isFavorite, String category, List<SubItem> subItems) {
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
}