package com.example.uitpayapp.modules.wallet.models.requests;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class MerchantWalletWithdrawRequest {
    @SerializedName("amount")
    private BigDecimal amount;

    public MerchantWalletWithdrawRequest() {}

    public MerchantWalletWithdrawRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
