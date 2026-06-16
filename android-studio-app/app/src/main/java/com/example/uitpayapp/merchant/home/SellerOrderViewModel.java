package com.example.uitpayapp.merchant.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uitpayapp.merchant.home.home_model.OrderItem;
import com.example.uitpayapp.merchant.home.home_model.SellerHistoryOrder;
import com.example.uitpayapp.merchant.home.home_model.SellerOrder;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderViewModel extends ViewModel {

    private final MutableLiveData<List<SellerOrder>> newOrders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<SellerOrder>> confirmedOrders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<SellerHistoryOrder>> historyOrders = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<SellerOrder>> getNewOrders() { return newOrders; }
    public LiveData<List<SellerOrder>> getConfirmedOrders() { return confirmedOrders; }
    public LiveData<List<SellerHistoryOrder>> getHistoryOrders() { return historyOrders; }

    public void loadData() {
        if (!newOrders.getValue().isEmpty() || !confirmedOrders.getValue().isEmpty()) return;

        List<SellerOrder> news = new ArrayList<>();
        List<OrderItem> dishes1 = new ArrayList<>();
        dishes1.add(new OrderItem(1, "Lý Trà Chanh", 35000));
        dishes1.add(new OrderItem(1, "Trà Sữa Phúc Bồn Tử", 35000));
        news.add(new SellerOrder("#20240001", "Nguyễn Văn A", "", 2, "70.000đ", "new", "Vui lòng chuẩn bị món ăn nhanh hơn nữa", dishes1));
        
        List<OrderItem> dishes2 = new ArrayList<>();
        dishes2.add(new OrderItem(1, "Bún Đậu Mắm Tôm Đặc Biệt", 80000));
        news.add(new SellerOrder("#20240002", "Trần Thị B", "", 1, "80.000đ", "new", "Giao hàng cẩn thận", dishes2));
        newOrders.setValue(news);

        List<SellerOrder> confirmed = new ArrayList<>();
        List<OrderItem> dishes3 = new ArrayList<>();
        dishes3.add(new OrderItem(1, "Trà Đào Cam Sả", 45000));
        confirmed.add(new SellerOrder("#20240003", "Lê Văn C", "", 1, "45.000đ", "confirmed", "Không đường", dishes3));
        confirmedOrders.setValue(confirmed);

        List<SellerHistoryOrder> history = new ArrayList<>();
        history.add(new SellerHistoryOrder("#20240004", "Phạm Văn D", "Đã giao", "17:46", 1, "0.1 km", "25/04/2024", "18:18", "46.500đ"));
        historyOrders.setValue(history);
    }

    public void notifyOrderUpdated() {
        newOrders.setValue(new ArrayList<>(newOrders.getValue()));
        confirmedOrders.setValue(new ArrayList<>(confirmedOrders.getValue()));
    }

    public void acceptOrder(SellerOrder order) {
        List<SellerOrder> currentNew = new ArrayList<>(newOrders.getValue());
        currentNew.remove(order);
        newOrders.setValue(currentNew);

        List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
        order.setStatus("confirmed");
        currentConfirmed.add(0, order);
        confirmedOrders.setValue(currentConfirmed);
    }

    public void moveToHistory(SellerHistoryOrder historyOrder, SellerOrder originalOrder) {
        List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
        currentConfirmed.remove(originalOrder);
        confirmedOrders.setValue(currentConfirmed);

        List<SellerHistoryOrder> currentHistory = new ArrayList<>(historyOrders.getValue());
        currentHistory.add(0, historyOrder);
        historyOrders.setValue(currentHistory);
    }
    
    public void cancelOrder(SellerHistoryOrder historyOrder, SellerOrder originalOrder) {
        List<SellerOrder> currentNew = new ArrayList<>(newOrders.getValue());
        if (currentNew.remove(originalOrder)) {
            newOrders.setValue(currentNew);
        } else {
            List<SellerOrder> currentConfirmed = new ArrayList<>(confirmedOrders.getValue());
            currentConfirmed.remove(originalOrder);
            confirmedOrders.setValue(currentConfirmed);
        }

        List<SellerHistoryOrder> currentHistory = new ArrayList<>(historyOrders.getValue());
        currentHistory.add(0, historyOrder);
        historyOrders.setValue(currentHistory);
    }
}
