package com.example.uitpayapp.home.home_models;

public class FoodCategory {
    private String name;
    private String emoji;
    private int bgColor;
    private boolean isSelectAll;

    public FoodCategory(String name, String emoji, int bgColor) {
        this.name = name;
        this.emoji = emoji;
        this.bgColor = bgColor;
        this.isSelectAll = false;
    }

    public FoodCategory(String name, String emoji, int bgColor, boolean isSelectAll) {
        this.name = name;
        this.emoji = emoji;
        this.bgColor = bgColor;
        this.isSelectAll = isSelectAll;
    }

    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public int getBgColor() { return bgColor; }
    public boolean isSelectAll() { return isSelectAll; }
}
