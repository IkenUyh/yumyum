package com.example.uitpayapp.modules.restaurant.models;

import com.google.gson.annotations.SerializedName;

public class UpdateRestaurantInfoDTO {
    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("openTime")
    private String openTime;

    @SerializedName("closeTime")
    private String closeTime;

    @SerializedName("imageUrl")
    private String imageUrl;

    public UpdateRestaurantInfoDTO(String name, String address, String openTime, String closeTime, String imageUrl) {
        this.name = name;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }
    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
