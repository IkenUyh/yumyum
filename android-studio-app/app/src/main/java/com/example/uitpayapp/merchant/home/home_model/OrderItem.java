package com.example.uitpayapp.merchant.home.home_model;

public class OrderItem {
    private String dishName;
    private int quantity;
    private long price;

    public OrderItem(int quantity, String dishName, long price) {
        this.quantity = quantity;
        this.dishName = dishName;
        this.price = price;
    }

    public String getDishName() { return dishName; }
    public int getQuantity() { return quantity; }
    public long getPrice() { return price; }

    @Override
    public String toString() {
        return quantity + " x " + dishName;
    }
}
