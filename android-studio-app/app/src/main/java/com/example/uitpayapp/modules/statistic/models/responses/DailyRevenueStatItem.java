package com.example.uitpayapp.modules.statistic.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class DailyRevenueStatItem {
    @SerializedName("day")
    private int day;

    @SerializedName("revenue")
    private BigDecimal revenue;

    @SerializedName("transactionCount")
    private long transactionCount;

    public DailyRevenueStatItem() {}

    public int getDay() {
        return day;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public long getTransactionCount() {
        return transactionCount;
    }
}
