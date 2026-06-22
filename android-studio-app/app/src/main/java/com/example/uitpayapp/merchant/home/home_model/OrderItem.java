package com.example.uitpayapp.merchant.home.home_model;

public class OrderItem {
    private Long foodId;    // ID thực từ API (dùng để gọi remove-item)
    private String dishName;
    private int quantity;
    private long price;
    private java.util.List<java.util.Map<String, Object>> selectedOptions;

    public OrderItem(Long foodId, int quantity, String dishName, long price, java.util.List<java.util.Map<String, Object>> selectedOptions) {
        this.foodId = foodId;
        this.quantity = quantity;
        this.dishName = dishName;
        this.price = price;
        this.selectedOptions = selectedOptions;
    }

    public OrderItem(Long foodId, int quantity, String dishName, long price) {
        this(foodId, quantity, dishName, price, null);
    }

    // Constructor tương thích ngược (mock data cũ, foodId = null)
    public OrderItem(int quantity, String dishName, long price) {
        this(null, quantity, dishName, price, null);
    }

    public Long getFoodId() { return foodId; }
    public String getDishName() { return dishName; }
    public int getQuantity() { return quantity; }
    public long getPrice() { return price; }
    public java.util.List<java.util.Map<String, Object>> getSelectedOptions() { return selectedOptions; }

    @Override
    public String toString() {
        return quantity + " x " + dishName;
    }
}
