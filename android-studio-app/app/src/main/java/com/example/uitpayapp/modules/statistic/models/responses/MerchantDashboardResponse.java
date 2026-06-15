package com.example.uitpayapp.modules.statistic.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class MerchantDashboardResponse {
    @SerializedName("totalRevenue")
    private BigDecimal totalRevenue;

    @SerializedName("totalCompletedOrders")
    private Long totalCompletedOrders;

    @SerializedName("topSellingFoods")
    private List<TopFoodResponse> topSellingFoods;

    // Constructors
    public MerchantDashboardResponse() {}

    public MerchantDashboardResponse(BigDecimal totalRevenue, Long totalCompletedOrders, List<TopFoodResponse> topSellingFoods) {
        this.totalRevenue = totalRevenue;
        this.totalCompletedOrders = totalCompletedOrders;
        this.topSellingFoods = topSellingFoods;
    }

    // Getters and Setters
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public Long getTotalCompletedOrders() { return totalCompletedOrders; }
    public void setTotalCompletedOrders(Long totalCompletedOrders) { this.totalCompletedOrders = totalCompletedOrders; }

    public List<TopFoodResponse> getTopSellingFoods() { return topSellingFoods; }
    public void setTopSellingFoods(List<TopFoodResponse> topSellingFoods) { this.topSellingFoods = topSellingFoods; }
}