package com.example.uitpayapp.home.food_order;

public class FoodCategory {
    private String name;
    private String emoji;
    private int bgColor;

    public FoodCategory(String name, String emoji, int bgColor) {
        this.name = name;
        this.emoji = emoji;
        this.bgColor = bgColor;
    }

    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public int getBgColor() { return bgColor; }
}
