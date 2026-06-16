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
}