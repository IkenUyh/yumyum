package com.example.uitpayapp.modules.loyalty.models;

import com.google.gson.annotations.SerializedName;

public class LoyaltyResponseDTO {
    @SerializedName("currentPoints")
    private Integer currentPoints;

    @SerializedName("checkinStreak")
    private Integer checkinStreak;

    @SerializedName("lastCheckinDate")
    private String lastCheckinDate;

    @SerializedName("canCheckInToday")
    private Boolean canCheckInToday;

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Integer getCheckinStreak() {
        return checkinStreak;
    }

    public void setCheckinStreak(Integer checkinStreak) {
        this.checkinStreak = checkinStreak;
    }

    public String getLastCheckinDate() {
        return lastCheckinDate;
    }

    public void setLastCheckinDate(String lastCheckinDate) {
        this.lastCheckinDate = lastCheckinDate;
    }

    public Boolean getCanCheckInToday() {
        return canCheckInToday;
    }

    public void setCanCheckInToday(Boolean canCheckInToday) {
        this.canCheckInToday = canCheckInToday;
    }

    @SerializedName("totalSpending")
    private Long totalSpending;

    @SerializedName("rankName")
    private String rankName;

    public Long getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(Long totalSpending) {
        this.totalSpending = totalSpending;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }
}
