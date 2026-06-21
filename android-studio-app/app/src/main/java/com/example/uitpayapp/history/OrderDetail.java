package com.example.uitpayapp.history;

import java.util.List;

public class OrderDetail {
    private String orderId;
    private String status; // "DELIVERING" hoặc "COMPLETED"
    private String estimatedTime;
    private String merchantName;
    private String merchantAddress;
    private String destAddress;
    private String customerName;
    private String customerPhone;
    private DriverInfo driverInfo;
    private List<CartItem> items;
    private double subTotal;
    private double shippingFee;
    private double appFee;
    private double discount;
    private double totalPaid;
    private double merchantLatitude;
    private double merchantLongitude;
    private double destLatitude;
    private double destLongitude;

    // Constructors, Getters and Setters

    public double getMerchantLatitude() { return merchantLatitude; }
    public void setMerchantLatitude(double merchantLatitude) { this.merchantLatitude = merchantLatitude; }

    public double getMerchantLongitude() { return merchantLongitude; }
    public void setMerchantLongitude(double merchantLongitude) { this.merchantLongitude = merchantLongitude; }

    public double getDestLatitude() { return destLatitude; }
    public void setDestLatitude(double destLatitude) { this.destLatitude = destLatitude; }

    public double getDestLongitude() { return destLongitude; }
    public void setDestLongitude(double destLongitude) { this.destLongitude = destLongitude; }

    public static class DriverInfo {
        public String name;
        public float rating;
        public String licensePlate;
        public String avatarUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getAppFee() {
        return appFee;
    }

    public void setAppFee(double appFee) {
        this.appFee = appFee;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public static class CartItem {
        public String itemName;
        public String note;
        public double price;
        public int quantity;
        public String imageUrl;
    }

    private String paymentMethod;

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}