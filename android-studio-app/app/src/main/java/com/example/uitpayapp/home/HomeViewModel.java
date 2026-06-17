package com.example.uitpayapp.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.uitpayapp.home.network.BrandResponse;
import com.example.uitpayapp.home.network.DealResponse;
import com.example.uitpayapp.home.network.HomeApiService;
import com.example.uitpayapp.home.network.HomeCoreResponse;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.recommendeddeal.RecommendedDealModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final HomeApiService apiService;

    private final MutableLiveData<UiState<HomeCoreResponse>> coreData = new MutableLiveData<>();
    private final MutableLiveData<UiState<BrandResponse>> brandsData = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<RecommendedDealModel>>> dealsData = new MutableLiveData<>();

    private int currentDealsPage = 1;
    private int currentTabId = 0;
    private String currentAddressId = "";
    private boolean isDealsLoading = false;
    private boolean hasMoreDeals = true;
    private List<RecommendedDealModel> accumulatedDeals = new ArrayList<>();

    public HomeViewModel() {
        this.apiService = RetrofitClient.getHomeApiService();
    }

    public LiveData<UiState<HomeCoreResponse>> getCoreData() { return coreData; }
    public LiveData<UiState<BrandResponse>> getBrandsData() { return brandsData; }
    public LiveData<UiState<List<RecommendedDealModel>>> getDealsData() { return dealsData; }

    public void setAddressAndRefresh(String addressId) {
        this.currentAddressId = addressId;
        refreshAll();
    }

    public void refreshAll() {
        fetchCoreData();
        fetchBrandsData();
        resetAndFetchDeals(currentTabId);
    }

    private void fetchCoreData() {
        coreData.setValue(UiState.loading(null));
        apiService.getHomeCore(currentAddressId).enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<HomeCoreResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<HomeCoreResponse>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<HomeCoreResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.uitpayapp.models.ApiResponse<HomeCoreResponse> apiResponse = response.body();
                    if (apiResponse.getCode() == 200 && apiResponse.getData() != null) {
                        HomeCoreResponse apiData = apiResponse.getData();
                        HomeCoreResponse mixedData = new HomeCoreResponse();
                        mixedData.setTopics(apiData.getTopics());
                        mixedData.setFlashSales(new java.util.ArrayList<>()); // keep mock flashsales empty
                        coreData.setValue(UiState.success(mixedData));
                    } else {
                        coreData.setValue(UiState.error(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi từ server", null));
                    }
                } else {
                    coreData.setValue(UiState.error("Không thể kết nối server (Mã lỗi: " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<HomeCoreResponse>> call, Throwable t) {
                coreData.setValue(UiState.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });
    }

    private void fetchBrandsData() {
        brandsData.setValue(UiState.loading(null));
        String jsonBrands = "{\"brands\":[]}";
        BrandResponse mockBrands = new com.google.gson.Gson().fromJson(jsonBrands, BrandResponse.class);
        brandsData.setValue(UiState.success(mockBrands));
    }

    public void resetAndFetchDeals(int tabId) {
        currentTabId = tabId;
        currentDealsPage = 1;
        hasMoreDeals = true;
        accumulatedDeals.clear();
        dealsData.setValue(UiState.loading(accumulatedDeals));
        fetchDealsPage();
    }

    public void loadNextDealsPage() {
        if (!isDealsLoading && hasMoreDeals) {
            currentDealsPage++;
            fetchDealsPage();
        }
    }

    private void fetchDealsPage() {
        isDealsLoading = true;
        dealsData.setValue(UiState.success(new ArrayList<>()));
        hasMoreDeals = false;
        isDealsLoading = false;
    }
}
