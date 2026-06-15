package com.example.uitpayapp.modules.wallet.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class TransactionResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("balanceAfter")
    private BigDecimal balanceAfter;

    @SerializedName("type")
    private String type;

    @SerializedName("referenceId")
    private String referenceId;

    @SerializedName("description")
    private String description;

    @SerializedName("createdAt")
    private String createdAt; // Nhận dạng String dạng ISO, Frontend tự format lại khi hiển thị

    // Getters and Setters
    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public String getType() { return type; }
    public String getReferenceId() { return referenceId; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }
}