package com.example.uitpayapp.merchant.shop.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uitpayapp.home.UiState;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.SessionManager;
import com.example.uitpayapp.modules.food.CategoryRepository;
import com.example.uitpayapp.modules.food.FoodRepository;
import com.example.uitpayapp.modules.food.models.requests.CreateCategoryRequest;
import com.example.uitpayapp.modules.food.models.requests.CreateFoodRequest;
import com.example.uitpayapp.modules.food.models.requests.CreateOptionGroupRequest;
import com.example.uitpayapp.modules.food.models.requests.CreateOptionItemRequest;
import com.example.uitpayapp.modules.food.models.responses.CategoryResponse;
import com.example.uitpayapp.modules.food.models.responses.FoodResponse;
import com.example.uitpayapp.modules.food.models.responses.OptionGroupResponse;
import com.example.uitpayapp.modules.food.models.responses.OptionItemResponse;
import com.example.uitpayapp.modules.restaurant.RestaurantRepository;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuCategory;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;
import com.example.uitpayapp.merchant.shop.shop_model.ToppingGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerchantMenuViewModel extends ViewModel {

    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<UiState<List<MerchantMenuCategory>>> dishCategories = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<ToppingGroup>>> toppingGroups = new MutableLiveData<>();
    private final MutableLiveData<Long> restaurantId = new MutableLiveData<>();
    private final MutableLiveData<UiState<String>> operationStatus = new MutableLiveData<>();
    private final MutableLiveData<UiState<FoodResponse>> foodOperationSuccess = new MutableLiveData<>();

    public MerchantMenuViewModel() {
        this.categoryRepository = new CategoryRepository();
        this.foodRepository = new FoodRepository();
        this.restaurantRepository = new RestaurantRepository();
    }

    public LiveData<UiState<List<MerchantMenuCategory>>> getDishCategories() {
        return dishCategories;
    }

    public LiveData<UiState<List<ToppingGroup>>> getToppingGroups() {
        return toppingGroups;
    }

    public LiveData<Long> getRestaurantId() {
        return restaurantId;
    }

    public LiveData<UiState<String>> getOperationStatus() {
        return operationStatus;
    }

    public LiveData<UiState<FoodResponse>> getFoodOperationSuccess() {
        return foodOperationSuccess;
    }

    /**
     * Khởi tạo và tải ID nhà hàng của chủ quán đang đăng nhập
     */
    public void initializeRestaurant(Context context, Runnable onInitialized) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("SellerPrefs", Context.MODE_PRIVATE);
        long currentStoreId = prefs.getLong("current_store_id", -1L);
        if (currentStoreId != -1L) {
            restaurantId.setValue(currentStoreId);
            if (onInitialized != null) {
                onInitialized.run();
            }
            return;
        }

        SessionManager sessionManager = SessionManager.getInstance(context);
        Long merchantId = sessionManager.getUserId();
        if (merchantId == null || merchantId == -1L) {
            dishCategories.setValue(UiState.error("Không tìm thấy phiên đăng nhập chủ quán!", null));
            return;
        }

        restaurantRepository.getAllRestaurants(new ApiCallback<List<RestaurantResponseDTO>>() {
            @Override
            public void onSuccess(List<RestaurantResponseDTO> data) {
                if (data == null || data.isEmpty()) {
                    dishCategories.setValue(UiState.error("Không tìm thấy quán ăn nào!", null));
                    return;
                }

                Long resId = null;
                for (RestaurantResponseDTO r : data) {
                    if (r.getMerchantId() != null && r.getMerchantId().equals(merchantId)) {
                        resId = r.getId();
                        break;
                    }
                }

                if (resId == null) {
                    dishCategories.setValue(UiState.error("Tài khoản chưa đăng ký quán ăn!", null));
                    return;
                }

                restaurantId.setValue(resId);
                if (onInitialized != null) {
                    onInitialized.run();
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Fallback giả lập (Mock) ID cửa hàng là 2 khi API lỗi
                restaurantId.setValue(2L);
                if (onInitialized != null) {
                    onInitialized.run();
                }
            }
        });
    }

    //Tải toàn bộ menu gồm các danh mục món ăn (Dish Categories) và nhóm Topping (Topping Groups)
    public void loadMenu() {
        Long resId = restaurantId.getValue();
        if (resId == null) {
            dishCategories.setValue(UiState.error("Chưa chọn nhà hàng!", null));
            return;
        }

        dishCategories.setValue(UiState.loading(null));
        toppingGroups.setValue(UiState.loading(null));

        // 1. Tải danh mục món ăn THEO nhà hàng (chỉ category có món của quán này)
        categoryRepository.getCategoriesByRestaurant(resId, new ApiCallback<List<CategoryResponse>>() {
            @Override
            public void onSuccess(List<CategoryResponse> categories) {
                // 2. Tải toàn bộ món ăn của quán
                foodRepository.getRestaurantMenuForMerchant(resId, new ApiCallback<List<FoodResponse>>() {
                    @Override
                    public void onSuccess(List<FoodResponse> foods) {
                        processMenuData(categories, foods);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        dishCategories.setValue(UiState.error("Không tải được thực đơn: " + errorMessage, null));
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                dishCategories.setValue(UiState.error("Không tải được danh mục: " + errorMessage, null));
            }
        });
    }

    private void processMenuData(List<CategoryResponse> categories, List<FoodResponse> foods) {
        // Gom nhóm món ăn theo categoryId
        Map<Long, List<MerchantMenuItem>> categoryItemsMap = new HashMap<>();
        for (FoodResponse food : foods) {
            Long catId = food.getCategoryId();
            if (catId == null) catId = -1L; // Nhóm không xác định

            List<MerchantMenuItem> list = categoryItemsMap.get(catId);
            if (list == null) {
                list = new ArrayList<>();
                categoryItemsMap.put(catId, list);
            }

            MerchantMenuItem uiItem = new MerchantMenuItem(
                    food.getId(),
                    food.getCategoryId(),
                    food.getName(),
                    food.getPrice() != null ? food.getPrice().doubleValue() : 0.0,
                    0, // Sử dụng URL thay vì drawable resource
                    Boolean.TRUE.equals(food.getIsAvailable()),
                    food.getDescription(),
                    food.getImageUrl()
            );
            list.add(uiItem);
        }

        // Tạo danh sách MerchantMenuCategory
        List<MerchantMenuCategory> uiCategories = new ArrayList<>();
        for (CategoryResponse cat : categories) {
            List<MerchantMenuItem> items = categoryItemsMap.get(cat.getId());
            if (items == null) items = new ArrayList<>();
            uiCategories.add(new MerchantMenuCategory(cat.getId(), cat.getName(), items));
        }

        // Thêm nhóm "Khác" nếu có món ăn không thuộc danh mục nào
        List<MerchantMenuItem> uncategorized = categoryItemsMap.get(-1L);
        if (uncategorized != null && !uncategorized.isEmpty()) {
            uiCategories.add(new MerchantMenuCategory(-1L, "Món khác", uncategorized));
        }

        dishCategories.setValue(UiState.success(uiCategories));

        // Tải topping groups của các món ăn
        loadToppingGroupsFromFoods(foods);
    }

    private void loadToppingGroupsFromFoods(List<FoodResponse> foods) {
        if (foods.isEmpty()) {
            toppingGroups.setValue(UiState.success(new ArrayList<>()));
            return;
        }

        // Dùng LinkedHashMap để dedup nhóm option theo groupId (giữ thứ tự chèn)
        // và AtomicInteger để đếm request hoàn thành (gọi song song an toàn)
        final Map<Long, ToppingGroup> groupMap = new java.util.LinkedHashMap<>();
        final java.util.concurrent.atomic.AtomicInteger pendingCount =
                new java.util.concurrent.atomic.AtomicInteger(foods.size());

        for (FoodResponse food : foods) {
            foodRepository.getFoodOptions(food.getId(), new ApiCallback<List<OptionGroupResponse>>() {
                @Override
                public void onSuccess(List<OptionGroupResponse> groups) {
                    if (groups != null) {
                        synchronized (groupMap) {
                            for (OptionGroupResponse g : groups) {
                                // Chỉ thêm vào map nếu groupId chưa có → dedup
                                if (!groupMap.containsKey(g.getId())) {
                                    List<MerchantMenuItem> items = new ArrayList<>();
                                    if (g.getItems() != null) {
                                        for (OptionItemResponse item : g.getItems()) {
                                            items.add(new MerchantMenuItem(
                                                    item.getId(),
                                                    g.getId(),
                                                    item.getName(),
                                                    item.getAdditionalPrice() != null
                                                            ? item.getAdditionalPrice().doubleValue() : 0.0,
                                                    0,
                                                    Boolean.TRUE.equals(item.getIsAvailable()),
                                                    "",
                                                    ""
                                            ));
                                        }
                                    }
                                    groupMap.put(g.getId(), new ToppingGroup(
                                            g.getId(),
                                            g.getName(),
                                            items,
                                            Boolean.TRUE.equals(g.getIsRequired()),
                                            g.getMaxChoices() != null ? g.getMaxChoices() : 1
                                    ));
                                }
                            }
                        }
                    }
                    // Khi tất cả request hoàn thành → emit kết quả
                    if (pendingCount.decrementAndGet() == 0) {
                        toppingGroups.postValue(UiState.success(new ArrayList<>(groupMap.values())));
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // Vẫn đếm xuống để không bị treo, group của món này bỏ qua
                    if (pendingCount.decrementAndGet() == 0) {
                        toppingGroups.postValue(UiState.success(new ArrayList<>(groupMap.values())));
                    }
                }
            });
        }
    }

    // ==========================================
    // DƯỚI ĐÂY LÀ CÁC LOGIC THAO TÁC API (CRUD)
    // ==========================================

    /**
     * Thêm danh mục món ăn mới
     */
    public void createCategory(String name) {
        Long resId = restaurantId.getValue();
        if (resId == null) {
            operationStatus.setValue(UiState.error("Chưa chọn nhà hàng!", null));
            return;
        }

        operationStatus.setValue(UiState.loading(null));
        CreateCategoryRequest request = new CreateCategoryRequest(name, resId);
        categoryRepository.createCategory(request, new ApiCallback<CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse result) {
                operationStatus.setValue(UiState.success("Đã thêm danh mục thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Thêm danh mục thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Chỉnh sửa danh mục món ăn
     */
    public void updateCategory(Long categoryId, String name) {
        Long resId = restaurantId.getValue();
        if (resId == null) {
            operationStatus.setValue(UiState.error("Chưa chọn nhà hàng!", null));
            return;
        }

        operationStatus.setValue(UiState.loading(null));
        CreateCategoryRequest request = new CreateCategoryRequest(name, resId);
        categoryRepository.updateCategory(categoryId, request, new ApiCallback<CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse result) {
                operationStatus.setValue(UiState.success("Đã cập nhật danh mục thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Cập nhật danh mục thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Thêm món ăn mới
     */
    public void createFood(String name, String description, double price, Long categoryId) {
        Long resId = restaurantId.getValue();
        if (resId == null) {
            foodOperationSuccess.setValue(UiState.error("Chưa chọn nhà hàng!", null));
            return;
        }

        foodOperationSuccess.setValue(UiState.loading(null));
        CreateFoodRequest request = new CreateFoodRequest(resId, categoryId, name, description, BigDecimal.valueOf(price));
        foodRepository.createFood(request, new ApiCallback<FoodResponse>() {
            @Override
            public void onSuccess(FoodResponse result) {
                loadMenu();
                foodOperationSuccess.setValue(UiState.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                foodOperationSuccess.setValue(UiState.error("Lưu món ăn thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Chỉnh sửa thông tin món ăn
     */
    public void updateFood(Long foodId, String name, String description, double price, Long categoryId) {
        Long resId = restaurantId.getValue();
        if (resId == null) {
            foodOperationSuccess.setValue(UiState.error("Chưa chọn nhà hàng!", null));
            return;
        }

        foodOperationSuccess.setValue(UiState.loading(null));
        CreateFoodRequest request = new CreateFoodRequest(resId, categoryId, name, description, BigDecimal.valueOf(price));
        foodRepository.updateFood(foodId, request, new ApiCallback<FoodResponse>() {
            @Override
            public void onSuccess(FoodResponse result) {
                loadMenu();
                foodOperationSuccess.setValue(UiState.success(result));
            }

            @Override
            public void onError(String errorMessage) {
                foodOperationSuccess.setValue(UiState.error("Cập nhật món ăn thất bại: " + errorMessage, null));
            }
        });
    }

    public void uploadFoodImage(Long foodId, java.io.File file) {
        if (file == null || !file.exists()) {
            foodOperationSuccess.setValue(UiState.error("Tệp ảnh không hợp lệ", null));
            return;
        }

        foodOperationSuccess.setValue(UiState.loading(null));
        okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
        okhttp3.MultipartBody.Part filePart = okhttp3.MultipartBody.Part.createFormData("foodFile", file.getName(), fileBody);

        foodRepository.uploadFoodImage(foodId, filePart, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                loadMenu();
                FoodResponse mockResponse = new FoodResponse();
                mockResponse.setId(foodId);
                mockResponse.setImageUrl(result);
                foodOperationSuccess.setValue(UiState.success(mockResponse));
            }

            @Override
            public void onError(String errorMessage) {
                foodOperationSuccess.setValue(UiState.error("Tải ảnh lên thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Ngưng bán món ăn (xóa mềm)
     */
    public void deleteFood(Long foodId) {
        operationStatus.setValue(UiState.loading(null));
        foodRepository.deleteFood(foodId, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                operationStatus.setValue(UiState.success("Đã ngưng bán món ăn thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Ngưng bán món ăn thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Bật/Tắt trạng thái bán của món ăn
     */
    public void updateFoodStatus(Long foodId, boolean isAvailable) {
        operationStatus.setValue(UiState.loading(null));
        foodRepository.updateFoodStatus(foodId, isAvailable, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                operationStatus.setValue(UiState.success(result));
                // Có thể reload lại menu hoặc để adapter tự xử lý UI (vì switch đã bật/tắt ở UI rồi)
                // loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Cập nhật trạng thái thất bại: " + errorMessage, null));
                // Nếu lỗi, nên reload menu để switch quay lại trạng thái đúng
                loadMenu();
            }
        });
    }

    /**
     * Thêm nhóm topping mới cho một món ăn
     */
    public void addToppingGroup(Long foodId, String name, boolean isRequired, int maxChoices, List<CreateOptionItemRequest> items) {
        operationStatus.setValue(UiState.loading(null));
        CreateOptionGroupRequest request = new CreateOptionGroupRequest(name, isRequired, maxChoices, items);
        foodRepository.addOptionGroup(foodId, request, new ApiCallback<OptionGroupResponse>() {
            @Override
            public void onSuccess(OptionGroupResponse result) {
                operationStatus.setValue(UiState.success("Đã lưu nhóm topping thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Lưu nhóm topping thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Chỉnh sửa nhóm topping
     */
    public void updateToppingGroup(Long groupId, String name, boolean isRequired, int maxChoices) {
        operationStatus.setValue(UiState.loading(null));
        CreateOptionGroupRequest request = new CreateOptionGroupRequest(name, isRequired, maxChoices, null);
        foodRepository.updateOptionGroup(groupId, request, new ApiCallback<OptionGroupResponse>() {
            @Override
            public void onSuccess(OptionGroupResponse result) {
                operationStatus.setValue(UiState.success("Đã cập nhật nhóm topping thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Cập nhật nhóm topping thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Xóa nhóm topping
     */
    public void deleteToppingGroup(Long groupId) {
        operationStatus.setValue(UiState.loading(null));
        foodRepository.deleteOptionGroup(groupId, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                operationStatus.setValue(UiState.success("Đã xóa nhóm topping thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Xóa nhóm topping thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Thêm topping mới vào nhóm
     */
    public void addToppingToGroup(Long groupId, String name, double price) {
        operationStatus.setValue(UiState.loading(null));
        CreateOptionItemRequest request = new CreateOptionItemRequest(name, BigDecimal.valueOf(price));
        foodRepository.addOptionItem(groupId, request, new ApiCallback<OptionItemResponse>() {
            @Override
            public void onSuccess(OptionItemResponse result) {
                operationStatus.setValue(UiState.success("Đã thêm topping thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Thêm topping thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Chỉnh sửa topping
     */
    public void updateTopping(Long itemId, String name, double price) {
        operationStatus.setValue(UiState.loading(null));
        CreateOptionItemRequest request = new CreateOptionItemRequest(name, BigDecimal.valueOf(price));
        foodRepository.updateOptionItem(itemId, request, new ApiCallback<OptionItemResponse>() {
            @Override
            public void onSuccess(OptionItemResponse result) {
                operationStatus.setValue(UiState.success("Đã cập nhật topping thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Cập nhật topping thất bại: " + errorMessage, null));
            }
        });
    }

    /**
     * Xóa topping
     */
    public void deleteTopping(Long itemId) {
        operationStatus.setValue(UiState.loading(null));
        foodRepository.deleteOptionItem(itemId, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                operationStatus.setValue(UiState.success("Đã xóa topping thành công!"));
                loadMenu();
            }

            @Override
            public void onError(String errorMessage) {
                operationStatus.setValue(UiState.error("Xóa topping thất bại: " + errorMessage, null));
            }
        });
    }
}
