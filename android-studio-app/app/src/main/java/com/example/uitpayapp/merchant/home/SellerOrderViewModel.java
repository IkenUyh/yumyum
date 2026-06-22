package com.example.uitpayapp.merchant.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.uitpayapp.merchant.home.home_model.OrderItem;
import com.example.uitpayapp.merchant.home.home_model.SellerHistoryOrder;
import com.example.uitpayapp.merchant.home.home_model.SellerOrder;
import com.example.uitpayapp.modules.order.OrderRepository;
import com.example.uitpayapp.modules.order.models.requests.CancelOrderRequest;
import com.example.uitpayapp.modules.order.models.requests.RemoveItemRequest;
import com.example.uitpayapp.modules.order.models.responses.OrderResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.SessionManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.util.Log;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SellerOrderViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    // Đơn mới (PENDING)
    private final MutableLiveData<List<SellerOrder>> newOrders = new MutableLiveData<>(new ArrayList<>());
    // Đã xác nhận / Đang chuẩn bị (PREPARING)
    private final MutableLiveData<List<SellerOrder>> confirmedOrders = new MutableLiveData<>(new ArrayList<>());
    // Lịch sử (DELIVERING, COMPLETED, CANCELLED)
    private final MutableLiveData<List<SellerHistoryOrder>> historyOrders = new MutableLiveData<>(new ArrayList<>());

    private StompClient mStompClient;
    private long connectedStoreId = -1L;
    private final MutableLiveData<String> newOrderEvent = new MutableLiveData<>();
    private final android.os.Handler reconnectHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    // Polling fallback - tự động refresh mỗi 5 giây như customer side
    private final android.os.Handler pollingHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable pollingRunnable;
    private boolean isPollingStarted = false;

    // UI feedback
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private final java.util.Set<String> notifiedOrderIds = new java.util.HashSet<>();
    private boolean isFirstLoadDone = false;

    private final List<SellerOrder> rawNewOrders = new java.util.ArrayList<>();
    private final List<SellerOrder> rawConfirmedOrders = new java.util.ArrayList<>();
    private final List<SellerHistoryOrder> rawHistoryOrders = new java.util.ArrayList<>();
    private String currentSearchQuery = "";

    public SellerOrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository();
    }

    public LiveData<List<SellerOrder>> getNewOrders() { return newOrders; }
    public LiveData<List<SellerOrder>> getConfirmedOrders() { return confirmedOrders; }
    public LiveData<List<SellerHistoryOrder>> getHistoryOrders() { return historyOrders; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<String> getNewOrderEvent() { return newOrderEvent; }

    public void clearNewOrderEvent() {
        newOrderEvent.setValue(null);
    }

    /**
     * Bắt đầu polling mỗi 5 giây để đảm bảo cập nhật realtime kể cả khi WebSocket lỗi.
     * Giống với cơ chế polling trong customer OrderDetailActivity.
     */
    public void startPolling() {
        if (isPollingStarted) return;
        isPollingStarted = true;
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                // Gọi silent refresh (không hiện loading indicator để tránh nhấp nháy)
                loadDataSilent();
                pollingHandler.postDelayed(this, 5000);
            }
        };
        pollingHandler.postDelayed(pollingRunnable, 5000);
        Log.d("SellerPolling", "Polling started - refresh every 5s");
    }

    /**
     * Dừng polling khi Activity bị destroy.
     */
    public void stopPolling() {
        isPollingStarted = false;
        pollingHandler.removeCallbacksAndMessages(null);
        Log.d("SellerPolling", "Polling stopped");
    }

    /**
     * Load dữ liệu không hiện loading spinner - dùng cho background polling.
     */
    private void loadDataSilent() {
        android.content.SharedPreferences prefs = getApplication()
                .getSharedPreferences("SellerPrefs", android.content.Context.MODE_PRIVATE);
        long currentStoreId = prefs.getLong("current_store_id", -1L);

        orderRepository.getMerchantHistory(new ApiCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> result) {
                List<OrderResponse> filtered = new ArrayList<>();
                for (OrderResponse o : result) {
                    if (currentStoreId == -1L || (o.getRestaurantId() != null && o.getRestaurantId() == currentStoreId)) {
                        filtered.add(o);
                    }
                }
                classifyOrders(filtered);
            }

            @Override
            public void onError(String message) {
                Log.e("SellerPolling", "Silent polling error: " + message);
            }
        });
    }

    /**
     * Tải toàn bộ đơn hàng của quán từ API, phân loại theo trạng thái.
     */
    public void loadData() {
        isLoading.setValue(true);

        // Lấy store_id hiện tại từ SellerPrefs để lọc đơn hàng
        android.content.SharedPreferences prefs = getApplication()
                .getSharedPreferences("SellerPrefs", android.content.Context.MODE_PRIVATE);
        long currentStoreId = prefs.getLong("current_store_id", -1L);

        if (currentStoreId != -1L && (connectedStoreId != currentStoreId || mStompClient == null || !mStompClient.isConnected())) {
            initWebSocket(currentStoreId);
        }

        orderRepository.getMerchantHistory(new ApiCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> result) {
                isLoading.setValue(false);
                // Lọc chỉ lấy đơn của quán đang chọn
                List<OrderResponse> filtered = new ArrayList<>();
                for (OrderResponse o : result) {
                    if (currentStoreId == -1L || (o.getRestaurantId() != null && o.getRestaurantId() == currentStoreId)) {
                        filtered.add(o);
                    }
                }
                classifyOrders(filtered);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue("Không tải được đơn hàng: " + message);
            }
        });
    }

    /**
     * Phân loại danh sách đơn từ API vào 3 LiveData theo trạng thái.
     */
    private void classifyOrders(List<OrderResponse> orders) {
        List<SellerOrder> pending = new ArrayList<>();
        List<SellerOrder> preparing = new ArrayList<>();
        List<SellerHistoryOrder> history = new ArrayList<>();

        SimpleDateFormat displayDateFmt = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        displayDateFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat displayTimeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        displayTimeFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        for (OrderResponse o : orders) {
            String status = o.getStatus() != null ? o.getStatus().toUpperCase() : "";

            switch (status) {
                case "PENDING":
                case "PREPARING":
                case "DELIVERING":
                    SellerOrder sellerOrder = mapToSellerOrder(o);
                    if ("PENDING".equals(status)) {
                        pending.add(sellerOrder);
                    } else {
                        preparing.add(sellerOrder);
                    }
                    break;

                default:
                    // DELIVERING, COMPLETED, CANCELLED → lịch sử
                    String displayStatus = mapStatusToVietnamese(status);
                    String dateStr = "";
                    String timeStr = "";
                    if (o.getCreatedAt() != null && !o.getCreatedAt().isEmpty()) {
                        try {
                            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                            iso.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date parsed = iso.parse(o.getCreatedAt());
                            if (parsed != null) {
                                dateStr = displayDateFmt.format(parsed);
                                timeStr = displayTimeFmt.format(parsed);
                            }
                        } catch (Exception ignored) {
                            dateStr = o.getCreatedAt();
                        }
                    }

                    String totalStr = o.getTotalAmount() != null
                            ? String.format("%,dđ", o.getTotalAmount().longValue())
                            : "0đ";

                    int itemCount = o.getItemCount() != null ? o.getItemCount() : 0;
                    String customerName = o.getCustomerName() != null ? o.getCustomerName() : "Khách hàng";

                    Double dist = o.getDistance();

                    history.add(new SellerHistoryOrder(
                            o.getId(),
                            "#" + o.getId(),
                            customerName,
                            displayStatus,
                            timeStr,
                            itemCount,
                            dist != null ? String.format(Locale.getDefault(), "%.1f km", dist) : "—",
                            dateStr,
                            timeStr,
                            totalStr
                    ));
                    break;
            }
        }

        boolean hasNewOrder = false;
        if (!isFirstLoadDone) {
            for (SellerOrder newO : pending) {
                notifiedOrderIds.add(newO.getId());
            }
            isFirstLoadDone = true;
        } else {
            for (SellerOrder newO : pending) {
                if (!notifiedOrderIds.contains(newO.getId())) {
                    notifiedOrderIds.add(newO.getId());
                    hasNewOrder = true;
                }
            }
        }

        rawNewOrders.clear();
        rawNewOrders.addAll(pending);
        rawConfirmedOrders.clear();
        rawConfirmedOrders.addAll(preparing);
        rawHistoryOrders.clear();
        rawHistoryOrders.addAll(history);

        applySearchFilter();

        if (hasNewOrder) {
            newOrderEvent.setValue("NEW_ORDER_DETECTED");
            if (!SellerHomeActivity.isResumed) {
                showLocalNotification();
            }
        }
    }

    public void setSearchQuery(String query) {
        this.currentSearchQuery = query;
        applySearchFilter();
    }

    private void applySearchFilter() {
        String query = currentSearchQuery.trim().toLowerCase(java.util.Locale.getDefault());
        if (query.isEmpty()) {
            newOrders.setValue(new java.util.ArrayList<>(rawNewOrders));
            confirmedOrders.setValue(new java.util.ArrayList<>(rawConfirmedOrders));
            historyOrders.setValue(new java.util.ArrayList<>(rawHistoryOrders));
        } else {
            java.util.List<SellerOrder> filteredNew = new java.util.ArrayList<>();
            for (SellerOrder o : rawNewOrders) {
                if (o.getCustomerName() != null && o.getCustomerName().toLowerCase(java.util.Locale.getDefault()).contains(query)) {
                    filteredNew.add(o);
                }
            }

            java.util.List<SellerOrder> filteredConfirmed = new java.util.ArrayList<>();
            for (SellerOrder o : rawConfirmedOrders) {
                if (o.getCustomerName() != null && o.getCustomerName().toLowerCase(java.util.Locale.getDefault()).contains(query)) {
                    filteredConfirmed.add(o);
                }
            }

            java.util.List<SellerHistoryOrder> filteredHistory = new java.util.ArrayList<>();
            for (SellerHistoryOrder o : rawHistoryOrders) {
                if (o.getCustomerName() != null && o.getCustomerName().toLowerCase(java.util.Locale.getDefault()).contains(query)) {
                    filteredHistory.add(o);
                }
            }

            newOrders.setValue(filteredNew);
            confirmedOrders.setValue(filteredConfirmed);
            historyOrders.setValue(filteredHistory);
        }
    }

    /**
     * Map OrderResponse → SellerOrder (dùng cho tab PENDING / PREPARING).
     */
    private SellerOrder mapToSellerOrder(OrderResponse o) {
        List<OrderItem> dishes = new ArrayList<>();
        if (o.getItems() != null) {
            for (OrderResponse.OrderItemResponse item : o.getItems()) {
                long priceVal = item.getPrice() != null ? item.getPrice().longValue() : 0L;
                int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                // foodId được lưu qua name vì API hiện tại không trả trực tiếp foodId trong item
                // Dùng null để fallback sang UI-only remove (local)
                dishes.add(new OrderItem(null, qty, item.getName() != null ? item.getName() : "", priceVal, item.getSelectedOptions()));
            }
        }

        String totalStr = o.getTotalAmount() != null
                ? String.format("%,dđ", o.getTotalAmount().longValue())
                : "0đ";

        int itemCount = o.getItemCount() != null ? o.getItemCount() : (o.getItems() != null ? o.getItems().size() : 0);
        String customerName = o.getCustomerName() != null ? o.getCustomerName() : "Khách hàng";
        String customerPhone = o.getCustomerPhone() != null ? o.getCustomerPhone() : "";

        String createdAtStr = "Không rõ";
        if (o.getCreatedAt() != null && !o.getCreatedAt().isEmpty()) {
            try {
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                iso.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parsed = iso.parse(o.getCreatedAt());
                if (parsed != null) {
                    SimpleDateFormat displayFmt = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    displayFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                    createdAtStr = displayFmt.format(parsed);
                }
            } catch (Exception ignored) {
                createdAtStr = o.getCreatedAt();
            }
        }

        String pickupTimeStr = "Không rõ";
        if (o.getExpectedDeliveryTime() != null && !o.getExpectedDeliveryTime().isEmpty()) {
            try {
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                iso.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parsed = iso.parse(o.getExpectedDeliveryTime());
                if (parsed != null) {
                    SimpleDateFormat displayFmt = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    displayFmt.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                    pickupTimeStr = displayFmt.format(parsed);
                }
            } catch (Exception ignored) {
                pickupTimeStr = o.getExpectedDeliveryTime();
            }
        }

        long shippingFee = o.getShippingFee() != null ? o.getShippingFee().longValue() : 0L;
        long discountAmount = o.getDiscountAmount() != null ? o.getDiscountAmount().longValue() : 0L;

        Double distance = o.getDistance();

        return new SellerOrder(
                o.getId(),
                "#" + o.getId(),
                customerName,
                customerPhone,
                "",
                itemCount,
                totalStr,
                o.getStatus(),
                o.getNote() != null ? o.getNote() : "",
                createdAtStr,
                pickupTimeStr,
                shippingFee,
                discountAmount,
                distance,
                dishes
        );
    }

    private String mapStatusToVietnamese(String status) {
        switch (status) {
            case "DELIVERING": return "Đang giao";
            case "COMPLETED":  return "Đã giao";
            case "CANCELLED":  return "Đã hủy";
            default:           return status;
        }
    }

    // ===================== ACTIONS =====================

    /**
     * Xác nhận đơn hàng mới (PENDING → PREPARING) bằng cách gọi API merchant-ready.
     */
    public void acceptOrder(SellerOrder order) {
        if (order.getOrderId() == null) {
            // Fallback local (không có orderId từ API)
            moveLocalOrderToConfirmed(order);
            return;
        }

        isLoading.setValue(true);
        orderRepository.merchantReady(order.getOrderId(), new ApiCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                isLoading.setValue(false);
                successMessage.setValue("Đã xác nhận đơn hàng");
                loadData(); // Reload toàn bộ
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue("Xác nhận thất bại: " + message);
            }
        });
    }

    /**
     * Hủy đơn hàng bằng cách gọi API cancel.
     */
    public void cancelOrder(SellerOrder order, String reason) {
        if (order.getOrderId() == null) {
            // Fallback local
            moveLocalOrderToCancelled(order);
            return;
        }

        isLoading.setValue(true);
        String cancelReason = (reason != null && !reason.trim().isEmpty()) ? reason : "Không có lý do";
        orderRepository.cancelOrder(order.getOrderId(), new CancelOrderRequest(cancelReason),
                new ApiCallback<OrderResponse>() {
                    @Override
                    public void onSuccess(OrderResponse result) {
                        isLoading.setValue(false);
                        successMessage.setValue("Đã hủy đơn hàng");
                        loadData();
                    }

                    @Override
                    public void onError(String message) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Hủy đơn thất bại: " + message);
                    }
                });
    }

    /**
     * Xóa một món ăn khỏi đơn hàng. Nếu có foodId → gọi API. Nếu không → xóa local.
     */
    public void removeItemFromOrder(SellerOrder order, OrderItem itemToRemove, Runnable onDone) {
        // Nếu không có foodId (API chưa trả về), chỉ xóa local
        if (order.getOrderId() == null || itemToRemove.getFoodId() == null) {
            removeItemLocal(order, itemToRemove);
            if (onDone != null) onDone.run();
            return;
        }

        isLoading.setValue(true);
        orderRepository.removeItemFromOrder(order.getOrderId(),
                new RemoveItemRequest(itemToRemove.getFoodId()),
                new ApiCallback<OrderResponse>() {
                    @Override
                    public void onSuccess(OrderResponse result) {
                        isLoading.setValue(false);
                        // Cập nhật lại thông tin đơn trong list local
                        updateOrderFromResponse(order, result);
                        notifyOrderUpdated();
                        successMessage.setValue("Đã cập nhật đơn hàng");
                        if (onDone != null) onDone.run();
                    }

                    @Override
                    public void onError(String message) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Cập nhật thất bại: " + message);
                    }
                });
    }

    /**
     * Chuyển đơn đã xác nhận sang lịch sử (giao đơn). Dùng local fallback.
     */
    public void moveToHistory(SellerHistoryOrder historyOrder, SellerOrder originalOrder) {
        List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
        currentConfirmed.remove(originalOrder);
        confirmedOrders.setValue(currentConfirmed);

        List<SellerHistoryOrder> currentHistory = new ArrayList<>(historyOrders.getValue());
        currentHistory.add(0, historyOrder);
        historyOrders.setValue(currentHistory);
    }
    public void deliverOrder(SellerOrder order) {
        if (order.getOrderId() == null) {
            // Fallback local
            moveLocalOrderToDelivering(order);
            return;
        }

        isLoading.setValue(true);
        orderRepository.merchantDeliverOrder(order.getOrderId(), new ApiCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                isLoading.setValue(false);
                successMessage.setValue("Đã bàn giao cho Shipper");
                loadData(); // Reload toàn bộ
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue("Bàn giao thất bại: " + message);
            }
        });
    }

    private void moveLocalOrderToDelivering(SellerOrder order) {
        List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
        currentConfirmed.remove(order);
        
        order.setStatus("DELIVERING");
        currentConfirmed.add(0, order);
        confirmedOrders.setValue(currentConfirmed);
    }

    public void completeOrder(SellerOrder order) {
        if (order.getOrderId() == null) {
            // Không có orderId, fallback local
            moveLocalOrderToCancelled(order);
            return;
        }

        isLoading.setValue(true);
        orderRepository.merchantCompleteOrder(order.getOrderId(), new ApiCallback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse result) {
                isLoading.setValue(false);
                successMessage.setValue("Đơn hàng đã hoàn thành!");
                loadData(); // Reload toàn bộ từ server
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue("Hoàn thành đơn thất bại: " + message);
            }
        });
    }

    public void notifyOrderUpdated() {
        newOrders.setValue(new ArrayList<>(newOrders.getValue()));
        confirmedOrders.setValue(new ArrayList<>(confirmedOrders.getValue()));
    }

    // ===================== LOCAL HELPERS =====================

    private void moveLocalOrderToConfirmed(SellerOrder order) {
        List<SellerOrder> currentNew = new ArrayList<>(newOrders.getValue());
        currentNew.remove(order);
        newOrders.setValue(currentNew);

        order.setStatus("PREPARING");
        List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
        currentConfirmed.add(0, order);
        confirmedOrders.setValue(currentConfirmed);
    }

    private void moveLocalOrderToCancelled(SellerOrder order) {
        List<SellerOrder> currentNew = new ArrayList<>(newOrders.getValue());
        boolean removed = currentNew.remove(order);
        if (removed) {
            newOrders.setValue(currentNew);
        } else {
            List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
            currentConfirmed.remove(order);
            confirmedOrders.setValue(currentConfirmed);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String now = stf.format(new Date());
        String today = sdf.format(new Date());

        List<SellerHistoryOrder> currentHistory = new ArrayList<>(historyOrders.getValue());
        currentHistory.add(0, new SellerHistoryOrder(
                order.getOrderId(), order.getId(), order.getCustomerName(),
                "Đã hủy", now, order.getNumberOfDishes(), "—", today, now, order.getTotalPrice()
        ));
        historyOrders.setValue(currentHistory);
    }

    private void removeItemLocal(SellerOrder order, OrderItem itemToRemove) {
        List<OrderItem> dishes = order.getDishes();
        if (dishes != null) {
            dishes.remove(itemToRemove);
            long newTotal = 0;
            for (OrderItem oi : dishes) {
                newTotal += (long) oi.getPrice() * oi.getQuantity();
            }
            order.setTotalPrice(String.format("%,dđ", newTotal));
            order.setNumberOfDishes(dishes.size());
        }
    }

    private void updateOrderFromResponse(SellerOrder order, OrderResponse response) {
        if (response.getTotalAmount() != null) {
            order.setTotalPrice(String.format("%,dđ", response.getTotalAmount().longValue()));
        }
        if (response.getItemCount() != null) {
            order.setNumberOfDishes(response.getItemCount());
        }
        // Sync lại danh sách món từ API response
        if (response.getItems() != null) {
            List<OrderItem> updatedDishes = new ArrayList<>();
            for (OrderResponse.OrderItemResponse item : response.getItems()) {
                long priceVal = item.getPrice() != null ? item.getPrice().longValue() : 0L;
                int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                updatedDishes.add(new OrderItem(null, qty, item.getName() != null ? item.getName() : "", priceVal, item.getSelectedOptions()));
            }
            order.getDishes().clear();
            order.getDishes().addAll(updatedDishes);
        }
    }

    private void reconnectWebSocket(long restaurantId) {
        if (mStompClient == null) return; // Đã disconnect chủ động
        
        reconnectHandler.removeCallbacksAndMessages(null);
        reconnectHandler.postDelayed(() -> {
            if (mStompClient != null && !mStompClient.isConnected()) {
                Log.d("WebSocket", "Attempting to reconnect...");
                initWebSocket(restaurantId);
            }
        }, 5000); // Thử lại sau 5 giây
    }

    @SuppressLint("CheckResult")
    private void initWebSocket(long restaurantId) {
        if (mStompClient != null && mStompClient.isConnected()) {
            mStompClient.disconnect();
        }
        connectedStoreId = restaurantId;
        
        String baseUrl = com.example.uitpayapp.network.RetrofitClient.getBaseUrl();
        String wsUrl = baseUrl;
        if (wsUrl.startsWith("http://")) {
            wsUrl = "ws://" + wsUrl.substring(7);
        } else if (wsUrl.startsWith("https://")) {
            wsUrl = "wss://" + wsUrl.substring(8);
        }
        if (!wsUrl.endsWith("/")) {
            wsUrl += "/";
        }
        // URL chỉ đến /ws/chat — NaikSoftware StompProtocolAndroid tự append
        // SockJS path {server-id}/{session-id}/websocket nội bộ
        wsUrl += "ws/chat";
        
        // Thêm Authorization header vào HTTP WebSocket handshake request
        String authToken = SessionManager.getInstance(getApplication()).getAuthToken();
        Map<String, String> connectHttpHeaders = new HashMap<>();
        if (authToken != null && !authToken.isEmpty()) {
            connectHttpHeaders.put("Authorization", "Bearer " + authToken);
        }
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl, connectHttpHeaders);

        mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("WebSocket", "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e("WebSocket", "Error", lifecycleEvent.getException());
                            reconnectWebSocket(restaurantId);
                            break;
                        case CLOSED:
                            Log.d("WebSocket", "Stomp connection closed");
                            break;
                    }
                });

        mStompClient.topic("/topic/restaurant/" + restaurantId + "/orders")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("WebSocket", "Received message: " + topicMessage.getPayload());
                    
                    // Delay 1000ms trước khi loadData để đảm bảo transaction trên server đã được commit vào DB
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        loadData();
                    }, 1000);
                    
                    if (topicMessage.getPayload() != null && topicMessage.getPayload().startsWith("NEW_ORDER_")) {
                        newOrderEvent.postValue(topicMessage.getPayload());
                    }
                }, throwable -> {
                    Log.e("WebSocket", "Error on subscribe topic", throwable);
                });

        // Gửi Authorization token trong STOMP CONNECT frame headers
        List<StompHeader> stompConnectHeaders = new ArrayList<>();
        if (authToken != null && !authToken.isEmpty()) {
            stompConnectHeaders.add(new StompHeader("Authorization", "Bearer " + authToken));
        }
        mStompClient.connect(stompConnectHeaders);
    }

    public void disconnectWebSocket() {
        if (reconnectHandler != null) {
            reconnectHandler.removeCallbacksAndMessages(null);
        }
        if (mStompClient != null) {
            mStompClient.disconnect();
            mStompClient = null;
        }
        connectedStoreId = -1L;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopPolling();
        disconnectWebSocket();
    }

    private void showLocalNotification() {
        android.content.Context context = getApplication();
        String channelId = "YumYum_Seller_Order_Channel";
        String channelName = "YumYum Seller Orders";
        
        android.app.NotificationManager notificationManager = 
                (android.app.NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                
        if (notificationManager == null) return;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new android.app.NotificationChannel(
                        channelId,
                        channelName,
                        android.app.NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Thông báo đơn hàng mới cho Merchant");
                channel.enableLights(true);
                channel.setLightColor(android.graphics.Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 500, 200, 500});
                notificationManager.createNotificationChannel(channel);
            }
        }
        
        android.content.Intent intent = new android.content.Intent(context, SellerHomeActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        int flags = android.app.PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            flags |= android.app.PendingIntent.FLAG_IMMUTABLE;
        }
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                context,
                0,
                intent,
                flags
        );
        
        android.net.Uri defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION);
        
        androidx.core.app.NotificationCompat.Builder notificationBuilder =
                new androidx.core.app.NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(com.example.uitpayapp.R.drawable.notifications_24px)
                        .setContentTitle("YumYum Merchant")
                        .setContentText("Bạn có đơn hàng mới")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[]{0, 500, 200, 500})
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);
                        
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}
