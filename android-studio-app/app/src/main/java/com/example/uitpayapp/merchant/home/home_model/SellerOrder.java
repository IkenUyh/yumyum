package com.example.uitpayapp.merchant.home.home_model;

import java.util.List;

public class SellerOrder {
    private String id;
    private String customerName;
    private String avatarUrl;
    private int numberOfDishes;
    private String totalPrice;
    private String status; // new, confirmed
    private String guestNote;
    private List<OrderItem> dishes;

    public SellerOrder(String id, String customerName, String avatarUrl, int numberOfDishes, String totalPrice, String status, String guestNote, List<OrderItem> dishes) {
        this.id = id;
        this.customerName = customerName;
        this.avatarUrl = avatarUrl;
        this.numberOfDishes = numberOfDishes;
        this.totalPrice = totalPrice;
        this.status = status;
        this.guestNote = guestNote;
        this.dishes = dishes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getNumberOfDishes() { return numberOfDishes; }
    public void setNumberOfDishes(int numberOfDishes) { this.numberOfDishes = numberOfDishes; }
    public String getTotalPrice() { return totalPrice; }
    public void setTotalPrice(String totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGuestNote() { return guestNote; }
    public List<OrderItem> getDishes() { return dishes; }
}
