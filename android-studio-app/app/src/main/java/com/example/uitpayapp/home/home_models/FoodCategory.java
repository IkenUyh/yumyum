package com.example.uitpayapp.home.home_models;

public class FoodCategory {
    private String name;
    private String emoji;
    private int iconResId; // 0 = use emoji, >0 = use drawable
    private int bgColor;
    private boolean isSelectAll;

    // Legacy emoji constructor
    public FoodCategory(String name, String emoji, int bgColor) {
        this.name = name;
        this.emoji = emoji;
        this.iconResId = 0;
        this.bgColor = bgColor;
        this.isSelectAll = false;
    }

    public FoodCategory(String name, String emoji, int bgColor, boolean isSelectAll) {
        this.name = name;
        this.emoji = emoji;
        this.iconResId = 0;
        this.bgColor = bgColor;
        this.isSelectAll = isSelectAll;
    }

    // New icon constructor
    public FoodCategory(String name, int iconResId, int bgColor) {
        this.name = name;
        this.emoji = null;
        this.iconResId = iconResId;
        this.bgColor = bgColor;
        this.isSelectAll = false;
    }

    public FoodCategory(String name, int iconResId, int bgColor, boolean isSelectAll) {
        this.name = name;
        this.emoji = null;
        this.iconResId = iconResId;
        this.bgColor = bgColor;
        this.isSelectAll = isSelectAll;
    }

    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public int getIconResId() { return iconResId; }
    public int getBgColor() { return bgColor; }
    public boolean isSelectAll() { return isSelectAll; }
    public boolean hasIcon() { return iconResId != 0; }
}
