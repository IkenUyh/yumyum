package com.example.uitpayapp.merchant.home.home_model;

public class OrderItem {
    private Long foodId;    // ID thực từ API (dùng để gọi remove-item)
    private String dishName;
    private int quantity;
    private long price;

    public OrderItem(Long foodId, int quantity, String dishName, long price) {
        this.foodId = foodId;
        this.quantity = quantity;
        this.dishName = dishName;
        this.price = price;
    }

    // Constructor tương thích ngược (mock data cũ, foodId = null)
    public OrderItem(int quantity, String dishName, long price) {
        this(null, quantity, dishName, price);
    }

    public Long getFoodId() { return foodId; }
    public String getDishName() { return dishName; }
    public int getQuantity() { return quantity; }
    public long getPrice() { return price; }

    @Override
    public String toString() {
        return quantity + " x " + dishName;
    }
}
