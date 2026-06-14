package com.example.uitpayapp.merchant.home.home_model;

import java.util.List;

public class SellerOrder {
    private String customerName;
    private String avatarUrl;
    private int numberOfDishes;
    private String totalPrice;
    private String status; // new, confirmed
    private String guestNote;
    private List<OrderItem> dishes;

    public SellerOrder(String customerName, String avatarUrl, int numberOfDishes, String totalPrice, String status, String guestNote, List<OrderItem> dishes) {
        this.customerName = customerName;
        this.avatarUrl = avatarUrl;
        this.numberOfDishes = numberOfDishes;
        this.totalPrice = totalPrice;
        this.status = status;
        this.guestNote = guestNote;
        this.dishes = dishes;
    }

    public String getCustomerName() { return customerName; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getNumberOfDishes() { return numberOfDishes; }
    public String getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getGuestNote() { return guestNote; }
    public List<OrderItem> getDishes() { return dishes; }
    public void setStatus(String status) { this.status = status; }
}
