package com.example.uitpayapp.home.home_models;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private Long dbId;
    private FoodMenuItem menuItem;
    private int quantity;
    private List<CartTopping> selectedToppings;

    public CartItem(FoodMenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedToppings = new ArrayList<>();
    }

    public CartItem(FoodMenuItem menuItem, int quantity, List<CartTopping> selectedToppings) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedToppings = selectedToppings != null ? selectedToppings : new ArrayList<>();
    }

    public CartItem(Long dbId, FoodMenuItem menuItem, int quantity, List<CartTopping> selectedToppings) {
        this.dbId = dbId;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.selectedToppings = selectedToppings != null ? selectedToppings : new ArrayList<>();
    }

    public Long getDbId() { return dbId; }
    public void setDbId(Long dbId) { this.dbId = dbId; }

    public FoodMenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public List<CartTopping> getSelectedToppings() {
        return selectedToppings;
    }
    
    public void setSelectedToppings(List<CartTopping> toppings) {
        this.selectedToppings = toppings != null ? toppings : new ArrayList<>();
    }

    public long getTotalPrice() {
        long itemPrice = menuItem.getPrice();
        long toppingsPrice = 0;
        for (CartTopping topping : selectedToppings) {
            toppingsPrice += topping.getPrice();
        }
        return (itemPrice + toppingsPrice) * quantity;
    }

    public String getToppingsString() {
        if (selectedToppings == null || selectedToppings.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedToppings.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(selectedToppings.get(i).getName());
        }
        return sb.toString();
    }
}
