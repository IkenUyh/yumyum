package com.example.uitpayapp.modules.restaurant.models;

import com.google.gson.annotations.SerializedName;

public class RestaurantResponseDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("openTime")
    private String openTime;

    @SerializedName("closeTime")
    private String closeTime;

    @SerializedName("merchantId")
    private Long merchantId;

    @SerializedName("isAcceptingOrders")
    private Boolean isAcceptingOrders;

    @SerializedName("maxPendingOrders")
    private Integer maxPendingOrders;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }
    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public Boolean getIsAcceptingOrders() { return isAcceptingOrders; }
    public void setIsAcceptingOrders(Boolean isAcceptingOrders) { this.isAcceptingOrders = isAcceptingOrders; }
    public Integer getMaxPendingOrders() { return maxPendingOrders; }
    public void setMaxPendingOrders(Integer maxPendingOrders) { this.maxPendingOrders = maxPendingOrders; }
}
