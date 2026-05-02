package com.example.uitpayapp.models;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}