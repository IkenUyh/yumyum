package com.uit.zalopay_clone_api.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        // Lấy đúng cái dòng chữ "Số điện thoại này đã được đăng ký!" để nhét vào JSON
        errorResponse.put("error", ex.getMessage());

        // Trả về mã 400 (Bad Request - Lỗi do người dùng nhập sai) thay vì 500 (Lỗi server)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}