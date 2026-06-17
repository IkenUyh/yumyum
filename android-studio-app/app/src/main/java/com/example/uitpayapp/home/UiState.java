package com.example.uitpayapp.home;

public class UiState<T> {
    public enum Status {
        SUCCESS, ERROR, LOADING, EMPTY
    }

    private final Status status;
    private final T data;
    private final String message;

    private UiState(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> UiState<T> success(T data) {
        return new UiState<>(Status.SUCCESS, data, null);
    }

    public static <T> UiState<T> empty() {
        return new UiState<>(Status.EMPTY, null, null);
    }

    public static <T> UiState<T> error(String msg, T data) {
        return new UiState<>(Status.ERROR, data, msg);
    }

    public static <T> UiState<T> loading(T data) {
        return new UiState<>(Status.LOADING, data, null);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() { return status == Status.SUCCESS; }
    public boolean isError() { return status == Status.ERROR; }
    public boolean isLoading() { return status == Status.LOADING; }
    public boolean isEmpty() { return status == Status.EMPTY; }
}
