package com.example.uitpayapp.home.home_models;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodMenuItem {
    private String id;
    private String name;
    private long price;
    private int imageResId;
    private String description;
    private String imageUrl;
    private Long restaurantId;
    private String restaurantName;
    private Double restaurantLatitude;
    private Double restaurantLongitude;
    private String restaurantOpenTime;
    private String restaurantCloseTime;
    private Boolean isAcceptingOrders;
    private long originalPrice;
    private int discountPercent;
    private String discountType;
    private Double distance;
    private String sourcePromotion;

    public FoodMenuItem(String id, String name, long price, int imageResId, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
    }

    public FoodMenuItem(String id, String name, long price, int imageResId, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public long getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public Double getRestaurantLatitude() { return restaurantLatitude; }
    public void setRestaurantLatitude(Double restaurantLatitude) { this.restaurantLatitude = restaurantLatitude; }
    public Double getRestaurantLongitude() { return restaurantLongitude; }
    public void setRestaurantLongitude(Double restaurantLongitude) { this.restaurantLongitude = restaurantLongitude; }
    public String getRestaurantOpenTime() { return restaurantOpenTime; }
    public void setRestaurantOpenTime(String restaurantOpenTime) { this.restaurantOpenTime = restaurantOpenTime; }
    public String getRestaurantCloseTime() { return restaurantCloseTime; }
    public void setRestaurantCloseTime(String restaurantCloseTime) { this.restaurantCloseTime = restaurantCloseTime; }
    public Boolean getIsAcceptingOrders() { return isAcceptingOrders; }
    public void setIsAcceptingOrders(Boolean isAcceptingOrders) { this.isAcceptingOrders = isAcceptingOrders; }
    public long getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(long originalPrice) { this.originalPrice = originalPrice; }
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    public String getSourcePromotion() { return sourcePromotion; }
    public void setSourcePromotion(String sourcePromotion) { this.sourcePromotion = sourcePromotion; }

    private Integer reviewCount;
    private Double ratingAverage;

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public Double getRatingAverage() { return ratingAverage; }
    public void setRatingAverage(Double ratingAverage) { this.ratingAverage = ratingAverage; }

    public String getFormattedPrice() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + "đ";
    }
}

