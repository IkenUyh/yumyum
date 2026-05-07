package com.example.uitpayapp.suggestion;

// SuggestionModel.java
public class SuggestionModel {
    public static final int TYPE_HORIZONTAL = 0;
    public static final int TYPE_VERTICAL = 1;

    private int viewType;
    private int imageResId;
    private String title;
    private String subtitle;

    public SuggestionModel(int viewType, int imageResId, String title, String subtitle) {
        this.viewType = viewType;
        this.imageResId = imageResId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getViewType() { return viewType; }
    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
}