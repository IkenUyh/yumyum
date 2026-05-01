package com.uit.zalopay_clone_api.common.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // Hàm tiện ích cho case Thành công
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Thành công")
                .data(data)
                .build();
    }

    // Hàm tiện ích cho case Thất bại
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}