package com.example.uitpayapp.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;

import com.example.uitpayapp.home.home_adapters.FoodCategoryAdapter;
import com.example.uitpayapp.home.home_adapters.FoodMenuAdapter;
import com.example.uitpayapp.home.home_adapters.HomeDealAdapter;
import com.example.uitpayapp.home.home_adapters.BrandAdapter;
import com.example.uitpayapp.home.home_adapters.TopicStoreAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.home_models.Restaurant;
import com.example.uitpayapp.home.home_models.TopicStore;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.recommendeddeal.RecommendedDealActivity;
import com.example.uitpayapp.recommendeddeal.RecommendedDealModel;
import com.example.uitpayapp.utils.CartAnimationHelper;
import com.example.uitpayapp.home.home_adapters.TopicStoreAdapter;
import com.example.uitpayapp.home.home_adapters.ImageSliderAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.home.home_models.FoodMenuItem;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.lifecycle.ViewModelProvider;
import com.example.uitpayapp.home.network.HomeCoreResponse;
import com.example.uitpayapp.home.network.BrandResponse;
import com.example.uitpayapp.home.network.TopicResponse;
import android.util.Log;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel viewModel;

    private List<Restaurant> restaurants;
    private List<Restaurant> filteredRestaurants;
    private FoodCategoryAdapter categoryAdapter;

    private List<Object> dealItems; // Mixed list: RecommendedDealModel, BannerItem, null(loading)
    private HomeDealAdapter homeDealAdapter;
    private TabLayout tabHomeDeals;
    private TabLayout stickyTabLayout;
    private boolean isSyncing = false;
    private int statusBarHeight = 0;
    private boolean isLoadingMore = false;
    private int dealItemCount = 0; // Counts only deal items (not banners)
    private int nextBannerAt = 5; // Insert banner after this many deal items
    private final Random bannerRandom = new Random();

    private final String[] RANDOM_SUBTITLES = {"Khám phá ngay", "Gợi ý cho bạn", "Đừng bỏ lỡ"};

    private String selectedCategory = null;
    private String currentSearchQuery = "";
    private TextView tvDeliveryAddress;

    private android.os.Handler sliderHandler;
    private Runnable sliderRunnable;
    private ImageSliderAdapter bannerAdapter;
    private List<String> loadedBannerUrls = new ArrayList<>();
    private String currentDeliveryAddress = "";
    
    private android.os.Handler searchHintHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchHintRunnable;
    private final String[] SEARCH_HINTS = {
            "Hôm nay ăn gì?", 
            "Thèm Trà Sữa?", 
            "Bún Bò nóng hổi", 
            "Ăn vặt không?", 
            "Pizza béo ngậy"
    };

    private android.os.CountDownTimer flashSaleTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_header_bar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + 16, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        stickyTabLayout = findViewById(R.id.tab_home_deals_sticky);

        setupImageSlider();

        findViewById(R.id.btn_cart).setOnClickListener(v -> checkoutGlobalCart());

        updateGlobalCartBadge();

        tvDeliveryAddress = findViewById(R.id.tv_delivery_address);
        findViewById(R.id.layout_address_bar).setOnClickListener(v -> showAddressSelection());
        View dummyAddressBar = findViewById(R.id.layout_address_bar_dummy);
        if (dummyAddressBar != null) {
            dummyAddressBar.setOnClickListener(v -> showAddressSelection());
        }

        RetrofitClient.initialize(this);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupCategories();
        setupSearch();
        setupDeals();
        setupStickyTab();
        setupBottomNavigation();
        setupObservers();

        loadInitialAddress();

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = findViewById(
                R.id.swipe_refresh_home);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                viewModel.refreshAll();
                swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    private void loadInitialAddress() {
        com.example.uitpayapp.network.SessionManager sessionManager = com.example.uitpayapp.network.SessionManager.getInstance(this);
        if (sessionManager.getAuthToken() != null && !sessionManager.getAuthToken().isEmpty()) {
            String savedAddress = sessionManager.getDeliveryAddressText();
            if (savedAddress != null && !savedAddress.isEmpty()) {
                updateAddressUI(savedAddress);
            } else {
                new com.example.uitpayapp.modules.user.AddressRepository().getDefaultAddress(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO>() {
                    @Override
                    public void onSuccess(com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO result) {
                        if (result != null) {
                            String addr = result.getDetailedAddress();
                            if (addr == null || addr.isEmpty()) addr = "Hiện chưa có địa chỉ";
                            sessionManager.saveDeliveryAddress(result.getId(), addr);
                            updateAddressUI(addr);
                        } else {
                            updateAddressUI("Hiện chưa có địa chỉ");
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        updateAddressUI("Hiện chưa có địa chỉ");
                    }
                });
            }
        } else {
            updateAddressUI("Vui lòng đăng nhập");
        }
    }

    private void updateAddressUI(String address) {
        currentDeliveryAddress = address;
        if (tvDeliveryAddress != null) tvDeliveryAddress.setText(address);
        TextView tvDummy = findViewById(R.id.tv_delivery_address_dummy);
        if (tvDummy != null) tvDummy.setText(address);
        
        TextView tvLabel = findViewById(R.id.tv_delivery_label);
        TextView tvLabelDummy = findViewById(R.id.tv_delivery_label_dummy);
        TextView tvArrow = findViewById(R.id.tv_delivery_arrow);
        View addressBar = findViewById(R.id.layout_address_bar);
        View addressBarDummy = findViewById(R.id.layout_address_bar_dummy);
        
        if ("Vui lòng đăng nhập".equals(address)) {
            if (tvLabel != null) tvLabel.setVisibility(View.GONE);
            if (tvLabelDummy != null) tvLabelDummy.setVisibility(View.GONE);
            if (tvArrow != null) tvArrow.setVisibility(View.GONE);
            if (addressBar != null) addressBar.setClickable(false);
            if (addressBarDummy != null) addressBarDummy.setClickable(false);
            if (tvDeliveryAddress != null) tvDeliveryAddress.setText("Vui lòng đăng nhập để chọn địa chỉ");
            if (tvDummy != null) tvDummy.setText("Vui lòng đăng nhập để chọn địa chỉ");
        } else {
            if (tvLabel != null) tvLabel.setVisibility(View.VISIBLE);
            if (tvLabelDummy != null) tvLabelDummy.setVisibility(View.VISIBLE);
            if (tvArrow != null) tvArrow.setVisibility(View.VISIBLE);
            if (addressBar != null) addressBar.setClickable(true);
            if (addressBarDummy != null) addressBarDummy.setClickable(true);
        }
        
        viewModel.setAddressAndRefresh(address);
    }

    private void setupObservers() {
        viewModel.getCoreData().observe(this, state -> {
            View fsLoading = findViewById(R.id.layout_flashsale_loading);
            View fsError = findViewById(R.id.layout_flashsale_error);
            View fsSection = findViewById(R.id.flashsale_section);

            View t1Loading = findViewById(R.id.layout_topic1_loading);
            View t1Error = findViewById(R.id.layout_topic1_error);
            View t1Section = findViewById(R.id.random_topic_section_1);

            View t2Loading = findViewById(R.id.layout_topic2_loading);
            View t2Error = findViewById(R.id.layout_topic2_error);
            View t2Section = findViewById(R.id.random_topic_section_2);

            
            View catLoading = findViewById(R.id.layout_categories_loading);
            View catError = findViewById(R.id.layout_categories_error);
            View catContent = findViewById(R.id.layout_categories_content);

            if (state.isLoading()) {
                if (fsLoading != null)
                    fsLoading.setVisibility(View.VISIBLE);
                if (fsError != null)
                    fsError.setVisibility(View.GONE);
                if (fsSection != null)
                    fsSection.setVisibility(View.GONE);

                if (t1Loading != null)
                    t1Loading.setVisibility(View.VISIBLE);
                if (t1Error != null)
                    t1Error.setVisibility(View.GONE);
                if (t1Section != null)
                    t1Section.setVisibility(View.GONE);

                if (t2Loading != null)
                    t2Loading.setVisibility(View.VISIBLE);
                if (t2Error != null)
                    t2Error.setVisibility(View.GONE);
                if (t2Section != null)
                    t2Section.setVisibility(View.GONE);

                if (catLoading != null)
                    catLoading.setVisibility(View.VISIBLE);
                if (catError != null)
                    catError.setVisibility(View.GONE);
                if (catContent != null)
                    catContent.setVisibility(View.GONE);
                    
                View bannerLoading = findViewById(R.id.layout_banner_loading);
                View bannerError = findViewById(R.id.layout_banner_error);
                View bannerContent = findViewById(R.id.imgAdvertisement);
                if (bannerLoading != null) bannerLoading.setVisibility(View.VISIBLE);
                if (bannerError != null) bannerError.setVisibility(View.GONE);
                if (bannerContent != null) bannerContent.setVisibility(View.GONE);

            } else if (state.isSuccess()) {
                if (fsLoading != null) fsLoading.setVisibility(View.GONE);
                if (fsError != null) fsError.setVisibility(View.GONE);
                if (fsSection != null) fsSection.setVisibility(View.VISIBLE);

                if (catLoading != null) catLoading.setVisibility(View.GONE);
                if (catError != null) catError.setVisibility(View.GONE);
                
                View bannerLoading = findViewById(R.id.layout_banner_loading);
                View bannerError = findViewById(R.id.layout_banner_error);
                View bannerContent = findViewById(R.id.imgAdvertisement);
                if (bannerLoading != null) bannerLoading.setVisibility(View.GONE);
                if (bannerError != null) bannerError.setVisibility(View.GONE);
                
                HomeCoreResponse data = state.getData();
                if (data != null) {
                    List<FoodCategory> serverCats = data.getCategories();
                    List<FoodCategory> displayCats = getStaticCategories(serverCats);
                    if (catContent != null)
                        catContent.setVisibility(View.VISIBLE);
                    if (categoryAdapter != null)
                        categoryAdapter.updateData(displayCats);


                    if (data.getFlashSales() != null && !data.getFlashSales().isEmpty()) {
                        if (fsError != null)
                            fsError.setVisibility(View.GONE);
                        if (fsSection != null)
                            fsSection.setVisibility(View.VISIBLE);
                        updateFlashsaleUI(data.getFlashSales());
                    } else {
                        if (fsSection != null)
                            fsSection.setVisibility(View.GONE);
                        if (fsError != null) {
                            fsError.setVisibility(View.VISIBLE);
                            android.widget.TextView tvFsError = findViewById(R.id.tv_flashsale_error);
                            if (tvFsError != null)
                                tvFsError.setText("Chưa có dữ liệu");
                        }
                    }
                    if (data.getTopics() != null && data.getTopics().size() >= 2) {
                        if (t1Error != null)
                            t1Error.setVisibility(View.GONE);
                        if (t1Section != null)
                            t1Section.setVisibility(View.VISIBLE);
                        updateTopicUI(findViewById(R.id.random_topic_section_1), data.getTopics().get(0));

                        if (t2Error != null)
                            t2Error.setVisibility(View.GONE);
                        if (t2Section != null)
                            t2Section.setVisibility(View.VISIBLE);
                        updateTopicUI(findViewById(R.id.random_topic_section_2), data.getTopics().get(1));
                        
                        applyRandomSubtitles();
                    } else {
                        if (t1Section != null)
                            t1Section.setVisibility(View.GONE);
                        if (t1Error != null) {
                            t1Error.setVisibility(View.VISIBLE);
                            android.widget.TextView tvT1Error = findViewById(R.id.tv_topic1_error);
                            if (tvT1Error != null)
                                tvT1Error.setText("Chưa có dữ liệu");
                        }

                        if (t2Section != null)
                            t2Section.setVisibility(View.GONE);
                        if (t2Error != null) {
                            t2Error.setVisibility(View.VISIBLE);
                            android.widget.TextView tvT2Error = findViewById(R.id.tv_topic2_error);
                            if (tvT2Error != null)
                                tvT2Error.setText("Chưa có dữ liệu");
                        }
                    }
                    
                    if (data.getBanners() != null && !data.getBanners().isEmpty()) {
                        if (bannerError != null) bannerError.setVisibility(View.GONE);
                        if (bannerContent != null) bannerContent.setVisibility(View.VISIBLE);
                        List<String> bannerUrls = new ArrayList<>();
                        for (com.example.uitpayapp.home.network.Banner b : data.getBanners()) {
                            bannerUrls.add(b.getImageUrl());
                        }
                        loadedBannerUrls.clear();
                        loadedBannerUrls.addAll(bannerUrls);
                        
                        if (bannerAdapter != null) {
                            bannerAdapter.updateData(bannerUrls);
                        }
                    } else {
                        if (bannerContent != null) bannerContent.setVisibility(View.GONE);
                        if (bannerError != null) {
                            bannerError.setVisibility(View.VISIBLE);
                            android.widget.TextView tvBannerError = findViewById(R.id.tv_banner_error);
                            if (tvBannerError != null)
                                tvBannerError.setText("Chưa có dữ liệu");
                        }
                    }
                }
            } else if (state.isError() || state.isEmpty()) {
                if (fsLoading != null)
                    fsLoading.setVisibility(View.GONE);
                if (fsSection != null)
                    fsSection.setVisibility(View.GONE);
                if (fsError != null) {
                    fsError.setVisibility(View.VISIBLE);
                    android.widget.TextView tvFsError = findViewById(R.id.tv_flashsale_error);
                    if (tvFsError != null)
                        tvFsError.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }

                if (catLoading != null)
                    catLoading.setVisibility(View.GONE);
                if (catContent != null)
                    catContent.setVisibility(View.GONE);
                if (catError != null) {
                    catError.setVisibility(View.VISIBLE);
                    android.widget.TextView tvCatError = findViewById(R.id.tv_categories_error);
                    if (tvCatError != null)
                        tvCatError.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }

                if (t1Loading != null)
                    t1Loading.setVisibility(View.GONE);
                if (t1Section != null)
                    t1Section.setVisibility(View.GONE);
                if (t1Error != null) {
                    t1Error.setVisibility(View.VISIBLE);
                    android.widget.TextView tvT1Error = findViewById(R.id.tv_topic1_error);
                    if (tvT1Error != null)
                        tvT1Error.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }

                if (t2Loading != null)
                    t2Loading.setVisibility(View.GONE);
                if (t2Section != null)
                    t2Section.setVisibility(View.GONE);
                if (t2Error != null) {
                    t2Error.setVisibility(View.VISIBLE);
                    android.widget.TextView tvT2Error = findViewById(R.id.tv_topic2_error);
                    if (tvT2Error != null)
                        tvT2Error.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }
                
                View bannerLoading = findViewById(R.id.layout_banner_loading);
                View bannerError = findViewById(R.id.layout_banner_error);
                View bannerContent = findViewById(R.id.imgAdvertisement);
                if (bannerLoading != null) bannerLoading.setVisibility(View.GONE);
                if (bannerContent != null) bannerContent.setVisibility(View.GONE);
                if (bannerError != null) {
                    bannerError.setVisibility(View.VISIBLE);
                    android.widget.TextView tvBannerError = findViewById(R.id.tv_banner_error);
                    if (tvBannerError != null)
                        tvBannerError.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }
            }
        });

        viewModel.getRandomTopicsData().observe(this, state -> {
            View t1Loading = findViewById(R.id.layout_topic1_loading);
            View t1Error = findViewById(R.id.layout_topic1_error);
            View t1Section = findViewById(R.id.random_topic_section_1);

            View t2Loading = findViewById(R.id.layout_topic2_loading);
            View t2Error = findViewById(R.id.layout_topic2_error);
            View t2Section = findViewById(R.id.random_topic_section_2);

            if (state.isLoading()) {
                if (t1Loading != null) t1Loading.setVisibility(View.VISIBLE);
                if (t1Error != null) t1Error.setVisibility(View.GONE);
                if (t1Section != null) t1Section.setVisibility(View.GONE);

                if (t2Loading != null) t2Loading.setVisibility(View.VISIBLE);
                if (t2Error != null) t2Error.setVisibility(View.GONE);
                if (t2Section != null) t2Section.setVisibility(View.GONE);
            } else if (state.isSuccess()) {
                if (t1Loading != null) t1Loading.setVisibility(View.GONE);
                if (t2Loading != null) t2Loading.setVisibility(View.GONE);
                
                java.util.List<com.example.uitpayapp.home.network.TopicResponse> data = state.getData();
                if (data != null) {
                    if (data.size() >= 1) {
                        if (t1Error != null) t1Error.setVisibility(View.GONE);
                        if (t1Section != null) t1Section.setVisibility(View.VISIBLE);
                        com.example.uitpayapp.home.network.TopicResponse t1 = data.get(0);
                        setupTopicSection(t1Section, t1.getTitle(), t1.getSubtitle(), t1.getCategoryId(), t1.getItems());
                    } else {
                        if (t1Section != null) t1Section.setVisibility(View.GONE);
                        if (t1Error != null) {
                            t1Error.setVisibility(View.VISIBLE);
                            android.widget.TextView tvTopic1Error = findViewById(R.id.tv_topic1_error);
                            if (tvTopic1Error != null) tvTopic1Error.setText("Chưa có dữ liệu");
                        }
                    }

                    if (data.size() >= 2) {
                        if (t2Error != null) t2Error.setVisibility(View.GONE);
                        if (t2Section != null) t2Section.setVisibility(View.VISIBLE);
                        com.example.uitpayapp.home.network.TopicResponse t2 = data.get(1);
                        setupTopicSection(t2Section, t2.getTitle(), t2.getSubtitle(), t2.getCategoryId(), t2.getItems());
                    } else {
                        if (t2Section != null) t2Section.setVisibility(View.GONE);
                        if (t2Error != null) {
                            t2Error.setVisibility(View.VISIBLE);
                            android.widget.TextView tvTopic2Error = findViewById(R.id.tv_topic2_error);
                            if (tvTopic2Error != null) tvTopic2Error.setText("Chưa có dữ liệu");
                        }
                    }
                    applyRandomSubtitles();
                }
            } else if (state.isError() || state.isEmpty()) {
                if (t1Loading != null) t1Loading.setVisibility(View.GONE);
                if (t1Section != null) t1Section.setVisibility(View.GONE);
                if (t1Error != null) {
                    t1Error.setVisibility(View.VISIBLE);
                    android.widget.TextView tvTopic1Error = findViewById(R.id.tv_topic1_error);
                    if (tvTopic1Error != null) tvTopic1Error.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }

                if (t2Loading != null) t2Loading.setVisibility(View.GONE);
                if (t2Section != null) t2Section.setVisibility(View.GONE);
                if (t2Error != null) {
                    t2Error.setVisibility(View.VISIBLE);
                    android.widget.TextView tvTopic2Error = findViewById(R.id.tv_topic2_error);
                    if (tvTopic2Error != null) tvTopic2Error.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }
            }
        });

        viewModel.getBrandsData().observe(this, state -> {
            View loadingView = findViewById(R.id.layout_brands_loading);
            View sectionView = findViewById(R.id.topic_brand_section);
            View errorView = findViewById(R.id.layout_brands_error);
            View divider = findViewById(R.id.divider_brands);

            if (state.isLoading()) {
                if (loadingView != null)
                    loadingView.setVisibility(View.VISIBLE);
                if (sectionView != null)
                    sectionView.setVisibility(View.GONE);
                if (errorView != null)
                    errorView.setVisibility(View.GONE);
            } else if (state.isSuccess()) {
                if (loadingView != null)
                    loadingView.setVisibility(View.GONE);
                if (errorView != null)
                    errorView.setVisibility(View.GONE);
                if (sectionView != null)
                    sectionView.setVisibility(View.VISIBLE);
                if (divider != null)
                    divider.setVisibility(View.VISIBLE);
                BrandResponse data = state.getData();
                if (data != null && data.getBrands() != null) {
                    updateBrandsUI(data.getBrands());
                }
            } else if (state.isError() || state.isEmpty()) {
                if (loadingView != null)
                    loadingView.setVisibility(View.GONE);
                if (sectionView != null)
                    sectionView.setVisibility(View.GONE);
                if (divider != null)
                    divider.setVisibility(View.VISIBLE);
                if (errorView != null) {
                    errorView.setVisibility(View.VISIBLE);
                    android.widget.TextView tvBrandsError = findViewById(R.id.tv_brands_error);
                    if (tvBrandsError != null)
                        tvBrandsError.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }
            }
        });

        viewModel.getDealsData().observe(this, state -> {
            View emptyView = findViewById(R.id.layout_deals_empty);
            View errorView = findViewById(R.id.layout_deals_error);
            androidx.recyclerview.widget.RecyclerView rvDeals = findViewById(R.id.rv_home_deals);
            View loadingOverlay = findViewById(R.id.layout_loading_overlay);
            boolean isStickyVisible = stickyTabLayout != null && stickyTabLayout.getVisibility() == View.VISIBLE;

            if (state.isLoading()) {
                if (isStickyVisible && loadingOverlay != null) {
                    loadingOverlay.setVisibility(View.VISIBLE);
                } else {
                    if (emptyView != null)
                        emptyView.setVisibility(View.GONE);
                    if (errorView != null)
                        errorView.setVisibility(View.GONE);
                    if (rvDeals != null)
                        rvDeals.setVisibility(View.VISIBLE);
                    if (dealItems.isEmpty() || dealItems.get(dealItems.size() - 1) != null) {
                        dealItems.add(null);
                        if (homeDealAdapter != null)
                            homeDealAdapter.notifyItemInserted(dealItems.size() - 1);
                    }
                }
                isLoadingMore = true;
            } else {
                if (loadingOverlay != null)
                    loadingOverlay.setVisibility(View.GONE);
                int loadingPos = dealItems.indexOf(null);
                if (loadingPos >= 0) {
                    dealItems.remove(loadingPos);
                    if (homeDealAdapter != null)
                        homeDealAdapter.notifyItemRemoved(loadingPos);
                }
                isLoadingMore = false;

                if (state.isSuccess()) {
                    if (emptyView != null)
                        emptyView.setVisibility(View.GONE);
                    if (errorView != null)
                        errorView.setVisibility(View.GONE);
                    if (rvDeals != null)
                        rvDeals.setVisibility(View.VISIBLE);
                    updateDealsUI(state.getData());
                } else if (state.isEmpty()) {
                    if (rvDeals != null)
                        rvDeals.setVisibility(View.GONE);
                    if (errorView != null)
                        errorView.setVisibility(View.GONE);
                    if (emptyView != null)
                        emptyView.setVisibility(View.VISIBLE);
                } else if (state.isError()) {
                    if (rvDeals != null)
                        rvDeals.setVisibility(View.GONE);
                    if (emptyView != null)
                        emptyView.setVisibility(View.GONE);
                    if (errorView != null)
                        errorView.setVisibility(View.VISIBLE);
                    android.widget.TextView tvError = findViewById(R.id.tv_deals_error);
                    if (tvError != null)
                        tvError.setText(state.getMessage());
                }
            }
        });

    }

    private void updateFlashsaleUI(List<FoodMenuItem> flashsaleFoods) {
        if (flashsaleFoods.size() < 3)
            return;
        int[] cardIds = { R.id.card_flashsale_1, R.id.card_flashsale_2, R.id.card_flashsale_3 };
        int[] ivIds = { R.id.iv_flashsale_1, R.id.iv_flashsale_2, R.id.iv_flashsale_3 };
        int[] nameIds = { R.id.tv_name_1, R.id.tv_name_2, R.id.tv_name_3 };
        int[] origPriceIds = { R.id.tv_orig_price_1, R.id.tv_orig_price_2, R.id.tv_orig_price_3 };
        int[] discPriceIds = { R.id.tv_disc_price_1, R.id.tv_disc_price_2, R.id.tv_disc_price_3 };

        for (int i = 0; i < 3; i++) {
            FoodMenuItem item = flashsaleFoods.get(i);
            View card = findViewById(cardIds[i]);
            if (card == null)
                continue;

            android.widget.ImageView iv = card.findViewById(ivIds[i]);
            android.widget.TextView tvName = card.findViewById(nameIds[i]);
            android.widget.TextView tvOrigPrice = card.findViewById(origPriceIds[i]);
            android.widget.TextView tvDiscPrice = card.findViewById(discPriceIds[i]);

            iv.clearAnimation();
            String imageUrl = item.getImageUrl();
            android.graphics.drawable.ColorDrawable grayPlaceholder = new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E0E0E0"));
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                android.view.animation.AlphaAnimation blinkAnimation = new android.view.animation.AlphaAnimation(0.5f, 1.0f);
                blinkAnimation.setDuration(500);
                blinkAnimation.setRepeatMode(android.view.animation.Animation.REVERSE);
                blinkAnimation.setRepeatCount(android.view.animation.Animation.INFINITE);
                iv.startAnimation(blinkAnimation);

                com.bumptech.glide.request.RequestOptions options = new com.bumptech.glide.request.RequestOptions().placeholder(grayPlaceholder);
                com.bumptech.glide.Glide.with(iv.getContext())
                        .load(imageUrl)
                        .apply(options)
                        .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                iv.clearAnimation();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                iv.clearAnimation();
                                return false;
                            }
                        })
                        .into(iv);
            } else if (item.getImageResId() != 0) {
                iv.setImageResource(item.getImageResId());
            } else {
                iv.setImageDrawable(grayPlaceholder);
            }
            tvName.setText(item.getName());

            long originalPrice = item.getPrice();
            long discountedPrice = originalPrice / 2;

            tvOrigPrice.setText(String.format("%,dđ", originalPrice).replace(',', '.'));
            tvOrigPrice.setPaintFlags(tvOrigPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

            tvDiscPrice.setText(String.format("%,dđ", discountedPrice).replace(',', '.'));

            card.setOnClickListener(v -> {
                FoodMenuItem discountedItem = new FoodMenuItem(
                        item.getId(),
                        item.getName(),
                        discountedPrice,
                        item.getImageResId(),
                        item.getDescription(),
                        item.getImageUrl());
                discountedItem.setRestaurantId(item.getRestaurantId());
                showFoodItemDetailPopup(discountedItem, iv);
            });
        }
        
        startFlashSaleTimer();
    }

    private void startFlashSaleTimer() {
        TextView tvHours = findViewById(R.id.tv_countdown_hours);
        TextView tvMinutes = findViewById(R.id.tv_countdown_minutes);
        TextView tvSeconds = findViewById(R.id.tv_countdown_seconds);

        if (tvHours == null || tvMinutes == null || tvSeconds == null) return;

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        calendar.add(java.util.Calendar.HOUR_OF_DAY, 1);
        
        long nextHour = calendar.getTimeInMillis();
        long timeLeft = nextHour - now;

        if (flashSaleTimer != null) {
            flashSaleTimer.cancel();
        }

        flashSaleTimer = new android.os.CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;

                tvHours.setText(String.format("%02d", hours));
                tvMinutes.setText(String.format("%02d", minutes));
                tvSeconds.setText(String.format("%02d", seconds));
            }

            public void onFinish() {
                startFlashSaleTimer();
            }
        }.start();
    }

    private void updateTopicUI(View sectionView, TopicResponse topic) {
        if (sectionView == null || topic == null)
            return;
        android.widget.TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        android.widget.TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        androidx.recyclerview.widget.RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);
        android.widget.TextView tvEmpty = sectionView.findViewById(R.id.tv_topic_empty);

        tvTitle.setText(topic.getTitle());
        tvSubtitle.setText(topic.getSubtitle());

        if (topic.getFoods() == null || topic.getFoods().isEmpty()) {
            rvStores.setVisibility(View.GONE);
            if (tvEmpty != null)
                tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvStores.setVisibility(View.VISIBLE);
            if (tvEmpty != null)
                tvEmpty.setVisibility(View.GONE);

            if (rvStores.getLayoutManager() == null) {
                rvStores.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this,
                        androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            }

            TopicStoreAdapter adapter = new TopicStoreAdapter(topic.getFoods(), (item, holder) -> {
                showFoodItemDetailPopup(item, holder.ivImage);
            });
            rvStores.setAdapter(adapter);

            android.widget.TextView tvSeeMore = sectionView.findViewById(R.id.tv_topic_see_more);
            if (tvSeeMore != null) {
                tvSeeMore.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CategoryActivity.class);
                    intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY, topic.getTitle());
                    intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY_ID, topic.getCategoryId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            }
        }
    }

    private void applyRandomSubtitles() {
        List<String> subs = new ArrayList<>(Arrays.asList(RANDOM_SUBTITLES));
        java.util.Collections.shuffle(subs);
        
        View t1 = findViewById(R.id.random_topic_section_1);
        if (t1 != null) {
            TextView tvSub1 = t1.findViewById(R.id.tv_topic_subtitle);
            if (tvSub1 != null) {
                tvSub1.setText(subs.get(0));
                tvSub1.setVisibility(View.VISIBLE);
            }
        }
        
        View t2 = findViewById(R.id.random_topic_section_2);
        if (t2 != null) {
            TextView tvSub2 = t2.findViewById(R.id.tv_topic_subtitle);
            if (tvSub2 != null) {
                tvSub2.setText(subs.get(1));
                tvSub2.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateBrandsUI(List<Restaurant> brands) {
        View sectionView = findViewById(R.id.topic_brand_section);
        if (sectionView == null)
            return;

        TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        TextView tvSeeMore = sectionView.findViewById(R.id.tv_topic_see_more);

        if (tvTitle != null)
            tvTitle.setText("Cửa hàng nổi bật");
        if (tvSubtitle != null)
            tvSubtitle.setText("Các cửa hàng được yêu thích nhất");
        if (tvSeeMore != null)
            tvSeeMore.setVisibility(View.GONE);

        androidx.recyclerview.widget.RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);
        if (rvStores.getLayoutManager() == null) {
            rvStores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        BrandAdapter brandAdapter = new BrandAdapter(brands, restaurant -> {
            Intent intent = new Intent(this, StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
            if (restaurant.getId() != null) {
                intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
            }
            startActivity(intent);
        });
        rvStores.setAdapter(brandAdapter);
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    private void updateDealsUI(List<RecommendedDealModel> deals) {
        dealItems.clear();
        dealItemCount = 0;
        nextBannerAt = 5 + bannerRandom.nextInt(3); // 5-7

        for (RecommendedDealModel deal : deals) {
            dealItems.add(deal);
            dealItemCount++;

            if (dealItemCount >= nextBannerAt && !loadedBannerUrls.isEmpty()) {
                String randomUrl = loadedBannerUrls.get(bannerRandom.nextInt(loadedBannerUrls.size()));
                dealItems.add(new HomeDealAdapter.BannerItem(randomUrl));
                nextBannerAt = dealItemCount + 5 + bannerRandom.nextInt(3);
            }
        }

        if (homeDealAdapter != null) {
            homeDealAdapter.notifyDataSetChanged();
        }
    }

    private void setupImageSlider() {
        ViewPager2 viewPager2 = findViewById(R.id.imgAdvertisement);
        bannerAdapter = new ImageSliderAdapter(new ArrayList<>());
        viewPager2.setAdapter(bannerAdapter);

        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerAdapter != null && bannerAdapter.getItemCount() > 1) {
                    int currentItem = viewPager2.getCurrentItem();
                    currentItem = (currentItem + 1) % bannerAdapter.getItemCount();
                    viewPager2.setCurrentItem(currentItem, true);
                }
                sliderHandler.postDelayed(this, 3000);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navFavorite = findViewById(R.id.navFavorite);
        LinearLayout navNotification = findViewById(R.id.navNotification);
        LinearLayout navAccount = findViewById(R.id.navAccount);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                NestedScrollView scrollView = findViewById(R.id.food_main_scroll);
                if (scrollView != null)
                    scrollView.smoothScrollTo(0, 0);
            });
        }

        if (navHistory != null) {
            navHistory.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.uitpayapp.history.TransactionHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (navFavorite != null) {
            navFavorite.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.uitpayapp.favorite.FavoriteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (navNotification != null) {
            navNotification.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.uitpayapp.notification.NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.uitpayapp.profile.ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void showAddressSelection() {
        com.example.uitpayapp.network.SessionManager sessionManager = com.example.uitpayapp.network.SessionManager.getInstance(this);
        if (sessionManager.getAuthToken() == null || sessionManager.getAuthToken().isEmpty()) {
            android.widget.Toast.makeText(this, "Vui lòng đăng nhập để chọn địa chỉ", android.widget.Toast.LENGTH_SHORT).show();
            // Optional: Chuyển sang màn đăng nhập
            return;
        }

        new com.example.uitpayapp.modules.user.AddressRepository().getMyAddresses(new com.example.uitpayapp.network.ApiCallback<java.util.List<com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO>>() {
            @Override
            public void onSuccess(java.util.List<com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO> result) {
                Long currentId = sessionManager.getDeliveryAddressId();
                com.example.uitpayapp.utils.AddressBottomSheetHelper.showAddressBottomSheet(
                        HomeActivity.this,
                        result,
                        currentId,
                        selectedAddress -> {
                            sessionManager.saveDeliveryAddress(selectedAddress.getId(), selectedAddress.getDetailedAddress());
                            updateAddressUI(selectedAddress.getDetailedAddress());
                        }
                );
            }

            @Override
            public void onError(String errorMessage) {
                android.widget.Toast.makeText(HomeActivity.this, errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        android.widget.EditText etSearch = findViewById(R.id.et_search);
        View searchContainer = findViewById(R.id.layout_search_container);
        if (etSearch != null && searchContainer != null) {
            etSearch.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                        HomeActivity.this,
                        searchContainer,
                        "search_bar_transition");
                startActivity(intent, options.toBundle());
            });
            
            searchHintRunnable = new Runnable() {
                int hintIndex = 0;
                @Override
                public void run() {
                    hintIndex = (hintIndex + 1) % SEARCH_HINTS.length;
                    String nextHint = SEARCH_HINTS[hintIndex];
                    
                    etSearch.animate().alpha(0.3f).setDuration(300).withEndAction(() -> {
                        etSearch.setHint(nextHint);
                        etSearch.animate().alpha(1f).setDuration(300).start();
                    }).start();
                    
                    searchHintHandler.postDelayed(this, 5000);
                }
            };
            searchHintHandler.postDelayed(searchHintRunnable, 5000);
        }
    }

    private void setupCategories() {
        List<FoodCategory> categories = new ArrayList<>();

        RecyclerView rv = findViewById(R.id.rv_categories);
        rv.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(
                this, 2, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false));
        categoryAdapter = new FoodCategoryAdapter(categories, category -> {
            if (category != null) {
                if ("Danh mục".equals(category.getName())) {
                    Intent intent = new Intent(this, AllCategoriesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Intent intent = new Intent(this, CategoryActivity.class);
                    intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY, category.getName());
                    if (category.getId() != null) {
                        intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY_ID, category.getId());
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
        rv.setAdapter(categoryAdapter);

        View indicatorThumb = findViewById(R.id.indicator_thumb);
        if (indicatorThumb != null) {
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int offset = recyclerView.computeHorizontalScrollOffset();
                    int extent = recyclerView.computeHorizontalScrollExtent();
                    int range = recyclerView.computeHorizontalScrollRange();
                    if (range > extent) {
                        float maxScroll = range - extent;
                        float percentage = offset / maxScroll;

                        float density = getResources().getDisplayMetrics().density;
                        float maxTranslation = 20 * density; // 40dp track - 20dp thumb
                        indicatorThumb.setTranslationX(percentage * maxTranslation);
                    }
                }
            });
        }
    }

    private void applyFilters() {
        filteredRestaurants = new ArrayList<>();
        for (Restaurant r : restaurants) {
            boolean matchesCategory = true;
            boolean matchesSearch = true;

            if (selectedCategory != null) {
                matchesCategory = selectedCategory.equals(r.getCategory());
            }

            if (!currentSearchQuery.isEmpty()) {
                String name = r.getName().replace("\n", " ").toLowerCase();
                boolean nameMatch = name.contains(currentSearchQuery);
                boolean menuMatch = false;
                for (FoodMenuItem item : r.getMenu()) {
                    if (item.getName().toLowerCase().contains(currentSearchQuery)) {
                        menuMatch = true;
                        break;
                    }
                }
                matchesSearch = nameMatch || menuMatch;
            }

            if (matchesCategory && matchesSearch) {
                filteredRestaurants.add(r);
            }
        }
    }

    private void setupRestaurants() {
        restaurants = HomeRepository.getInstance().getRestaurants();
        filteredRestaurants = new ArrayList<>(restaurants);
    }

    private void setupTopics() {
        List<Object[]> topicPool = HomeRepository.getInstance().getTopics();

        java.util.Collections.shuffle(topicPool);
        int[] sectionIds = { R.id.random_topic_section_1, R.id.random_topic_section_2 };
        for (int i = 0; i < 2; i++) {
            Object[] topic = topicPool.get(i);
            @SuppressWarnings("unchecked")
            List<FoodMenuItem> foods = (List<FoodMenuItem>) topic[2];
            setupTopicSection(findViewById(sectionIds[i]), (String) topic[0], (String) topic[1], foods);
        }
        applyRandomSubtitles();
    }

    private void setupBrands() {
        View sectionView = findViewById(R.id.topic_brand_section);
        if (sectionView == null)
            return;

        TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        TextView tvSeeMore = sectionView.findViewById(R.id.tv_topic_see_more);
        RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);

        tvTitle.setText("Thương hiệu nổi bật");
        tvSubtitle.setText("Các cửa hàng được yêu thích nhất");

        rvStores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        BrandAdapter brandAdapter = new BrandAdapter(new ArrayList<>(), restaurant -> {
            Intent intent = new Intent(this, StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
            startActivity(intent);
        });
        rvStores.setAdapter(brandAdapter);

        com.example.uitpayapp.network.RetrofitClient.getRestaurantService().getAllRestaurants().enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<com.example.uitpayapp.home.home_models.Restaurant> mappedRestaurants = new ArrayList<>();
                    for (com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO dto : response.body().getData()) {
                        String shortName = dto.getName() != null && dto.getName().length() > 0 ? dto.getName().substring(0, 1) : "A";
                        double ratingVal = dto.getRatingAverage() != null ? dto.getRatingAverage() : 0.0;
                        int reviewsVal = dto.getReviewCount() != null ? dto.getReviewCount() : 0;
                        mappedRestaurants.add(new com.example.uitpayapp.home.home_models.Restaurant(
                                dto.getId(), dto.getName(), shortName, 
                                android.graphics.Color.parseColor("#E4002B"), "Danh mục", 
                                new ArrayList<>(), R.drawable.img_food_chicken, 
                                ratingVal, reviewsVal, 30, dto.getAddress(), dto.getImageUrl()));
                    }
                    brandAdapter.updateData(mappedRestaurants);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>> call, Throwable t) {
                // Fallback to mock data on error
                brandAdapter.updateData(HomeRepository.getInstance().getRestaurants());
            }
        });

        tvSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllBrandsActivity.class);
            startActivity(intent);
        });
    }

    private void setupFlashsale() {
        List<FoodMenuItem> flashsaleFoods = HomeRepository.getInstance().getDealFoods();
        if (flashsaleFoods.size() < 3)
            return;

        java.util.Collections.shuffle(flashsaleFoods);

        int[] cardIds = { R.id.card_flashsale_1, R.id.card_flashsale_2, R.id.card_flashsale_3 };
        int[] ivIds = { R.id.iv_flashsale_1, R.id.iv_flashsale_2, R.id.iv_flashsale_3 };
        int[] nameIds = { R.id.tv_name_1, R.id.tv_name_2, R.id.tv_name_3 };
        int[] origPriceIds = { R.id.tv_orig_price_1, R.id.tv_orig_price_2, R.id.tv_orig_price_3 };
        int[] discPriceIds = { R.id.tv_disc_price_1, R.id.tv_disc_price_2, R.id.tv_disc_price_3 };

        for (int i = 0; i < 3; i++) {
            FoodMenuItem item = flashsaleFoods.get(i);
            View card = findViewById(cardIds[i]);
            if (card == null)
                continue;

            ImageView iv = card.findViewById(ivIds[i]);
            TextView tvName = card.findViewById(nameIds[i]);
            TextView tvOrigPrice = card.findViewById(origPriceIds[i]);
            TextView tvDiscPrice = card.findViewById(discPriceIds[i]);

            iv.setImageResource(item.getImageResId());
            tvName.setText(item.getName());

            long originalPrice = item.getPrice();
            long discountedPrice = originalPrice / 2;

            tvOrigPrice.setText(String.format("%,dđ", originalPrice).replace(',', '.'));
            tvOrigPrice.setPaintFlags(tvOrigPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

            tvDiscPrice.setText(String.format("%,dđ", discountedPrice).replace(',', '.'));

            card.setOnClickListener(v -> {
                FoodMenuItem discountedItem = new FoodMenuItem(
                        item.getId(),
                        item.getName(),
                        discountedPrice,
                        item.getImageResId(),
                        item.getDescription(),
                        item.getImageUrl());
                discountedItem.setRestaurantId(item.getRestaurantId());
                showFoodItemDetailPopup(discountedItem, iv);
            });
        }
    }

    private void setupTopicSection(View sectionView, String title, String subtitle, List<FoodMenuItem> foods) {
        TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        TextView tvSeeMore = sectionView.findViewById(R.id.tv_topic_see_more);
        RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);

        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
        tvSubtitle.setVisibility(View.GONE); // Hide subtitle as requested

        rvStores.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        TopicStoreAdapter adapter = new TopicStoreAdapter(foods, (item, holder) -> {
            showFoodItemDetailPopup(item, holder.ivImage);
        });
        rvStores.setAdapter(adapter);

        tvSeeMore.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.home.CategoryActivity.class);
            intent.putExtra(com.example.uitpayapp.home.CategoryActivity.EXTRA_SELECTED_CATEGORY, title);
            intent.putExtra(com.example.uitpayapp.home.CategoryActivity.EXTRA_SELECTED_CATEGORY_ID, categoryId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void showFoodItemDetailPopup(FoodMenuItem item, android.widget.ImageView sourceImage) {
        com.example.uitpayapp.utils.FoodDetailBottomSheetHelper.show(this, item, null,
                (selectedItem, quantity, selectedToppings) -> {
                    CartItem newItem = new CartItem(selectedItem, quantity, selectedToppings);
                    CartManager.getInstance().addItemSync(newItem,
                            new com.example.uitpayapp.network.ApiCallback<String>() {
                                @Override
                                public void onSuccess(String data) {
                                    runOnUiThread(() -> {
                                        View btnCart = findViewById(R.id.btn_cart);
                                        CartAnimationHelper.animateFlyToCart(HomeActivity.this,
                                                sourceImage != null ? sourceImage : findViewById(android.R.id.content),
                                                btnCart, () -> {
                                                    updateGlobalCartBadge();
                                                });
                                    });
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    runOnUiThread(() -> {
                                        android.widget.Toast.makeText(HomeActivity.this,
                                                "Không thể thêm vào giỏ hàng: " + errorMessage,
                                                android.widget.Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                });
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    private void setupDeals() {
        tabHomeDeals = findViewById(R.id.tab_home_deals);
        RecyclerView rvDeals = findViewById(R.id.rv_home_deals);

        dealItems = new ArrayList<>();

        homeDealAdapter = new HomeDealAdapter(dealItems);
        rvDeals.setLayoutManager(new LinearLayoutManager(this));
        rvDeals.setAdapter(homeDealAdapter);

        // Prevent RecyclerView from grabbing focus and scrolling
        rvDeals.setFocusable(false);
        rvDeals.setFocusableInTouchMode(false);
        rvDeals.setDescendantFocusability(android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // Initial load
        resetAndLoadDeals();

        tabHomeDeals.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isSyncing) {
                    isSyncing = true;
                    TabLayout.Tab stickyTab = stickyTabLayout.getTabAt(tab.getPosition());
                    if (stickyTab != null)
                        stickyTab.select();
                    isSyncing = false;
                }
                resetAndLoadDeals();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        stickyTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isSyncing) {
                    isSyncing = true;
                    TabLayout.Tab originalTab = tabHomeDeals.getTabAt(tab.getPosition());
                    if (originalTab != null)
                        originalTab.select();
                    isSyncing = false;
                }
                resetAndLoadDeals();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    private void resetAndLoadDeals() {
        if (viewModel != null) {
            viewModel.resetAndFetchDeals(tabHomeDeals.getSelectedTabPosition());
        }
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    private void loadMoreDeals(boolean useFloatingOverlay) {
        if (viewModel != null) {
            viewModel.loadNextDealsPage();
        }
    }

    private void setupStickyTab() {
        NestedScrollView scrollView = findViewById(R.id.food_main_scroll);

        scrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    // Sticky tab logic
                    int[] tabLocation = new int[2];
                    tabHomeDeals.getLocationOnScreen(tabLocation);
                    int tabTopOnScreen = tabLocation[1];

                    View layoutHeaderBar = findViewById(R.id.layout_header_bar);
                    int[] headerLocation = new int[2];
                    layoutHeaderBar.getLocationOnScreen(headerLocation);
                    int headerBottomOnScreen = headerLocation[1] + layoutHeaderBar.getHeight();

                    if (tabTopOnScreen <= headerBottomOnScreen) {
                        if (stickyTabLayout.getVisibility() != View.VISIBLE) {
                            stickyTabLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (stickyTabLayout.getVisibility() != View.GONE) {
                            stickyTabLayout.setVisibility(View.GONE);
                        }
                    }

                    // Infinite scroll: load more when near bottom
                    View child = v.getChildAt(0);
                    if (child != null) {
                        int scrollRange = child.getHeight() - v.getHeight();
                        if (scrollY >= scrollRange - 500 && !isLoadingMore) {
                            loadMoreDeals(false);
                        }
                    }
                });
    }

    private void showRestaurantMenu(Restaurant restaurant) {
        Intent intent = new Intent(this, StoreDetailActivity.class);
        intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
        if (restaurant.getId() != null) {
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGlobalCartBadge();
        updateNotificationBadge();
    }

    private void updateGlobalCartBadge() {
        final TextView tvBadge = findViewById(R.id.tv_global_cart_badge);
        if (tvBadge == null)
            return;
        CartManager.getInstance().getCartCountSync(new com.example.uitpayapp.network.ApiCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                runOnUiThread(() -> {
                    if (count != null && count > 0) {
                        tvBadge.setVisibility(View.VISIBLE);
                        tvBadge.setText(String.valueOf(count));
                    } else {
                        tvBadge.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    int count = CartManager.getInstance().getTotalItemCount();
                    if (count > 0) {
                        tvBadge.setVisibility(View.VISIBLE);
                        tvBadge.setText(String.valueOf(count));
                    } else {
                        tvBadge.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void updateNotificationBadge() {
        final TextView tvNotificationBadge = findViewById(R.id.tv_notification_badge);
        if (tvNotificationBadge == null)
            return;

        com.example.uitpayapp.modules.notification.NotificationRepository repo = new com.example.uitpayapp.modules.notification.NotificationRepository();
        repo.getUnreadCount(new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Long>>() {
            @Override
            public void onSuccess(java.util.Map<String, Long> countData) {
                long unreadCount = countData != null && countData.containsKey("unreadCount")
                        ? countData.get("unreadCount")
                        : 0;
                if (unreadCount > 0) {
                    tvNotificationBadge.setText(String.valueOf(unreadCount));
                    tvNotificationBadge.setVisibility(View.VISIBLE);
                } else {
                    tvNotificationBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Fail silently
            }
        });
    }

    private void checkoutGlobalCart() {
        if (!com.example.uitpayapp.network.SessionManager.getInstance(this).isLoggedIn()) {
            com.example.uitpayapp.utils.LoginPopupHelper.showLoginRequiredPopup(this);
            return;
        }
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
        if (searchHintHandler != null && searchHintRunnable != null) {
            searchHintHandler.removeCallbacks(searchHintRunnable);
        }
        if (flashSaleTimer != null) {
            flashSaleTimer.cancel();
        }
    }


    private List<FoodCategory> getStaticCategories(List<FoodCategory> serverCategories) {
        List<FoodCategory> list = new ArrayList<>();
        list.add(new FoodCategory("Cơm", R.drawable.ic_cat_com, 0));
        list.add(new FoodCategory("Bún Phở", R.drawable.ic_cat_bun_pho, 0));
        list.add(new FoodCategory("Bánh mì", R.drawable.ic_cat_banh_mi, 0));
        list.add(new FoodCategory("Fastfood", R.drawable.ic_cat_fastfood, 0));
        list.add(new FoodCategory("Lẩu", R.drawable.ic_cat_lau, 0));
        list.add(new FoodCategory("Đồ nướng", R.drawable.ic_cat_bbq, 0));
        list.add(new FoodCategory("Cà Phê", R.drawable.ic_cat_ca_phe, 0));
        list.add(new FoodCategory("Trà sữa", R.drawable.ic_cat_tra_sua, 0));
        list.add(new FoodCategory("Ăn vặt", R.drawable.ic_cat_an_vat, 0));
        list.add(new FoodCategory("Danh mục", R.drawable.ic_cat_all, 0, true));

        if (serverCategories != null) {
            for (FoodCategory staticCat : list) {
                String matchStr = staticCat.getName().toLowerCase();
                if (matchStr.equals("bún phở")) matchStr = "bún";
                if (matchStr.equals("cà phê")) matchStr = "phê";
                if (matchStr.equals("đồ nướng")) matchStr = "nướng";
                
                for (FoodCategory serverCat : serverCategories) {
                    if (serverCat.getName() != null) {
                        if (serverCat.getName().toLowerCase().contains(matchStr)) {
                            staticCat.setId(serverCat.getId());
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }
}
