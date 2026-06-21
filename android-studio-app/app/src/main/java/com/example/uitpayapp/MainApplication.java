package com.example.uitpayapp;

import android.app.Application;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.network.NetworkMonitor;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize RetrofitClient globally at application start
        RetrofitClient.initialize(this);
        // Initialize NetworkMonitor globally at application start
        NetworkMonitor.initialize(this);
    }
}
