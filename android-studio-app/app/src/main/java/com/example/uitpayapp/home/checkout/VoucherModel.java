package com.example.uitpayapp.home.checkout;

public class VoucherModel {
    private String id;
    private String title;
    private String description;
    private long discountAmount;
    private long minOrderAmount;
    private String type; // SHIPPING_DISCOUNT, ORDER_DISCOUNT

    public VoucherModel(String id, String title, String description, long discountAmount, long minOrderAmount) {
        this(id, title, description, discountAmount, minOrderAmount, null);
    }

    public VoucherModel(String id, String title, String description, long discountAmount, long minOrderAmount, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount;
        this.type = type;
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

    public String getType() {
        return type;
    }
}
