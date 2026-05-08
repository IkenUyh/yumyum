package com.example.uitpayapp.home.food_order;

import java.util.List;

public class Restaurant {
    private String name;
    private String shortName;
    private int bgColor;
    private String category;
    private List<FoodMenuItem> menu;

    public Restaurant(String name, String shortName, int bgColor, String category, List<FoodMenuItem> menu) {
        this.name = name;
        this.shortName = shortName;
        this.bgColor = bgColor;
        this.category = category;
        this.menu = menu;
    }

    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public int getBgColor() { return bgColor; }
    public String getCategory() { return category; }
    public List<FoodMenuItem> getMenu() { return menu; }
}
