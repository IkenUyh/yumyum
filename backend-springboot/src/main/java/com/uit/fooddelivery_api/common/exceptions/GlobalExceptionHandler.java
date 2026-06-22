package com.uit.fooddelivery_api.common.exceptions;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        // Trả về mã lỗi 400 kèm câu thông báo, data để null
        ApiResponse<Object> errorResponse = ApiResponse.error(400, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Bắt lỗi khi 2 người cùng mua Flashsale tại 1 thời điểm (Người chậm hơn sẽ dính lỗi này)
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Object>> handleOptimisticLocking(org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
        ApiResponse<Object> errorResponse = ApiResponse.error(409, "Hệ thống đang quá tải hoặc món ăn Flashsale bạn chọn vừa bị người khác nhanh tay mua mất! Vui lòng tải lại trang và đặt lại.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}