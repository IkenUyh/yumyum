package com.example.uitpayapp.modules.order;

import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.order.models.requests.*;
import com.example.uitpayapp.modules.order.models.responses.OrderResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final OrderService orderService;

    public OrderRepository() {
        // Hãy đảm bảo bạn đã khai báo getOrderService() trong RetrofitClient theo mục 3 phía dưới
        this.orderService = RetrofitClient.getOrderService();
    }

    private <T> void enqueueCall(Call<ApiResponse<T>> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<T> apiResponse = response.body();
                    // Giả định backend trả về mã code thành công (ví dụ: 200 hoặc tùy định nghĩa của bạn)
                    if (apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Unknown Error");
                    }
                } else {
                    callback.onError("Không kết nối được server (Lỗi: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                callback.onError("Không kết nối được server (Lỗi: " + (t.getMessage() != null ? t.getMessage() : "Mạng") + ")");
            }
        });
    }

    public void previewOrder(CreateOrderRequest request, ApiCallback<com.example.uitpayapp.modules.order.models.responses.OrderPreviewResponse> callback) {
        enqueueCall(orderService.previewOrder(request), callback);
    }

    public void createOrder(CreateOrderRequest request, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.createOrder(request), callback);
    }

    public void cancelOrder(Long orderId, CancelOrderRequest request, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.cancelOrder(orderId, request), callback);
    }

    public void getAvailableOrders(ApiCallback<List<OrderResponse>> callback) {
        enqueueCall(orderService.getAvailableOrders(), callback);
    }

    public void acceptOrder(Long orderId, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.acceptOrder(orderId), callback);
    }

    public void completeOrder(Long orderId, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.completeOrder(orderId), callback);
    }

    public void getCustomerHistory(ApiCallback<List<OrderResponse>> callback) {
        enqueueCall(orderService.getCustomerHistory(), callback);
    }

    public void getMerchantHistory(ApiCallback<List<OrderResponse>> callback) {
        enqueueCall(orderService.getMerchantHistory(), callback);
    }

    public void merchantReady(Long orderId, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.merchantReady(orderId), callback);
    }

    public void driverPickup(Long orderId, ConfirmPickupRequest request, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.driverPickup(orderId, request), callback);
    }

    public void driverComplete(Long orderId, ConfirmDeliveryRequest request, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.driverComplete(orderId, request), callback);
    }

    public void removeItemFromOrder(Long orderId, RemoveItemRequest request, ApiCallback<OrderResponse> callback) {
        enqueueCall(orderService.removeItemFromOrder(orderId, request), callback);
    }
}