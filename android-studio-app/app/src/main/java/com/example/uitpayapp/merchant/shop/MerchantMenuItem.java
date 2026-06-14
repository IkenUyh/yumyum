package com.example.uitpayapp.merchant.shop;

public class MerchantMenuItem {
    private String name;
    private double price;
    private int imageRes;
    private boolean isEnabled;

    public MerchantMenuItem(String name, double price, int imageRes, boolean isEnabled) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.isEnabled = isEnabled;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageRes() { return imageRes; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
}
