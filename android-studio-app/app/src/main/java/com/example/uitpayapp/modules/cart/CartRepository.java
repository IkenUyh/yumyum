package com.example.uitpayapp.modules.cart;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.cart.models.requests.CartItemRequestDTO;
import com.example.uitpayapp.modules.cart.models.responses.CartItemResponseDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private final CartService cartService;

    public CartRepository() {
        this.cartService = RetrofitClient.getCartService();
    }

    // Lấy danh sách giỏ hàng
    public void getCart(String token, final ApiCallback<List<CartItemResponseDTO>> callback) {
        cartService.getCart(token).enqueue(new Callback<ApiResponse<List<CartItemResponseDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CartItemResponseDTO>>> call, Response<ApiResponse<List<CartItemResponseDTO>>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CartItemResponseDTO>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Thêm hoặc Cập nhật số lượng món ăn
    public void addOrUpdateItem(String token, CartItemRequestDTO dto, final ApiCallback<String> callback) {
        cartService.addOrUpdateItem(token, dto).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Xóa một item khỏi giỏ hàng
    public void removeItem(Long itemId, String token, final ApiCallback<String> callback) {
        cartService.removeItem(itemId, token).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart(String token, final ApiCallback<String> callback) {
        cartService.clearCart(token).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                handleResponse(response, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Hàm Helper xử lý bóc tách ApiResponse chung
    private <T> void handleResponse(Response<ApiResponse<T>> response, ApiCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            // Tùy theo logic API backend: Trả về thành công dữ liệu hoặc thông báo chuỗi văn bản
            if (apiResponse.getData() != null) {
                callback.onSuccess(apiResponse.getData());
            } else if (apiResponse.getMessage() != null) {
                // Trường hợp cụ thể xử lý data kiểu String rỗng từ API backend thành công
                callback.onSuccess((T) apiResponse.getMessage());
            } else {
                callback.onError("Không nhận được phản hồi dữ liệu hợp lệ.");
            }
        } else {
            callback.onError("Lỗi kết nối hệ thống: " + response.code());
        }
    }
}