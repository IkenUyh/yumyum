package com.example.uitpayapp.home.network;

import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import java.util.List;

public class HomeCoreResponse {
    private List<Banner> banners;
    private List<FoodCategory> categories;
    private List<FoodMenuItem> flashSales;
    public List<Banner> getBanners() { return banners; }
    public List<FoodCategory> getCategories() { return categories; }
    public List<FoodMenuItem> getFlashSales() { return flashSales; }
}
