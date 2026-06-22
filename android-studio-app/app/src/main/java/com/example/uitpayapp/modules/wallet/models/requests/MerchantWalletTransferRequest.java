package com.example.uitpayapp.modules.wallet.models.requests;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class MerchantWalletTransferRequest {
    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("restaurantId")
    private Long restaurantId;

    public MerchantWalletTransferRequest() {}

    public MerchantWalletTransferRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
