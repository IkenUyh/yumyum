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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SellerOrderViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    // Đơn mới (PENDING)
    private final MutableLiveData<List<SellerOrder>> newOrders = new MutableLiveData<>(new ArrayList<>());
    // Đã xác nhận / Đang chuẩn bị (PREPARING)
    private final MutableLiveData<List<SellerOrder>> confirmedOrders = new MutableLiveData<>(new ArrayList<>());
    // Lịch sử (DELIVERING, COMPLETED, CANCELLED)
    private final MutableLiveData<List<SellerHistoryOrder>> historyOrders = new MutableLiveData<>(new ArrayList<>());

    // UI feedback
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

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

    /**
     * Tải toàn bộ đơn hàng của quán từ API, phân loại theo trạng thái.
     */
    public void loadData() {
        isLoading.setValue(true);
        orderRepository.getMerchantHistory(new ApiCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> result) {
                isLoading.setValue(false);
                classifyOrders(result);
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
        SimpleDateFormat displayTimeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (OrderResponse o : orders) {
            String status = o.getStatus() != null ? o.getStatus().toUpperCase() : "";

            switch (status) {
                case "PENDING":
                case "PREPARING":
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

                    history.add(new SellerHistoryOrder(
                            o.getId(),
                            "#" + o.getId(),
                            customerName,
                            displayStatus,
                            timeStr,
                            itemCount,
                            "—",
                            dateStr,
                            timeStr,
                            totalStr
                    ));
                    break;
            }
        }

        newOrders.setValue(pending);
        confirmedOrders.setValue(preparing);
        historyOrders.setValue(history);
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
                dishes.add(new OrderItem(qty, item.getName() != null ? item.getName() : "", priceVal));
            }
        }

        String totalStr = o.getTotalAmount() != null
                ? String.format("%,dđ", o.getTotalAmount().longValue())
                : "0đ";

        int itemCount = o.getItemCount() != null ? o.getItemCount() : (o.getItems() != null ? o.getItems().size() : 0);
        String customerName = o.getCustomerName() != null ? o.getCustomerName() : "Khách hàng";
        String customerPhone = o.getCustomerPhone() != null ? o.getCustomerPhone() : "";

        return new SellerOrder(
                o.getId(),
                "#" + o.getId(),
                customerName,
                customerPhone,
                "",
                itemCount,
                totalStr,
                o.getStatus(),
                "",
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
     * Chuyển đơn đã xác nhận sang lịch sử (giao đơn).
     */
    public void moveToHistory(SellerHistoryOrder historyOrder, SellerOrder originalOrder) {
        List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
        currentConfirmed.remove(originalOrder);
        confirmedOrders.setValue(currentConfirmed);

        List<SellerHistoryOrder> currentHistory = new ArrayList<>(historyOrders.getValue());
        currentHistory.add(0, historyOrder);
        historyOrders.setValue(currentHistory);
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
                updatedDishes.add(new OrderItem(qty, item.getName() != null ? item.getName() : "", priceVal));
            }
            order.getDishes().clear();
            order.getDishes().addAll(updatedDishes);
        }
    }
}
