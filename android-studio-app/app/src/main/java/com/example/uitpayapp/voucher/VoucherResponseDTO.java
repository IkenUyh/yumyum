package com.example.uitpayapp.voucher;

import java.math.BigDecimal;

public class VoucherResponseDTO {
    private Long id;
    private String code;
    private String type; // SHIPPING_DISCOUNT, ORDER_DISCOUNT
    private Integer discountPercent;
    private BigDecimal maxDiscount;
    private BigDecimal minOrderValue;
    private Integer stockQuantity;
    private String startDate;
    private String endDate;
    private Boolean isActive;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }

    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(BigDecimal minOrderValue) { this.minOrderValue = minOrderValue; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
