package com.example.uitpayapp.admin;

public class PendingDish {
    private String id;
    private String dishName;
    private String storeName;
    private String category;
    private double price;
    private int imageRes;
    private String status; // "pending", "approved", "rejected"
    private String rejectReason;
    private String submittedDate;

    public PendingDish(String id, String dishName, String storeName, String category, double price, int imageRes, String status, String submittedDate) {
        this.id = id;
        this.dishName = dishName;
        this.storeName = storeName;
        this.category = category;
        this.price = price;
        this.imageRes = imageRes;
        this.status = status;
        this.submittedDate = submittedDate;
    }

    public String getId() { return id; }
    public String getDishName() { return dishName; }
    public String getStoreName() { return storeName; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getImageRes() { return imageRes; }
    public String getStatus() { return status; }
    public String getRejectReason() { return rejectReason; }
    public String getSubmittedDate() { return submittedDate; }

    public void setStatus(String status) { this.status = status; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
