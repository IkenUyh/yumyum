package com.example.uitpayapp.modules.system.models.responses;

import com.google.gson.annotations.SerializedName;

public class SystemParameterResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("paramKey")
    private String paramKey;

    @SerializedName("paramValue")
    private String paramValue;

    @SerializedName("description")
    private String description;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getParamKey() { return paramKey; }
    public void setParamKey(String paramKey) { this.paramKey = paramKey; }

    public String getParamValue() { return paramValue; }
    public void setParamValue(String paramValue) { this.paramValue = paramValue; } // Đã sửa lỗi ở đây

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}