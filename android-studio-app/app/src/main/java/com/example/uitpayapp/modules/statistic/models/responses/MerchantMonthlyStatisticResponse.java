package com.example.uitpayapp.modules.statistic.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class MerchantMonthlyStatisticResponse {
    @SerializedName("month")
    private int month;

    @SerializedName("year")
    private int year;

    @SerializedName("totalRevenue")
    private BigDecimal totalRevenue;

    @SerializedName("avgDailyRevenue")
    private BigDecimal avgDailyRevenue;

    @SerializedName("highestDailyRevenue")
    private BigDecimal highestDailyRevenue;

    @SerializedName("lowestDailyRevenue")
    private BigDecimal lowestDailyRevenue;

    @SerializedName("totalTransactions")
    private long totalTransactions;

    @SerializedName("dailyStats")
    private List<DailyRevenueStatItem> dailyStats;

    public MerchantMonthlyStatisticResponse() {}

    public int getMonth() { return month; }
    public int getYear() { return year; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getAvgDailyRevenue() { return avgDailyRevenue; }
    public BigDecimal getHighestDailyRevenue() { return highestDailyRevenue; }
    public BigDecimal getLowestDailyRevenue() { return lowestDailyRevenue; }
    public long getTotalTransactions() { return totalTransactions; }
    public List<DailyRevenueStatItem> getDailyStats() { return dailyStats; }
}
