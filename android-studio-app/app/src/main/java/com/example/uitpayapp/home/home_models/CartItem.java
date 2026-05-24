package com.example.uitpayapp.home.home_models;

public class CartItem {
    private FoodMenuItem menuItem;
    private int quantity;

    public CartItem(FoodMenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public FoodMenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public long getTotalPrice() {
        return menuItem.getPrice() * quantity;
    }
}
