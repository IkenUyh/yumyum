package com.example.uitpayapp.modules.wallet.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class BalanceResponse {
    @SerializedName("balance")
    private BigDecimal balance;

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}