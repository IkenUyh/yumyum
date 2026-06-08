package com.example.uitpayapp.home.checkout;

public class VoucherModel {
    private String id;
    private String title;
    private String description;
    private long discountAmount;
    private long minOrderAmount;

    public VoucherModel(String id, String title, String description, long discountAmount, long minOrderAmount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getDiscountAmount() {
        return discountAmount;
    }

    public long getMinOrderAmount() {
        return minOrderAmount;
    }
}
