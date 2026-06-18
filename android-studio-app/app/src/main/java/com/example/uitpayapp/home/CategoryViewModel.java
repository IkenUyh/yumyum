package com.example.uitpayapp.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.network.CategoryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final CategoryRepository repository;
    private final MutableLiveData<UiState<List<FoodMenuItem>>> foodsData = new MutableLiveData<>();

    // Cache dữ liệu gốc từ API, dùng để sắp xếp cục bộ cho các tab
    private List<FoodMenuItem> originalFoods = new ArrayList<>();

    public CategoryViewModel() {
        this.repository = CategoryRepository.getInstance();
    }

    public LiveData<UiState<List<FoodMenuItem>>> getFoodsData() {
        return foodsData;
    }

    /**
     * Gọi API lấy danh sách món ăn theo category ID.
     * Sau khi fetch xong, sẽ cache lại và áp dụng bộ lọc hiện tại.
     */
    public void fetchFoodsByCategory(Long categoryId) {
        foodsData.setValue(UiState.loading(null));
        repository.getFoodsByCategory(categoryId, new CategoryRepository.CategoryFoodsCallback() {
            @Override
            public void onSuccess(List<FoodMenuItem> foods) {
                originalFoods = new ArrayList<>(foods);
                foodsData.setValue(UiState.success(new ArrayList<>(foods)));
            }

            @Override
            public void onEmpty() {
                originalFoods.clear();
                foodsData.setValue(UiState.empty());
            }

            @Override
            public void onError(String message) {
                originalFoods.clear();
                foodsData.setValue(UiState.error(message, null));
            }
        });
    }

    /**
     * Sắp xếp lại danh sách theo filter type dựa trên cache cục bộ.
     * - "Gần tôi": giữ nguyên thứ tự gốc (server trả về)
     * - "Bán chạy": shuffle (giả lập, vì chưa có dữ liệu lượt bán)
     * - "Đánh giá": shuffle (giả lập, vì chưa có dữ liệu rating)
     */
    public void applyFilter(String filterType) {
        if (originalFoods.isEmpty()) return;

        List<FoodMenuItem> sorted = new ArrayList<>(originalFoods);
        if ("Bán chạy".equals(filterType) || "Đánh giá".equals(filterType)) {
            Collections.shuffle(sorted);
        }
        // "Gần tôi" giữ nguyên thứ tự gốc
        foodsData.setValue(UiState.success(sorted));
    }

    /**
     * Kiểm tra đã có dữ liệu cache chưa (để tránh gọi lại API khi chuyển tab)
     */
    public boolean hasData() {
        return !originalFoods.isEmpty();
    }
}
