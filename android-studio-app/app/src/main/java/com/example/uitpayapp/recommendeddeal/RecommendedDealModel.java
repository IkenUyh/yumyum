package com.example.uitpayapp.recommendeddeal;

public class RecommendedDealModel {
    private String storeName;
    private double distance; //DVT: km
    private int deliveryTime; //DVT: phút
    private int foodImageResId;
    private String discountTag;
    private String foodTitle;
    private long soldCount;
    private double originalPrice;
    private double discountPrice;
    private String imageUrl;
    private double rating;
    private Long restaurantId;

    public RecommendedDealModel(String storeName, double distance, int deliveryTime, int foodImageResId,
                               String discountTag, String foodTitle, long soldCount,
                               double originalPrice, double discountPrice, String imageUrl, double rating) {
        this.storeName = storeName;
        this.distance = distance;
        this.deliveryTime = deliveryTime;
        this.foodImageResId = foodImageResId;
        this.discountTag = discountTag;
        this.foodTitle = foodTitle;
        this.soldCount = soldCount;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.restaurantId = null;
    }

    public RecommendedDealModel(String storeName, double distance, int deliveryTime, int foodImageResId,
                               String discountTag, String foodTitle, long soldCount,
                               double originalPrice, double discountPrice, String imageUrl, double rating, Long restaurantId) {
        this.storeName = storeName;
        this.distance = distance;
        this.deliveryTime = deliveryTime;
        this.foodImageResId = foodImageResId;
        this.discountTag = discountTag;
        this.foodTitle = foodTitle;
        this.soldCount = soldCount;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.restaurantId = restaurantId;
    }

    public String getStoreName() { return storeName; }
    public double getDistance() { return distance; }
    public int getDeliveryTime() { return deliveryTime; }
    public int getFoodImageResId() { return foodImageResId; }
    public String getDiscountTag() { return discountTag; }
    public String getFoodTitle() { return foodTitle; }
    public long getSoldCount() { return soldCount; }
    public double getOriginalPrice() { return originalPrice; }
    public double getDiscountPrice() { return discountPrice; }
    public String getImageUrl() { return imageUrl; }
    public double getRating() { return rating; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
}
