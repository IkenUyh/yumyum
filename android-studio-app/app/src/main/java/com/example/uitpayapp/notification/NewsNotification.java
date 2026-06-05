package com.example.uitpayapp.notification;

public class NewsNotification {
    private String id;
    private String title;
    private String summary;
    private String timestamp;

    public NewsNotification(String id, String title, String summary, String timestamp) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getTimestamp() { return timestamp; }
}