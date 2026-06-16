package com.example.uitpayapp.merchant.notification;

public class SellerNotification {
    private String id;
    private String title;
    private String content;
    private String time;
    private boolean isRead;
    private int type; // 1: Order, 2: Promo, 3: System, 4: Review

    public SellerNotification(String id, String title, String content, String time, boolean isRead, int type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.isRead = isRead;
        this.type = type;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public int getType() { return type; }
}
