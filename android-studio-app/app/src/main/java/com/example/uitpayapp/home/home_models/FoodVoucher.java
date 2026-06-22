package com.example.uitpayapp.home.home_models;

public class FoodVoucher {
    private String brandName;
    private String discount;
    private String condition;
    private int brandColor;
    private boolean collected;

    public FoodVoucher(String brandName, String discount, String condition, int brandColor) {
        this.brandName = brandName;
        this.discount = discount;
        this.condition = condition;
        this.brandColor = brandColor;
        this.collected = false;
    }

    public String getBrandName() { return brandName; }
    public String getDiscount() { return discount; }
    public String getCondition() { return condition; }
    public int getBrandColor() { return brandColor; }
    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
}
