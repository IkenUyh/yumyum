package com.example.uitpayapp.merchant.shop.shop_model;

import java.io.Serializable;
import java.util.List;

public class ToppingGroup implements Serializable {
    private String name;
    private List<MerchantMenuItem> toppings;

    public ToppingGroup(String name, List<MerchantMenuItem> toppings) {
        this.name = name;
        this.toppings = toppings;
    }

    public String getName() { return name; }
    public List<MerchantMenuItem> getToppings() { return toppings; }
    public int getEnabledCount() {
        int count = 0;
        for (MerchantMenuItem item : toppings) {
            if (item.isEnabled()) count++;
        }
        return count;
    }
}
