package com.example.uitpayapp.home.network;

import com.example.uitpayapp.home.home_models.Restaurant;
import java.util.List;

public class BrandResponse {
    private List<Restaurant> brands;
    public List<Restaurant> getBrands() { return brands; }
    public void setBrands(List<Restaurant> brands) { this.brands = brands; }
}
