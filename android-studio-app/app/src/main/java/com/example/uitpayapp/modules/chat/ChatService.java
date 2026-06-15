package com.example.uitpayapp.modules.chat;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.modules.chat.models.responses.ChatMessageResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatService {

    // REST API Lấy lịch sử chat theo mã đơn hàng
    @GET("api/v1/chat/history/{orderId}")
    Call<ApiResponse<List<ChatMessageResponse>>> getChatHistory(@Path("orderId") Long orderId);
}