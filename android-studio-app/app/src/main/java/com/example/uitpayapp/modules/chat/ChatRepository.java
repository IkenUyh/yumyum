package com.example.uitpayapp.modules.chat;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.chat.models.responses.ChatMessageResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {

    private final ChatService chatService;

    public ChatRepository() {
        this.chatService = RetrofitClient.getChatService();
    }

    public void fetchChatHistory(Long orderId, final ApiCallback<List<ChatMessageResponse>> callback) {
        chatService.getChatHistory(orderId).enqueue(new Callback<ApiResponse<List<ChatMessageResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChatMessageResponse>>> call, Response<ApiResponse<List<ChatMessageResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ChatMessageResponse>> apiResponse = response.body();
                    // Giả định ApiResponse có hàm kiểm tra thành công hoặc check trực tiếp data
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("Không có dữ liệu lịch sử đoạn chat.");
                    }
                } else {
                    callback.onError("Lỗi kết nối hệ thống: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ChatMessageResponse>>> call, Throwable t) {
                callback.onError(t.toString());
            }
        });
    }
}