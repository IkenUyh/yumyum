package com.example.uitpayapp.history;

public class FoodOrder {
    private String orderId;
    private String merchantName;
    private String itemName;
    private long totalPrice;
    private int itemCount;
    private String date;
    private String status; // Hoàn thành / Đã hủy
    private String service; // Đồ ăn / Thức uống
    private int imageResId;
    private boolean isFavorite;
    private String category; // Tab: Đang đến, Lịch sử...

    public FoodOrder(String orderId, String merchantName, String itemName, long totalPrice, int itemCount, String date, String status, String service, int imageResId, boolean isFavorite, String category) {
        this.orderId = orderId;
        this.merchantName = merchantName;
        this.itemName = itemName;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
        this.date = date;
        this.status = status;
        this.service = service;
        this.imageResId = imageResId;
        this.isFavorite = isFavorite;
        this.category = category;
    }

    public String getOrderId() { return orderId; }
    public String getMerchantName() { return merchantName; }
    public String getItemName() { return itemName; }
    public long getTotalPrice() { return totalPrice; }
    public int getItemCount() { return itemCount; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getService() { return service; }
    public int getImageResId() { return imageResId; }
    public boolean isFavorite() { return isFavorite; }
    public String getCategory() { return category; }
}