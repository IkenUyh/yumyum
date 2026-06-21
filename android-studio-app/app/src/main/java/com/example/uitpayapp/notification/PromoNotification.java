package com.example.uitpayapp.notification;

public class PromoNotification {
    private String id;
    private String title;
    private String body; // Chứa văn bản mô tả chi tiết kèm emoji
    private String timestamp;
    private int imageResId;
    private boolean isRead;

    public PromoNotification(String id, String title, String body, String timestamp, int imageResId, boolean isRead) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.imageResId = imageResId;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getTimestamp() { return timestamp; }
    public int getImageResId() { return imageResId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}