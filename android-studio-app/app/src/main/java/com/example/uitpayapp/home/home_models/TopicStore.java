package com.example.uitpayapp.home.home_models;

public class TopicStore {
    private String name;
    private int imageResId;

    public TopicStore(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
