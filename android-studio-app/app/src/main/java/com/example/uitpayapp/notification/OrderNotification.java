package com.example.uitpayapp.notification;

public class OrderNotification {
    private String id;
    private String title;
    private String content;
    private String timestamp;
    private int shopImageResId;

    public OrderNotification(String id, String title, String content, String timestamp, int shopImageResId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.shopImageResId = shopImageResId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public int getShopImageResId() { return shopImageResId; }
}