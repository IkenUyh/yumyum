package com.example.uitpayapp.merchant.shop.shop_model;

import java.io.Serializable;

public class MerchantMenuItem implements Serializable {
    private Long id;
    private Long categoryId;
    private String name;
    private double price;
    private int imageRes;
    private boolean isEnabled;
    private String description;
    private String imageUrl;

    public MerchantMenuItem(String name, double price, int imageRes, boolean isEnabled) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.isEnabled = isEnabled;
    }

    public MerchantMenuItem(Long id, Long categoryId, String name, double price, int imageRes, boolean isEnabled, String description, String imageUrl) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.isEnabled = isEnabled;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageRes() { return imageRes; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
