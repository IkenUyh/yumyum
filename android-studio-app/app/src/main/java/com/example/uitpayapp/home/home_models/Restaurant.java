package com.example.uitpayapp.home.home_models;

import java.util.List;

public class Restaurant implements java.io.Serializable {
    private Long id;
    private String name;
    private String shortName;
    private int bgColor;
    private String category;
    private List<FoodMenuItem> menu;
    private int imageResId;
    private double rating;
    private int reviewCount;
    private int deliveryTime;
    private String address;
    private String imageUrl;

    public Restaurant(String name, String shortName, int bgColor, String category, List<FoodMenuItem> menu,
            int imageResId, double rating, int reviewCount, int deliveryTime, String address) {
        this.name = name;
        this.shortName = shortName;
        this.bgColor = bgColor;
        this.category = category;
        this.menu = menu;
        this.imageResId = imageResId;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.deliveryTime = deliveryTime;
        this.address = address;
    }

    public Restaurant(Long id, String name, String shortName, int bgColor, String category, List<FoodMenuItem> menu,
            int imageResId, double rating, int reviewCount, int deliveryTime, String address, String imageUrl) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.bgColor = bgColor;
        this.category = category;
        this.menu = menu;
        this.imageResId = imageResId;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.imageUrl = imageUrl;
    }

    public Restaurant(Long id, String name, String shortName, int bgColor, String category, List<FoodMenuItem> menu,
            int imageResId, double rating, int reviewCount, int deliveryTime, String address) {
        this(id, name, shortName, bgColor, category, menu, imageResId, rating, reviewCount, deliveryTime, address, null);
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public int getBgColor() {
        return bgColor;
    }

    public String getCategory() {
        return category;
    }

    public List<FoodMenuItem> getMenu() {
        return menu;
    }

    public int getImageResId() {
        return imageResId;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public String getAddress() {
        return address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
