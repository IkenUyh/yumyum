package com.example.uitpayapp.home.home_models;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodMenuItem {
    private String name;
    private long price;
    private int imageResId;
    private String description;

    public FoodMenuItem(String name, long price, int imageResId, String description) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
    }

    public String getName() { return name; }
    public long getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getDescription() { return description; }

    public String getFormattedPrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + "đ";
    }
}
