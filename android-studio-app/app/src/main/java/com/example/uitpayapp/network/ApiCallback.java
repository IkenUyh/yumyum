package com.example.uitpayapp.network;

public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}