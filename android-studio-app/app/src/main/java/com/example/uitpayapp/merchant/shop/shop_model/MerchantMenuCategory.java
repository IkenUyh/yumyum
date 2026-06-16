package com.example.uitpayapp.merchant.shop.shop_model;

import java.util.List;

public class MerchantMenuCategory {
    private String categoryName;
    private List<MerchantMenuItem> items;

    public MerchantMenuCategory(String categoryName, List<MerchantMenuItem> items) {
        this.categoryName = categoryName;
        this.items = items;
    }

    public String getCategoryName() { return categoryName; }
    public List<MerchantMenuItem> getItems() { return items; }
}
