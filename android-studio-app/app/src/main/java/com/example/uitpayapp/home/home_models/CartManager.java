package com.example.uitpayapp.home.home_models;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class CartManager {

    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCart() {
        return cartItems;
    }

    public void addItem(CartItem item) {
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem existing = cartItems.get(i);
            
            // Check if food name and toppings match
            boolean sameName = existing.getMenuItem().getName().equals(item.getMenuItem().getName());
            boolean sameToppings = new HashSet<>(existing.getSelectedToppings()).equals(new HashSet<>(item.getSelectedToppings()));
            
            if (sameName && sameToppings) {
                int newQty = existing.getQuantity() + item.getQuantity();
                CartItem updatedItem = new CartItem(item.getMenuItem(), newQty, item.getSelectedToppings());
                cartItems.set(i, updatedItem);
                return;
            }
        }
        cartItems.add(item);
    }
    
    public void updateItem(int position, CartItem newItem) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            
            boolean merged = false;
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem existing = cartItems.get(i);
                boolean sameName = existing.getMenuItem().getName().equals(newItem.getMenuItem().getName());
                boolean sameToppings = new HashSet<>(existing.getSelectedToppings()).equals(new HashSet<>(newItem.getSelectedToppings()));
                
                if (sameName && sameToppings) {
                    int newQty = existing.getQuantity() + newItem.getQuantity();
                    CartItem updatedItem = new CartItem(newItem.getMenuItem(), newQty, newItem.getSelectedToppings());
                    cartItems.set(i, updatedItem);
                    merged = true;
                    break;
                }
            }
            
            if (!merged) {
                cartItems.add(position, newItem);
            }
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void updateQuantity(int position, int quantity) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).setQuantity(quantity);
        }
    }

    public void clearCart() {
        cartItems.clear();
    }

    public long getTotalPrice() {
        long total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getTotalItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public String getFormattedTotalPrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(getTotalPrice()) + "đ";
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public String getProductSummary() {
        StringBuilder sb = new StringBuilder();
        for (CartItem ci : cartItems) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(ci.getQuantity()).append("x ").append(ci.getMenuItem().getName());
        }
        return sb.toString();
    }
}
