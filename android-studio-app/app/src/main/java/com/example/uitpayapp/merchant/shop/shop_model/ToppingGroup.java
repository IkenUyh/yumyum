package com.example.uitpayapp.merchant.shop.shop_model;

import java.io.Serializable;
import java.util.List;

public class ToppingGroup implements Serializable {
    private Long id;
    private String name;
    private List<MerchantMenuItem> toppings;
    private boolean isRequired;
    private int maxChoices;

    public ToppingGroup(String name, List<MerchantMenuItem> toppings) {
        this.name = name;
        this.toppings = toppings;
    }

    public ToppingGroup(Long id, String name, List<MerchantMenuItem> toppings, boolean isRequired, int maxChoices) {
        this.id = id;
        this.name = name;
        this.toppings = toppings;
        this.isRequired = isRequired;
        this.maxChoices = maxChoices;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public List<MerchantMenuItem> getToppings() { return toppings; }
    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }
    public int getMaxChoices() { return maxChoices; }
    public void setMaxChoices(int maxChoices) { this.maxChoices = maxChoices; }

    public int getEnabledCount() {
        int count = 0;
        for (MerchantMenuItem item : toppings) {
            if (item.isEnabled()) count++;
        }
        return count;
    }
}
