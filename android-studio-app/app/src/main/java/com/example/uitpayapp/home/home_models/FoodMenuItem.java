package com.example.uitpayapp.home.home_models;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodMenuItem {
    private String id;
    private String name;
    private long price;
    private int imageResId;
    private String description;
    private String imageUrl;
    private Long restaurantId;

    public FoodMenuItem(String id, String name, long price, int imageResId, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
    }

    public FoodMenuItem(String id, String name, long price, int imageResId, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public long getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getFormattedPrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + "đ";
    }
}
