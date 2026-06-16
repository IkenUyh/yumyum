package com.example.uitpayapp.modules.restaurant.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardResponseDTO {
    @SerializedName("totalRevenue")
    private Double totalRevenue;

    @SerializedName("totalCompletedOrders")
    private Long totalCompletedOrders;

    @SerializedName("bestSellers")
    private List<FoodSaleStatDTO> bestSellers;

    @SerializedName("worstSellers")
    private List<FoodSaleStatDTO> worstSellers;

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    public Long getTotalCompletedOrders() { return totalCompletedOrders; }
    public void setTotalCompletedOrders(Long totalCompletedOrders) { this.totalCompletedOrders = totalCompletedOrders; }
    public List<FoodSaleStatDTO> getBestSellers() { return bestSellers; }
    public void setBestSellers(List<FoodSaleStatDTO> bestSellers) { this.bestSellers = bestSellers; }
    public List<FoodSaleStatDTO> getWorstSellers() { return worstSellers; }
    public void setWorstSellers(List<FoodSaleStatDTO> worstSellers) { this.worstSellers = worstSellers; }
}
