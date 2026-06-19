package com.example.uitpayapp.merchant.shop.shop_model;

import java.util.List;

public class MerchantMenuCategory {
    private Long id;
    private String categoryName;
    private List<MerchantMenuItem> items;

    public MerchantMenuCategory(String categoryName, List<MerchantMenuItem> items) {
        this.categoryName = categoryName;
        this.items = items;
    }

    public MerchantMenuCategory(Long id, String categoryName, List<MerchantMenuItem> items) {
        this.id = id;
        this.categoryName = categoryName;
        this.items = items;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategoryName() { return categoryName; }
    public List<MerchantMenuItem> getItems() { return items; }
}
