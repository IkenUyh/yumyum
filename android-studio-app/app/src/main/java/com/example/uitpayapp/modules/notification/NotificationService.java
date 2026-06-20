package com.example.uitpayapp.modules.notification;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.notification.models.NotificationResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {

    // Lấy lịch sử thông báo (mặc định 30 ngày gần nhất)
    // Truyền month + year để lọc theo tháng cụ thể
    @GET("api/v1/notifications/history")
    Call<ApiResponse<List<NotificationResponseDTO>>> getHistory(
            @Query("month") Integer month,
            @Query("year") Integer year
    );

    // Lấy số lượng thông báo chưa đọc
    @GET("api/v1/notifications/unread-count")
    Call<ApiResponse<Map<String, Long>>> getUnreadCount();

    // Đánh dấu 1 thông báo là đã đọc
    @PATCH("api/v1/notifications/mark-read/{id}")
    Call<ApiResponse<String>> markAsRead(@Path("id") Long id);

    // Đánh dấu tất cả là đã đọc
    @PATCH("api/v1/notifications/mark-all-read")
    Call<ApiResponse<String>> markAllAsRead();
}
