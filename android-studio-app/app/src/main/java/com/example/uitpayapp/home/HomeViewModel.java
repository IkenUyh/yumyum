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
        apiService.getHomeCore(currentAddressId).enqueue(new Callback<HomeCoreResponse>() {
            @Override
            public void onResponse(Call<HomeCoreResponse> call, Response<HomeCoreResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    coreData.setValue(UiState.success(response.body()));
                } else {
                    coreData.setValue(UiState.error("Không kết nối được server (Lỗi " + response.code() + ")", null));
                }
            }

            @Override
            public void onFailure(Call<HomeCoreResponse> call, Throwable t) {
                coreData.setValue(UiState.error("Không kết nối được server: " + t.getMessage(), null));
            }
        });
    }

    private void fetchBrandsData() {
        brandsData.setValue(UiState.loading(null));
        apiService.getPopularBrands(currentAddressId).enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getBrands() == null || response.body().getBrands().isEmpty()) {
                        brandsData.setValue(UiState.empty());
                    } else {
                        brandsData.setValue(UiState.success(response.body()));
                    }
                } else {
                    brandsData.setValue(UiState.error("Không tải được danh sách thương hiệu", null));
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                brandsData.setValue(UiState.error("Không kết nối được server", null));
            }
        });
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
        apiService.getRecommendedDeals(currentAddressId, currentTabId, currentDealsPage, 10)
                .enqueue(new Callback<DealResponse>() {
            @Override
            public void onResponse(Call<DealResponse> call, Response<DealResponse> response) {
                isDealsLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<RecommendedDealModel> newDeals = response.body().getDeals();
                    if (newDeals != null) {
                        accumulatedDeals.addAll(newDeals);
                    }
                    if (accumulatedDeals.isEmpty()) {
                        dealsData.setValue(UiState.empty());
                    } else {
                        dealsData.setValue(UiState.success(new ArrayList<>(accumulatedDeals)));
                    }
                    
                    if (currentDealsPage >= response.body().getTotalPages()) {
                        hasMoreDeals = false;
                    }
                } else {
                    if (accumulatedDeals.isEmpty()) {
                        dealsData.setValue(UiState.error("Không kết nối được server", null));
                    }
                }
            }

            @Override
            public void onFailure(Call<DealResponse> call, Throwable t) {
                isDealsLoading = false;
                if (accumulatedDeals.isEmpty()) {
                    dealsData.setValue(UiState.error("Không kết nối được server", null));
                }
            }
        });
    }
}
