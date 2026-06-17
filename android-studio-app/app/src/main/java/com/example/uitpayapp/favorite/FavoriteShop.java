package com.example.uitpayapp.favorite;

public class FavoriteShop {
    private String id;
    private String name;
    private double rating;
    private double distance; // Đơn vị: km (để lọc Gần tôi)
    private int deliveryTime; // Đơn vị: phút
    private int imageResId;
    private String discountTag;
    private String serviceType; // Đồ ăn, Thực phẩm, Rượu bia, Hoa, Siêu thị
    private int orderCount; // Số lượng đặt để xếp vào cụm "Đặt Nhiều Nhất"
    private boolean isFavorited = true; // Theo dõi trạng thái yêu thích

    public FavoriteShop(String id, String name, double rating, double distance, int deliveryTime, int imageResId, String discountTag, String serviceType, int orderCount) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.distance = distance;
        this.deliveryTime = deliveryTime;
        this.imageResId = imageResId;
        this.discountTag = discountTag;
        this.serviceType = serviceType;
        this.orderCount = orderCount;
    }

    // Các hàm Getter
    public String getId() { return id; }
    public String getName() { return name; }
    public double getRating() { return rating; }
    public double getDistance() { return distance; }
    public int getDeliveryTime() { return deliveryTime; }
    public int getImageResId() { return imageResId; }
    public String getDiscountTag() { return discountTag; }
    public String getServiceType() { return serviceType; }
    public int getOrderCount() { return orderCount; }
    
    public boolean isFavorited() { return isFavorited; }
    public void setFavorited(boolean favorited) { isFavorited = favorited; }
}