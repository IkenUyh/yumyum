package com.example.uitpayapp.history;

public class RecommendedShop {
    private String name;
    private double rating;
    private double distance; // km
    private int deliveryTime; // phút
    private String promoText; // "Mã giảm 18%"
    private int imageResId;

    public RecommendedShop(String name, double rating, double distance, int deliveryTime, String promoText, int imageResId) {
        this.name = name;
        this.rating = rating;
        this.distance = distance;
        this.deliveryTime = deliveryTime;
        this.promoText = promoText;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public double getRating() { return rating; }
    public double getDistance() { return distance; }
    public int getDeliveryTime() { return deliveryTime; }
    public String getPromoText() { return promoText; }
    public int getImageResId() { return imageResId; }
}