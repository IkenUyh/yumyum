package com.example.uitpayapp.merchant.home.home_model;

public class SellerHistoryOrder {
    private Long orderId;      // ID thực từ API
    private String id;         // Chuỗi hiển thị "#20240004"
    private String customerName;
    private String status;
    private String pickupTime;
    private int itemCount;
    private String distance;
    private String orderDate;
    private String finishTime;
    private String totalPrice;

    public SellerHistoryOrder(Long orderId, String id, String customerName, String status,
                              String pickupTime, int itemCount, String distance,
                              String orderDate, String finishTime, String totalPrice) {
        this.orderId = orderId;
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.pickupTime = pickupTime;
        this.itemCount = itemCount;
        this.distance = distance;
        this.orderDate = orderDate;
        this.finishTime = finishTime;
        this.totalPrice = totalPrice;
    }

    public Long getOrderId() { return orderId; }
    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getStatus() { return status; }
    public String getPickupTime() { return pickupTime; }
    public int getItemCount() { return itemCount; }
    public String getDistance() { return distance; }
    public String getOrderDate() { return orderDate; }
    public String getFinishTime() { return finishTime; }
    public String getTotalPrice() { return totalPrice; }
}
