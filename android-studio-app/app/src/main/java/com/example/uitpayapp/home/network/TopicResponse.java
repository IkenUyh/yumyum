package com.example.uitpayapp.home.network;

import com.example.uitpayapp.home.home_models.FoodMenuItem;
import java.util.List;

public class TopicResponse {
    private String title;
    private String subtitle;
    private long categoryId;
    private List<FoodMenuItem> items;

    public TopicResponse() {}

    public TopicResponse(String title, String subtitle, List<FoodMenuItem> items) {
        this.title = title;
        this.subtitle = subtitle;
        this.items = items;
        this.categoryId = -1;
    }
    
    public TopicResponse(String title, String subtitle, long categoryId, List<FoodMenuItem> items) {
        this.title = title;
        this.subtitle = subtitle;
        this.categoryId = categoryId;
        this.items = items;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public long getCategoryId() { return categoryId; }
    public List<FoodMenuItem> getItems() { return items; }
    public List<FoodMenuItem> getFoods() { return items; }
}
