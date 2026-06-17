package com.example.uitpayapp.modules.order.models.responses;

import com.google.gson.annotations.SerializedName;

public class OrderPreviewResponse {
    @SerializedName("foodTotal")
    private double foodTotal;

    @SerializedName("shippingFee")
    private double shippingFee;

    @SerializedName("totalDiscountAmount")
    private double totalDiscountAmount;

    @SerializedName("finalTotal")
    private double finalTotal;

    @SerializedName("distanceKm")
    private double distanceKm;

    public OrderPreviewResponse() {
    }

    public double getFoodTotal() {
        return foodTotal;
    }

    public void setFoodTotal(double foodTotal) {
        this.foodTotal = foodTotal;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(double totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }
}
