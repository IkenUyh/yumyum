package com.example.uitpayapp.home.network;

import com.example.uitpayapp.home.home_models.FoodMenuItem;
import java.util.List;

public class TopicResponse {
    private String title;
    private String subtitle;
    private List<FoodMenuItem> items;

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public List<FoodMenuItem> getItems() { return items; }
    public List<FoodMenuItem> getFoods() { return items; }
}
