package com.example.uitpayapp.modules.statistic.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class MerchantDailyStatisticResponse {
    @SerializedName("totalRevenue")
    private BigDecimal totalRevenue;

    @SerializedName("transactionCount")
    private Long transactionCount;

    public MerchantDailyStatisticResponse() {}

    public MerchantDailyStatisticResponse(BigDecimal totalRevenue, Long transactionCount) {
        this.totalRevenue = totalRevenue;
        this.transactionCount = transactionCount;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }
}
