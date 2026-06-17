package com.example.uitpayapp.home.network;

import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import java.util.List;

public class HomeCoreResponse {
    private List<Banner> banners;
    private List<FoodCategory> categories;
    private List<FoodMenuItem> flashSales;
    private List<TopicResponse> topics;

    public List<Banner> getBanners() { return banners; }
    public void setBanners(List<Banner> banners) { this.banners = banners; }

    public List<FoodCategory> getCategories() { return categories; }
    public void setCategories(List<FoodCategory> categories) { this.categories = categories; }

    public List<FoodMenuItem> getFlashSales() { return flashSales; }
    public void setFlashSales(List<FoodMenuItem> flashSales) { this.flashSales = flashSales; }

    public List<TopicResponse> getTopics() { return topics; }
    public void setTopics(List<TopicResponse> topics) { this.topics = topics; }
}
