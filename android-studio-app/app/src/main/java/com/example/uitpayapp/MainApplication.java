package com.example.uitpayapp;

import android.app.Application;
import com.example.uitpayapp.network.RetrofitClient;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize RetrofitClient globally at application start
        RetrofitClient.initialize(this);
    }
}
