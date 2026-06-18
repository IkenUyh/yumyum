package com.example.uitpayapp.home.network;

import com.example.uitpayapp.home.home_models.FoodMenuItem;
import java.util.List;

public class TopicResponse {
    private String title;
    private String subtitle;
    private List<FoodMenuItem> items;

    public TopicResponse() {}

    public TopicResponse(String title, String subtitle, List<FoodMenuItem> items) {
        this.title = title;
        this.subtitle = subtitle;
        this.items = items;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public List<FoodMenuItem> getItems() { return items; }
    public List<FoodMenuItem> getFoods() { return items; }
}
