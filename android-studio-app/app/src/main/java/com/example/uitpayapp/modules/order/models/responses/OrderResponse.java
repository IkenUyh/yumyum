package com.example.uitpayapp.modules.order.models.responses;
 
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("restaurantName")
    private String restaurantName;

    @SerializedName("restaurantImageUrl")
    private String restaurantImageUrl;

    @SerializedName("totalAmount")
    private BigDecimal totalAmount;

    @SerializedName("status")
    private String status;

    @SerializedName("deliveryMode")
    private String deliveryMode;

    @SerializedName("expectedDeliveryTime")
    private String expectedDeliveryTime; // Nhận dạng chuỗi ISO từ LocalDateTime backend

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("itemCount")
    private Integer itemCount;

    @SerializedName("items")
    private List<OrderItemResponse> items;

    // Thông tin khách hàng (dùng cho màn hình Seller)
    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("reviewed")
    private Boolean reviewed;

    @SerializedName("reviewExpired")
    private Boolean reviewExpired;

    @SerializedName("shippingFee")
    private BigDecimal shippingFee;

    @SerializedName("discountAmount")
    private BigDecimal discountAmount;

    @SerializedName("destAddress")
    private String destAddress;

    @SerializedName("restaurantLatitude")
    private BigDecimal restaurantLatitude;

    @SerializedName("restaurantLongitude")
    private BigDecimal restaurantLongitude;

    @SerializedName("destLatitude")
    private BigDecimal destLatitude;

    @SerializedName("destLongitude")
    private BigDecimal destLongitude;


    public static class OrderItemResponse {
        @SerializedName("name")
        private String name;

        @SerializedName("imageUrl")
        private String imageUrl;

        @SerializedName("quantity")
        private Integer quantity;

        @SerializedName("price")
        private BigDecimal price;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRestaurantImageUrl() { return restaurantImageUrl; }
    public void setRestaurantImageUrl(String restaurantImageUrl) { this.restaurantImageUrl = restaurantImageUrl; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(String deliveryMode) { this.deliveryMode = deliveryMode; }

    public String getExpectedDeliveryTime() { return expectedDeliveryTime; }
    public void setExpectedDeliveryTime(String expectedDeliveryTime) { this.expectedDeliveryTime = expectedDeliveryTime; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Boolean getReviewed() { return reviewed != null && reviewed; }
    public void setReviewed(Boolean reviewed) { this.reviewed = reviewed; }

    public Boolean getReviewExpired() { return reviewExpired != null && reviewExpired; }
    public void setReviewExpired(Boolean reviewExpired) { this.reviewExpired = reviewExpired; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getDestAddress() { return destAddress; }
    public void setDestAddress(String destAddress) { this.destAddress = destAddress; }

    public BigDecimal getRestaurantLatitude() { return restaurantLatitude; }
    public void setRestaurantLatitude(BigDecimal restaurantLatitude) { this.restaurantLatitude = restaurantLatitude; }

    public BigDecimal getRestaurantLongitude() { return restaurantLongitude; }
    public void setRestaurantLongitude(BigDecimal restaurantLongitude) { this.restaurantLongitude = restaurantLongitude; }

    public BigDecimal getDestLongitude() { return destLongitude; }
    public void setDestLongitude(BigDecimal destLongitude) { this.destLongitude = destLongitude; }

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("paymentUrl")
    private String paymentUrl;

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
}