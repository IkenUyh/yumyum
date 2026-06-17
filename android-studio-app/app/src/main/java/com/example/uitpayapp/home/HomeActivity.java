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
import com.example.uitpayapp.home.FakeDealGenerator;
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

    private String selectedCategory = null;
    private String currentSearchQuery = "";
    private TextView tvDeliveryAddress;

    private Handler sliderHandler;
    private Runnable sliderRunnable;

    private static final String[] ADDRESSES = {
            "48 Phó Cơ Điều, Phường Chợ Lớn, TP. Hồ Chí Minh",
            "268 Lý Thường Kiệt, Phường Diên Hồng, TP. Hồ Chí Minh",
            "803 Kha Vạn Cân, Phường Linh Xuân, TP. Hồ Chí Minh",
            "215 Điện Biên Phủ, Phường Gia Định, TP. Hồ Chí Minh"
    };

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

        setupRestaurants();
        setupCategories();
        setupSearch();
        setupDeals();
        setupDeals();
        setupStickyTab();
        setupBottomNavigation();
        setupObservers();

        viewModel.setAddressAndRefresh(ADDRESSES[0]);

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_home);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                viewModel.refreshAll();
                swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    private void setupObservers() {
        viewModel.getCoreData().observe(this, state -> {
            View fsLoading = findViewById(R.id.layout_flashsale_loading);
            View fsError = findViewById(R.id.layout_flashsale_error);
            View fsSection = findViewById(R.id.flashsale_section);
            
            View t1Loading = findViewById(R.id.layout_topic1_loading);
            View t1Error = findViewById(R.id.layout_topic1_error);
            View t1Section = findViewById(R.id.topic_section_1);
            
            View t2Loading = findViewById(R.id.layout_topic2_loading);
            View t2Error = findViewById(R.id.layout_topic2_error);
            View t2Section = findViewById(R.id.topic_section_2);

            if (state.isLoading()) {
                if (fsLoading != null) fsLoading.setVisibility(View.VISIBLE);
                if (fsError != null) fsError.setVisibility(View.GONE);
                if (fsSection != null) fsSection.setVisibility(View.GONE);
                
                if (t1Loading != null) t1Loading.setVisibility(View.VISIBLE);
                if (t1Error != null) t1Error.setVisibility(View.GONE);
                if (t1Section != null) t1Section.setVisibility(View.GONE);
                
                if (t2Loading != null) t2Loading.setVisibility(View.VISIBLE);
                if (t2Error != null) t2Error.setVisibility(View.GONE);
                if (t2Section != null) t2Section.setVisibility(View.GONE);
            } else if (state.isSuccess()) {
                if (fsLoading != null) fsLoading.setVisibility(View.GONE);
                if (fsError != null) fsError.setVisibility(View.GONE);
                if (fsSection != null) fsSection.setVisibility(View.VISIBLE);
                
                if (t1Loading != null) t1Loading.setVisibility(View.GONE);
                if (t1Error != null) t1Error.setVisibility(View.GONE);
                if (t1Section != null) t1Section.setVisibility(View.VISIBLE);
                
                if (t2Loading != null) t2Loading.setVisibility(View.GONE);
                if (t2Error != null) t2Error.setVisibility(View.GONE);
                if (t2Section != null) t2Section.setVisibility(View.VISIBLE);
                
                HomeCoreResponse data = state.getData();
                if (data != null) {
                    if (data.getCategories() != null && !data.getCategories().isEmpty() && categoryAdapter != null) {
                        categoryAdapter.updateData(data.getCategories());
                    }

                    if (data.getFlashSales() != null && !data.getFlashSales().isEmpty()) {
                        if (fsError != null) fsError.setVisibility(View.GONE);
                        if (fsSection != null) fsSection.setVisibility(View.VISIBLE);
                        updateFlashsaleUI(data.getFlashSales());
                    } else {
                        if (fsSection != null) fsSection.setVisibility(View.GONE);
                        if (fsError != null) {
                            fsError.setVisibility(View.VISIBLE);
                            android.widget.TextView tvFsError = findViewById(R.id.tv_flashsale_error);
                            if (tvFsError != null) tvFsError.setText("Chưa có dữ liệu");
                        }
                    }
                    if (data.getTopics() != null && data.getTopics().size() >= 2) {
                        if (t1Error != null) t1Error.setVisibility(View.GONE);
                        if (t1Section != null) t1Section.setVisibility(View.VISIBLE);
                        updateTopicUI(findViewById(R.id.topic_section_1), data.getTopics().get(0));
                        
                        if (t2Error != null) t2Error.setVisibility(View.GONE);
                        if (t2Section != null) t2Section.setVisibility(View.VISIBLE);
                        updateTopicUI(findViewById(R.id.topic_section_2), data.getTopics().get(1));
                    } else {
                        if (t1Section != null) t1Section.setVisibility(View.GONE);
                        if (t1Error != null) {
                            t1Error.setVisibility(View.VISIBLE);
                            android.widget.TextView tvT1Error = findViewById(R.id.tv_topic1_error);
                            if (tvT1Error != null) tvT1Error.setText("Chưa có dữ liệu");
                        }
                        
                        if (t2Section != null) t2Section.setVisibility(View.GONE);
                        if (t2Error != null) {
                            t2Error.setVisibility(View.VISIBLE);
                            android.widget.TextView tvT2Error = findViewById(R.id.tv_topic2_error);
                            if (tvT2Error != null) tvT2Error.setText("Chưa có dữ liệu");
                        }
                    }
                }
            } else if (state.isError() || state.isEmpty()) {
                if (fsLoading != null) fsLoading.setVisibility(View.GONE);
                if (fsSection != null) fsSection.setVisibility(View.GONE);
                if (fsError != null) {
                    fsError.setVisibility(View.VISIBLE);
                    android.widget.TextView tvFsError = findViewById(R.id.tv_flashsale_error);
                    if (tvFsError != null) tvFsError.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }
                
                if (t1Loading != null) t1Loading.setVisibility(View.GONE);
                if (t1Section != null) t1Section.setVisibility(View.GONE);
                if (t1Error != null) {
                    t1Error.setVisibility(View.VISIBLE);
                    android.widget.TextView tvT1Error = findViewById(R.id.tv_topic1_error);
                    if (tvT1Error != null) tvT1Error.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }
                
                if (t2Loading != null) t2Loading.setVisibility(View.GONE);
                if (t2Section != null) t2Section.setVisibility(View.GONE);
                if (t2Error != null) {
                    t2Error.setVisibility(View.VISIBLE);
                    android.widget.TextView tvT2Error = findViewById(R.id.tv_topic2_error);
                    if (tvT2Error != null) tvT2Error.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
                }
            }
        });

        viewModel.getBrandsData().observe(this, state -> {
            View loadingView = findViewById(R.id.layout_brands_loading);
            View sectionView = findViewById(R.id.topic_brand_section);
            View errorView = findViewById(R.id.layout_brands_error);
            View divider = findViewById(R.id.divider_brands);
            
            if (state.isLoading()) {
                if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
                if (sectionView != null) sectionView.setVisibility(View.GONE);
                if (errorView != null) errorView.setVisibility(View.GONE);
            } else if (state.isSuccess()) {
                if (loadingView != null) loadingView.setVisibility(View.GONE);
                if (errorView != null) errorView.setVisibility(View.GONE);
                if (sectionView != null) sectionView.setVisibility(View.VISIBLE);
                if (divider != null) divider.setVisibility(View.VISIBLE);
                BrandResponse data = state.getData();
                if (data != null && data.getBrands() != null) {
                    updateBrandsUI(data.getBrands());
                }
            } else if (state.isError() || state.isEmpty()) {
                if (loadingView != null) loadingView.setVisibility(View.GONE);
                if (sectionView != null) sectionView.setVisibility(View.GONE);
                if (divider != null) divider.setVisibility(View.VISIBLE);
                if (errorView != null) {
                    errorView.setVisibility(View.VISIBLE);
                    android.widget.TextView tvBrandsError = findViewById(R.id.tv_brands_error);
                    if (tvBrandsError != null) tvBrandsError.setText(state.getMessage() != null ? state.getMessage() : "Chưa có dữ liệu");
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
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                    if (errorView != null) errorView.setVisibility(View.GONE);
                    if (rvDeals != null) rvDeals.setVisibility(View.VISIBLE);
                    if (dealItems.isEmpty() || dealItems.get(dealItems.size() - 1) != null) {
                        dealItems.add(null);
                        if (homeDealAdapter != null) homeDealAdapter.notifyItemInserted(dealItems.size() - 1);
                    }
                }
                isLoadingMore = true;
            } else {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                int loadingPos = dealItems.indexOf(null);
                if (loadingPos >= 0) {
                    dealItems.remove(loadingPos);
                    if (homeDealAdapter != null) homeDealAdapter.notifyItemRemoved(loadingPos);
                }
                isLoadingMore = false;
                
                if (state.isSuccess()) {
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                    if (errorView != null) errorView.setVisibility(View.GONE);
                    if (rvDeals != null) rvDeals.setVisibility(View.VISIBLE);
                    updateDealsUI(state.getData());
                } else if (state.isEmpty()) {
                    if (rvDeals != null) rvDeals.setVisibility(View.GONE);
                    if (errorView != null) errorView.setVisibility(View.GONE);
                    if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
                } else if (state.isError()) {
                    if (rvDeals != null) rvDeals.setVisibility(View.GONE);
                    if (emptyView != null) emptyView.setVisibility(View.GONE);
                    if (errorView != null) errorView.setVisibility(View.VISIBLE);
                    android.widget.TextView tvError = findViewById(R.id.tv_deals_error);
                    if (tvError != null) tvError.setText(state.getMessage());
                }
            }
        });

    }

    private void updateFlashsaleUI(List<FoodMenuItem> flashsaleFoods) {
        if (flashsaleFoods.size() < 3) return;
        int[] cardIds = {R.id.card_flashsale_1, R.id.card_flashsale_2, R.id.card_flashsale_3};
        int[] ivIds = {R.id.iv_flashsale_1, R.id.iv_flashsale_2, R.id.iv_flashsale_3};
        int[] nameIds = {R.id.tv_name_1, R.id.tv_name_2, R.id.tv_name_3};
        int[] origPriceIds = {R.id.tv_orig_price_1, R.id.tv_orig_price_2, R.id.tv_orig_price_3};
        int[] discPriceIds = {R.id.tv_disc_price_1, R.id.tv_disc_price_2, R.id.tv_disc_price_3};

        for (int i = 0; i < 3; i++) {
            FoodMenuItem item = flashsaleFoods.get(i);
            View card = findViewById(cardIds[i]);
            if (card == null) continue;

            android.widget.ImageView iv = card.findViewById(ivIds[i]);
            android.widget.TextView tvName = card.findViewById(nameIds[i]);
            android.widget.TextView tvOrigPrice = card.findViewById(origPriceIds[i]);
            android.widget.TextView tvDiscPrice = card.findViewById(discPriceIds[i]);

            iv.setImageResource(item.getImageResId() != 0 ? item.getImageResId() : 0);
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
                        item.getDescription()
                );
                showFoodItemDetailPopup(discountedItem, iv);
            });
        }
    }

    private void updateTopicUI(View sectionView, TopicResponse topic) {
        if (sectionView == null || topic == null) return;
        android.widget.TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        android.widget.TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        androidx.recyclerview.widget.RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);
        android.widget.TextView tvEmpty = sectionView.findViewById(R.id.tv_topic_empty);

        tvTitle.setText(topic.getTitle());
        tvSubtitle.setText(topic.getSubtitle());

        if (topic.getFoods() == null || topic.getFoods().isEmpty()) {
            rvStores.setVisibility(View.GONE);
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvStores.setVisibility(View.VISIBLE);
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            
            if (rvStores.getLayoutManager() == null) {
                rvStores.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            }
            
            TopicStoreAdapter adapter = new TopicStoreAdapter(topic.getFoods(), (item, holder) -> {
                showFoodItemDetailPopup(item, holder.ivImage);
            });
            rvStores.setAdapter(adapter);
        }
    }

    private void updateBrandsUI(List<Restaurant> brands) {
        View sectionView = findViewById(R.id.topic_brand_section);
        if (sectionView == null) return;
        
        TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        TextView tvSeeMore = sectionView.findViewById(R.id.tv_topic_see_more);
        
        if (tvTitle != null) tvTitle.setText("Cửa hàng nổi bật");
        if (tvSubtitle != null) tvSubtitle.setText("Các cửa hàng được yêu thích nhất");
        if (tvSeeMore != null) tvSeeMore.setVisibility(View.GONE);

        androidx.recyclerview.widget.RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);
        if (rvStores.getLayoutManager() == null) {
            rvStores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        BrandAdapter brandAdapter = new BrandAdapter(brands, restaurant -> {
            Intent intent = new Intent(this, StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
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

            if (dealItemCount >= nextBannerAt) {
                dealItems.add(HomeDealAdapter.BannerItem.random());
                nextBannerAt = dealItemCount + 5 + bannerRandom.nextInt(3);
            }
        }
        
        if (homeDealAdapter != null) {
            homeDealAdapter.notifyDataSetChanged();
        }
    }

    private void setupImageSlider() {
        ViewPager2 viewPager2 = findViewById(R.id.imgAdvertisement);
        List<Integer> imageList = Arrays.asList(
                R.drawable.img_priority_banner1,
                R.drawable.img_priority_banner2,
                R.drawable.img_priority_banner3
        );
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(imageList);
        viewPager2.setAdapter(sliderAdapter);

        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                currentItem = (currentItem + 1) % imageList.size();
                viewPager2.setCurrentItem(currentItem, true);
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
                if (scrollView != null) scrollView.smoothScrollTo(0, 0);
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
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_destination, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_destination_title)).setText("Chọn địa chỉ giao");

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btn_close_destination).setOnClickListener(v -> dialog.dismiss());

        LinearLayout rootLayout = (LinearLayout) view;
        View destWallet = view.findViewById(R.id.btn_dest_wallet);
        View destSaving = view.findViewById(R.id.btn_dest_saving);
        if (destWallet != null) rootLayout.removeView(destWallet);
        if (destSaving != null) rootLayout.removeView(destSaving);

        for (String address : ADDRESSES) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            int paddingVertical = (int)(16 * getResources().getDisplayMetrics().density);
            itemLayout.setPadding(0, paddingVertical, 0, paddingVertical);
            
            android.util.TypedValue outValue = new android.util.TypedValue();
            getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            itemLayout.setBackgroundResource(outValue.resourceId);

            ImageView iv = new ImageView(this);
            int iconSize = (int)(24 * getResources().getDisplayMetrics().density);
            iv.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
            iv.setImageResource(R.drawable.ic_location);
            iv.setColorFilter(Color.parseColor("#f24405"));

            TextView tv = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins((int)(16 * getResources().getDisplayMetrics().density), 0, 0, 0);
            tv.setLayoutParams(params);
            tv.setText(address);
            tv.setTextSize(15f);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setMaxLines(2);
            tv.setEllipsize(android.text.TextUtils.TruncateAt.END);

            itemLayout.addView(iv);
            itemLayout.addView(tv);

            itemLayout.setOnClickListener(v -> {
                tvDeliveryAddress.setText(address);
                TextView tvDummy = findViewById(R.id.tv_delivery_address_dummy);
                if (tvDummy != null) tvDummy.setText(address);
                dialog.dismiss();
                if (viewModel != null) viewModel.setAddressAndRefresh(address);
            });

            rootLayout.addView(itemLayout);
        }

        dialog.show();
    }

    private void setupSearch() {
        View etSearch = findViewById(R.id.et_search);
        View searchContainer = findViewById(R.id.layout_search_container);
        if (etSearch != null && searchContainer != null) {
            etSearch.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        HomeActivity.this,
                        searchContainer,
                        "search_bar_transition"
                );
                startActivity(intent, options.toBundle());
            });
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
        int[] sectionIds = {R.id.topic_section_1, R.id.topic_section_2};
        for (int i = 0; i < 2; i++) {
            Object[] topic = topicPool.get(i);
            @SuppressWarnings("unchecked")
            List<FoodMenuItem> foods = (List<FoodMenuItem>) topic[2];
            setupTopicSection(findViewById(sectionIds[i]), (String) topic[0], (String) topic[1], foods);
        }
    }

    private void setupBrands() {
        View sectionView = findViewById(R.id.topic_brand_section);
        if (sectionView == null) return;

        TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        TextView tvSeeMore = sectionView.findViewById(R.id.tv_topic_see_more);
        RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);

        tvTitle.setText("Thương hiệu nổi bật");
        tvSubtitle.setText("Các cửa hàng được yêu thích nhất");

        rvStores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        BrandAdapter brandAdapter = new BrandAdapter(restaurants, restaurant -> {
            Intent intent = new Intent(this, StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
            startActivity(intent);
        });
        rvStores.setAdapter(brandAdapter);

        tvSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllBrandsActivity.class);
            startActivity(intent);
        });
    }

    private void setupFlashsale() {
        List<FoodMenuItem> flashsaleFoods = HomeRepository.getInstance().getDealFoods();
        if (flashsaleFoods.size() < 3) return;
        
        java.util.Collections.shuffle(flashsaleFoods);

        int[] cardIds = {R.id.card_flashsale_1, R.id.card_flashsale_2, R.id.card_flashsale_3};
        int[] ivIds = {R.id.iv_flashsale_1, R.id.iv_flashsale_2, R.id.iv_flashsale_3};
        int[] nameIds = {R.id.tv_name_1, R.id.tv_name_2, R.id.tv_name_3};
        int[] origPriceIds = {R.id.tv_orig_price_1, R.id.tv_orig_price_2, R.id.tv_orig_price_3};
        int[] discPriceIds = {R.id.tv_disc_price_1, R.id.tv_disc_price_2, R.id.tv_disc_price_3};

        for (int i = 0; i < 3; i++) {
            FoodMenuItem item = flashsaleFoods.get(i);
            View card = findViewById(cardIds[i]);
            if (card == null) continue;

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
                        item.getDescription()
                );
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

        rvStores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        TopicStoreAdapter adapter = new TopicStoreAdapter(foods, (item, holder) -> {
            showFoodItemDetailPopup(item, holder.ivImage);
        });
        rvStores.setAdapter(adapter);

        tvSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY, title);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void showFoodItemDetailPopup(FoodMenuItem item, ImageView sourceImage) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_food_detail, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        ImageView btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        ImageView ivFoodImage = view.findViewById(R.id.iv_food_image);
        TextView tvFoodName = view.findViewById(R.id.tv_food_name);
        TextView tvFoodDesc = view.findViewById(R.id.tv_food_desc);
        TextView tvFoodPrice = view.findViewById(R.id.tv_food_price);

        ivFoodImage.setImageResource(item.getImageResId());
        tvFoodName.setText(item.getName());
        tvFoodDesc.setText(item.getDescription());
        tvFoodPrice.setText(item.getFormattedPrice());

        final int[] popupQty = {1};
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        View btnDecrease = view.findViewById(R.id.btn_decrease);
        View btnIncrease = view.findViewById(R.id.btn_increase);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        
        final int[] toppingTotal = {0};

        // Initial total
        updatePopupPrice(view, item.getPrice(), toppingTotal[0]);

        btnDecrease.setOnClickListener(v -> {
            if (popupQty[0] > 1) {
                popupQty[0]--;
                tvQuantity.setText(String.valueOf(popupQty[0]));
                updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
            }
        });

        btnIncrease.setOnClickListener(v -> {
            popupQty[0]++;
            tvQuantity.setText(String.valueOf(popupQty[0]));
            updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
        });

        // Add mock toppings
        LinearLayout layoutToppings = view.findViewById(R.id.layout_toppings_container);
        String[] mockToppings = {"Thêm trân châu đen", "Thêm phô mai", "Thêm thạch mảng cầu"};
        int[] mockPrices = {5000, 10000, 5000};
        
        final java.util.List<com.example.uitpayapp.home.home_models.CartTopping> selectedToppings = new java.util.ArrayList<>();

        for (int i = 0; i < 3; i++) {
            View toppingView = android.view.LayoutInflater.from(this).inflate(R.layout.item_food_topping, layoutToppings, false);
            android.widget.CheckBox cbTopping = toppingView.findViewById(R.id.cb_topping);
            TextView tvToppingPrice = toppingView.findViewById(R.id.tv_topping_price);
            cbTopping.setText(mockToppings[i]);
            
            if (mockPrices[i] > 0) {
                tvToppingPrice.setText("+" + String.format("%,dđ", mockPrices[i]).replace(',', '.'));
            } else {
                tvToppingPrice.setText("0đ");
            }
            
            final int price = mockPrices[i];
            final String toppingName = mockToppings[i];
            final String toppingId = "tp_" + i;
            cbTopping.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    toppingTotal[0] += price;
                    selectedToppings.add(new com.example.uitpayapp.home.home_models.CartTopping(toppingId, toppingName, price));
                } else {
                    toppingTotal[0] -= price;
                    selectedToppings.remove(new com.example.uitpayapp.home.home_models.CartTopping(toppingId, toppingName, price));
                }
                updatePopupPrice(view, item.getPrice(), toppingTotal[0]);
            });
            
            layoutToppings.addView(toppingView);
        }

        btnAddToCart.setOnClickListener(v -> {
            CartItem newItem = new CartItem(item, popupQty[0], new java.util.ArrayList<>(selectedToppings));
            CartManager.getInstance().addItemSync(newItem, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    CartManager.getInstance().addItem(newItem);
                    runOnUiThread(() -> {
                        View btnCart = findViewById(R.id.btn_cart);
                        CartAnimationHelper.animateFlyToCart(HomeActivity.this, ivFoodImage, btnCart, () -> {
                            updateGlobalCartBadge();
                        });
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        android.widget.Toast.makeText(HomeActivity.this, "Không thể thêm vào giỏ hàng: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                    });
                }
            });
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updatePopupPrice(View view, long itemPrice, int toppingTotal) {
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        TextView btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        int qty = Integer.parseInt(tvQuantity.getText().toString());
        long total = (itemPrice + toppingTotal) * qty;
        btnAddToCart.setText("Thêm vào giỏ - " + String.format("%,dđ", total).replace(',', '.'));
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
                    if (stickyTab != null) stickyTab.select();
                    isSyncing = false;
                }
                resetAndLoadDeals();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        stickyTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isSyncing) {
                    isSyncing = true;
                    TabLayout.Tab originalTab = tabHomeDeals.getTabAt(tab.getPosition());
                    if (originalTab != null) originalTab.select();
                    isSyncing = false;
                }
                resetAndLoadDeals();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
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

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
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
        if (tvBadge == null) return;
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
        if (tvNotificationBadge == null) return;
        
        com.example.uitpayapp.modules.notification.NotificationRepository repo = 
                new com.example.uitpayapp.modules.notification.NotificationRepository();
        repo.getUnreadCount(new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Long>>() {
            @Override
            public void onSuccess(java.util.Map<String, Long> countData) {
                long unreadCount = countData != null && countData.containsKey("unreadCount") ? countData.get("unreadCount") : 0;
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
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    public static class HomeRepository {
        private static HomeRepository instance;

        private HomeRepository() {
        }

        public static synchronized HomeRepository getInstance() {
            if (instance == null) {
                instance = new HomeRepository();
            }
            return instance;
        }

        public java.util.List<FoodMenuItem> getPopularFoods() {
            java.util.List<FoodMenuItem> list = new java.util.ArrayList<>();
            list.add(new FoodMenuItem("f_1", "Gà rán KFC", 45000, R.drawable.img_food_chicken, "Gà rán giòn rụm"));
            list.add(new FoodMenuItem("f_2", "Trà sữa thái", 25000, R.drawable.img_food_bubbletea, "Trà sữa thái xanh trân châu"));
            list.add(new FoodMenuItem("f_3", "Cà phê đen đá", 15000, R.drawable.img_food_coffee, "Cà phê phin truyền thống"));
            list.add(new FoodMenuItem("f_4", "Pizza xúc xích", 89000, R.drawable.img_food_pizza, "Pizza phô mai xúc xích"));
            list.add(new FoodMenuItem("f_5", "Gà cay phô mai", 55000, R.drawable.img_food_chicken, "Gà xào bắp cải phô mai"));
            list.add(new FoodMenuItem("f_6", "Trà đào", 30000, R.drawable.img_food_bubbletea, "Trà đào cam sả thanh mát"));
            return list;
        }

        public java.util.List<FoodMenuItem> getDealFoods() {
            java.util.List<FoodMenuItem> list = new java.util.ArrayList<>();
            list.add(new FoodMenuItem("d_1", "Gà rán truyền thống", 45000, R.drawable.img_food_chicken, "1 miếng gà rán giòn"));
            list.add(new FoodMenuItem("d_2", "Combo gà rán + khoai", 89000, R.drawable.img_food_chicken, "2 miếng gà + khoai tây"));
            list.add(new FoodMenuItem("d_3", "Burger gà giòn", 39000, R.drawable.img_food_chicken, "Burger gà với rau tươi"));
            list.add(new FoodMenuItem("d_4", "Cơm gà sốt cay", 55000, R.drawable.img_food_chicken, "Cơm trắng + gà sốt cay"));
            list.add(new FoodMenuItem("d_5", "Trà sen vàng", 45000, R.drawable.img_food_bubbletea, "Trà ướp sen thơm mát"));
            list.add(new FoodMenuItem("d_6", "Trà đào cam sả", 55000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"));
            list.add(new FoodMenuItem("d_7", "Cà phê sữa đá", 35000, R.drawable.img_food_coffee, "Cà phê phin truyền thống"));
            list.add(new FoodMenuItem("d_8", "Bánh mì chà bông", 25000, R.drawable.img_food_chicken, "Bánh mì nướng giòn"));
            return list;
        }

        public java.util.List<FoodMenuItem> getCategoryFoods(int categoryIndex) {
            java.util.List<FoodMenuItem> list = new java.util.ArrayList<>();
            switch (categoryIndex) {
                case 0:
                    list.add(new FoodMenuItem("c0_1", "Phở Bò Tái Nạm", 45000, R.drawable.img_food_chicken, "Phở bò truyền thống với nước dùng đậm đà"));
                    list.add(new FoodMenuItem("c0_2", "Bún Bò Huế Chả Cua", 55000, R.drawable.img_food_pizza, "Bún bò Huế cay nồng, chả cua dai ngon"));
                    list.add(new FoodMenuItem("c0_3", "Bún Riêu Cua Ốc", 40000, R.drawable.img_food_chicken, "Bún riêu ốc đậu nóng hổi"));
                    list.add(new FoodMenuItem("c0_4", "Hủ Tiếu Nam Vang", 50000, R.drawable.img_food_pizza, "Hủ tiếu tôm thịt, trứng cút"));
                    list.add(new FoodMenuItem("c0_5", "Bún Thịt Nướng", 35000, R.drawable.img_food_chicken, "Bún thịt nướng chả giò"));
                    break;
                case 1:
                    list.add(new FoodMenuItem("c1_1", "Combo Gà Giòn", 89000, R.drawable.img_food_chicken, "2 miếng gà giòn + khoai tây + nước ngọt"));
                    list.add(new FoodMenuItem("c1_2", "Gà Cay Phô Mai", 65000, R.drawable.img_food_chicken, "Gà phủ sốt cay và phô mai chảy"));
                    list.add(new FoodMenuItem("c1_3", "Cơm Gà Xối Mỡ", 45000, R.drawable.img_food_chicken, "Cơm chiên đùi gà góc phần tư"));
                    list.add(new FoodMenuItem("c1_4", "Burger Gà Giòn", 39000, R.drawable.img_food_chicken, "Burger gà kèm rau tươi"));
                    list.add(new FoodMenuItem("c1_5", "Gà Viên Lắc Phô Mai", 40000, R.drawable.img_food_chicken, "Gà viên chiên lắc bột phô mai"));
                    break;
                case 2:
                    list.add(new FoodMenuItem("c2_1", "Trà Sữa Trân Châu", 35000, R.drawable.img_food_bubbletea, "Trà sữa truyền thống, trân châu đen dai"));
                    list.add(new FoodMenuItem("c2_2", "Cà Phê Sữa Đá", 25000, R.drawable.img_food_coffee, "Cà phê phin pha sữa đặc"));
                    list.add(new FoodMenuItem("c2_3", "Trà Đào Cam Sả", 45000, R.drawable.img_food_bubbletea, "Trà đào thơm mát sảng khoái"));
                    list.add(new FoodMenuItem("c2_4", "Sữa Tươi Đường Đen", 40000, R.drawable.img_food_bubbletea, "Sữa tươi dalat milk và đường đen"));
                    list.add(new FoodMenuItem("c2_5", "Sinh Tố Bơ", 45000, R.drawable.img_food_bubbletea, "Sinh tố bơ sáp thơm béo"));
                    break;
                case 3:
                    list.add(new FoodMenuItem("c3_1", "Khoai Tây Chiên", 30000, R.drawable.img_food_pizza, "Khoai tây chiên giòn rắc rong biển"));
                    list.add(new FoodMenuItem("c3_2", "Phô Mai Que", 35000, R.drawable.img_food_pizza, "Phô mai que tẩm bột chiên giòn"));
                    list.add(new FoodMenuItem("c3_3", "Bánh Tráng Trộn", 25000, R.drawable.img_food_pizza, "Bánh tráng trộn khô bò, xoài, trứng cút"));
                    list.add(new FoodMenuItem("c3_4", "Cá Viên Chiên", 30000, R.drawable.img_food_chicken, "Cá viên, tôm viên, bò viên chiên"));
                    break;
                default:
                    list.add(new FoodMenuItem("c_def_1", "Món ăn đang cập nhật", 0, R.drawable.img_food_chicken, "Vui lòng quay lại sau"));
                    break;
            }
            return list;
        }

        public java.util.List<String> getAllCategoryNames() {
            return java.util.Arrays.asList(
                "Tất cả", "Cơm", "Bún Phở", "Bánh mì", "Fastfood", "Lẩu", 
                "Đồ nướng", "Cafe", "Trà sữa", "Ăn vặt", "Bánh ngọt", 
                "Hải sản", "Đồ chay", "Gỏi Cuốn", "Cháo", "Bia rượu"
            );
        }

        public java.util.List<FoodMenuItem> getCategoryFoodsByName(String categoryName) {
            java.util.List<FoodMenuItem> list = new java.util.ArrayList<>();
            if ("Tất cả".equals(categoryName)) {
                // Gom tất cả món ăn từ các danh mục khác lại
                for (String cat : getAllCategoryNames()) {
                    if (!"Tất cả".equals(cat)) {
                        list.addAll(getCategoryFoodsByName(cat));
                    }
                }
                return list;
            }

            // Modular handling for future API integration
            switch (categoryName) {
                case "Cơm": return getCategoryFoods(0);
                case "Bún\nPhở": return getCategoryFoods(0);
                case "Đồ Ăn\nnhanh": return getCategoryFoods(1);
                case "Đồ nướng\nBBQ": return getCategoryFoods(1);
                case "Cà phê\nTrà sữa": return getCategoryFoods(2);
                case "Ăn vặt\nBánh ngọt": return getCategoryFoods(3);
                case "Bánh mì": return getCategoryFoods(3);
                case "Lẩu":
                case "Hải sản":
                default:
                    // Fallback modular mock data generation
                    String nameWithoutNewline = categoryName.replace("\n", " - ");
                    for (int i = 1; i <= 5; i++) {
                        list.add(new FoodMenuItem(
                                "mock_" + categoryName.hashCode() + "_" + i, 
                                "Món " + nameWithoutNewline + " " + i, 
                                40000 + i*10000, 
                                R.drawable.img_food_pizza, 
                                "Mô tả cho món " + nameWithoutNewline
                        ));
                    }
                    return list;
            }
        }

        public java.util.List<com.example.uitpayapp.home.home_models.Restaurant> getRestaurants() {
            java.util.List<com.example.uitpayapp.home.home_models.Restaurant> restaurants = new java.util.ArrayList<>();
            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("KFC", "KFC", android.graphics.Color.parseColor("#E4002B"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r1_1", "Gà rán truyền thống", 45000, R.drawable.img_food_chicken, "1 miếng gà rán giòn"),
                    new FoodMenuItem("r1_2", "Combo gà rán + khoai", 89000, R.drawable.img_food_chicken, "2 miếng gà + khoai tây"),
                    new FoodMenuItem("r1_3", "Burger gà giòn", 39000, R.drawable.img_food_chicken, "Burger gà với rau tươi"),
                    new FoodMenuItem("r1_4", "Cơm gà sốt cay", 55000, R.drawable.img_food_chicken, "Cơm trắng + gà sốt cay")
            ), R.drawable.img_food_chicken, 4.8, 1250, 25, "KFC Võ Văn Ngân, Thủ Đức"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("The Coffee House", "TCH", android.graphics.Color.parseColor("#ED692F"), "Cà phê\nTrà sữa", java.util.Arrays.asList(
                    new FoodMenuItem("r3_1", "Cà phê sữa đá", 35000, R.drawable.img_food_coffee, "Cà phê phin truyền thống"),
                    new FoodMenuItem("r3_2", "Trà đào cam sả", 55000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"),
                    new FoodMenuItem("r3_3", "Trà sen vàng", 45000, R.drawable.img_food_bubbletea, "Trà ướp sen thơm mát"),
                    new FoodMenuItem("r3_4", "Bánh mì chà bông", 25000, R.drawable.img_food_chicken, "Bánh mì nướng giòn")
            ), R.drawable.img_food_coffee, 4.7, 950, 20, "TCH Kha Vạn Cân, Thủ Đức"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Phúc Long", "PL", android.graphics.Color.parseColor("#006241"), "Cà phê\nTrà sữa", java.util.Arrays.asList(
                    new FoodMenuItem("r2_1", "Trà sen vàng", 45000, R.drawable.img_food_bubbletea, "Trà ướp sen thơm mát"),
                    new FoodMenuItem("r2_2", "Trà đào cam sả", 55000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"),
                    new FoodMenuItem("r2_3", "Cà phê sữa đá", 35000, R.drawable.img_food_coffee, "Cà phê phin truyền thống"),
                    new FoodMenuItem("r2_4", "Bánh mì chà bông", 25000, R.drawable.img_food_chicken, "Bánh mì nướng giòn")
            ), R.drawable.img_food_bubbletea, 4.6, 850, 15, "Phúc Long Lê Văn Việt, Q9"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Jollibee", "JB", android.graphics.Color.parseColor("#E31837"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r3_1", "Gà giòn vui vẻ 1 miếng", 35000, R.drawable.img_food_chicken, "Gà giòn đặc biệt"),
                    new FoodMenuItem("r3_2", "Combo Jolly 1", 79000, R.drawable.img_food_chicken, "Gà + cơm + nước"),
                    new FoodMenuItem("r3_3", "Mì Ý sốt bò bằm", 55000, R.drawable.img_food_pizza, "Mì Ý với sốt bò đậm đà"),
                    new FoodMenuItem("r3_4", "Burger Yumm", 49000, R.drawable.img_food_chicken, "Burger bò phô mai")
            ), R.drawable.img_food_chicken, 4.8, 1500, 25, "Jollibee TTTM Vincom Dĩ An"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Highlands\nCoffee", "HC", android.graphics.Color.parseColor("#6F4E37"), "Cà phê\nTrà sữa", java.util.Arrays.asList(
                    new FoodMenuItem("r4_1", "Phin sữa đá", 39000, R.drawable.img_food_coffee, "Cà phê phin sữa đặc"),
                    new FoodMenuItem("r4_2", "Freeze trà xanh", 55000, R.drawable.img_food_bubbletea, "Trà xanh đá xay"),
                    new FoodMenuItem("r4_3", "Bánh mì thịt nguội", 35000, R.drawable.img_food_chicken, "Bánh mì kiểu Việt"),
                    new FoodMenuItem("r4_4", "Freeze sô-cô-la", 55000, R.drawable.img_food_coffee, "Sô-cô-la đá xay kem")
            ), R.drawable.img_food_coffee, 4.5, 750, 15, "Highlands Vincom Thủ Đức"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("TEXAS\nCHICKEN", "TX", android.graphics.Color.parseColor("#FF6900"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r5_1", "Gà rán Texas 1 miếng", 42000, R.drawable.img_food_chicken, "Gà giòn kiểu Texas"),
                    new FoodMenuItem("r5_2", "Combo Texas Big", 99000, R.drawable.img_food_chicken, "3 miếng gà + khoai + nước"),
                    new FoodMenuItem("r5_3", "Burger gà cay", 45000, R.drawable.img_food_chicken, "Burger gà sốt cay"),
                    new FoodMenuItem("r5_4", "Khoai tây chiên", 25000, R.drawable.img_food_pizza, "Khoai tây giòn tan")
            ), R.drawable.img_food_chicken, 4.6, 620, 25, "Texas Chicken Pearl Plaza"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("MAYCHA", "MC", android.graphics.Color.parseColor("#FF69B4"), "Cà phê\nTrà sữa", java.util.Arrays.asList(
                    new FoodMenuItem("r6_1", "Trà sữa truyền thống", 35000, R.drawable.img_food_bubbletea, "Trà sữa trân châu đường đen"),
                    new FoodMenuItem("r6_2", "Trà sữa matcha", 45000, R.drawable.img_food_bubbletea, "Matcha Nhật Bản"),
                    new FoodMenuItem("r6_3", "Trà đào", 40000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"),
                    new FoodMenuItem("r6_4", "Sữa tươi trân châu", 38000, R.drawable.img_food_bubbletea, "Sữa tươi + trân châu đen")
            ), R.drawable.img_food_bubbletea, 4.9, 2100, 15, "MAYCHA Đặng Văn Bi"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Burger\nKing", "BK", android.graphics.Color.parseColor("#FF8C00"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r7_1", "Whopper", 79000, R.drawable.img_food_chicken, "Burger bò nướng lửa cỡ lớn"),
                    new FoodMenuItem("r7_2", "Combo Whopper", 109000, R.drawable.img_food_chicken, "Whopper + khoai + nước"),
                    new FoodMenuItem("r7_3", "Chicken Nuggets 6pc", 45000, R.drawable.img_food_chicken, "6 miếng gà viên chiên"),
                    new FoodMenuItem("r7_4", "Onion Rings", 35000, R.drawable.img_food_pizza, "Hành tây chiên giòn")
            ), R.drawable.img_food_chicken, 4.3, 350, 30, "Burger King Nguyễn Duy Trinh"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Domino's\nPizza", "DP", android.graphics.Color.parseColor("#006491"), "Cơm\nPizza", java.util.Arrays.asList(
                    new FoodMenuItem("r8_1", "Pizza Hải sản Pesto", 169000, R.drawable.img_food_pizza, "Pizza hải sản sốt pesto"),
                    new FoodMenuItem("r8_2", "Pizza Pepperoni", 149000, R.drawable.img_food_pizza, "Pizza pepperoni cổ điển"),
                    new FoodMenuItem("r8_3", "Gà viên phô mai", 59000, R.drawable.img_food_chicken, "Gà viên nhân phô mai"),
                    new FoodMenuItem("r8_4", "Khoai tây xoắn", 49000, R.drawable.img_food_pizza, "Khoai tây xoắn giòn")
            ), R.drawable.img_food_pizza, 4.7, 890, 35, "Domino's Pizza Q2"));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Tous Les\nJours", "TJ", android.graphics.Color.parseColor("#C62828"), "Bánh\nKem", java.util.Arrays.asList(
                    new FoodMenuItem("r9_1", "Bánh kem dâu tây", 189000, R.drawable.img_food_pizza, "Bánh kem tươi vị dâu"),
                    new FoodMenuItem("r9_2", "Bánh mì bơ tỏi", 25000, R.drawable.img_food_chicken, "Bánh mì nướng bơ tỏi giòn"),
                    new FoodMenuItem("r9_3", "Croissant trứng muối", 35000, R.drawable.img_food_chicken, "Croissant nhân trứng muối"),
                    new FoodMenuItem("r9_4", "Bánh su kem", 29000, R.drawable.img_food_bubbletea, "Bánh su kem tươi mát")
            ), R.drawable.img_food_pizza, 4.8, 450, 20, "Tous Les Jours Vincom Thảo Điền"));

            return restaurants;
        }

        public java.util.List<Object[]> getTopics() {
            java.util.List<Object[]> topicPool = new java.util.ArrayList<>();
            topicPool.add(new Object[]{"Xôi", "Nóng hổi, dẻo thơm mỗi sáng", java.util.Arrays.asList(
                    new FoodMenuItem("t1_1", "Xôi loại 1", 25000, R.drawable.img_food_chicken, "Món xôi chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t1_2", "Xôi loại 2", 35000, R.drawable.img_food_chicken, "Món xôi chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t1_3", "Xôi loại 3", 45000, R.drawable.img_food_chicken, "Món xôi chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t1_4", "Xôi loại 4", 55000, R.drawable.img_food_chicken, "Món xôi chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t1_5", "Xôi loại 5", 65000, R.drawable.img_food_chicken, "Món xôi chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Thức ăn khác", "Khám phá đa dạng các món ngon", java.util.Arrays.asList(
                    new FoodMenuItem("t2_1", "Thức ăn khác loại 1", 25000, R.drawable.img_food_pizza, "Món thức ăn khác chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t2_2", "Thức ăn khác loại 2", 35000, R.drawable.img_food_pizza, "Món thức ăn khác chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t2_3", "Thức ăn khác loại 3", 45000, R.drawable.img_food_pizza, "Món thức ăn khác chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t2_4", "Thức ăn khác loại 4", 55000, R.drawable.img_food_pizza, "Món thức ăn khác chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t2_5", "Thức ăn khác loại 5", 65000, R.drawable.img_food_pizza, "Món thức ăn khác chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Trà", "Thanh mát, giải nhiệt ngày hè", java.util.Arrays.asList(
                    new FoodMenuItem("t3_1", "Trà loại 1", 25000, R.drawable.img_food_bubbletea, "Món trà chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t3_2", "Trà loại 2", 35000, R.drawable.img_food_bubbletea, "Món trà chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t3_3", "Trà loại 3", 45000, R.drawable.img_food_bubbletea, "Món trà chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t3_4", "Trà loại 4", 55000, R.drawable.img_food_bubbletea, "Món trà chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t3_5", "Trà loại 5", 65000, R.drawable.img_food_bubbletea, "Món trà chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Sữa", "Dinh dưỡng tuyệt vời cho cơ thể", java.util.Arrays.asList(
                    new FoodMenuItem("t4_1", "Sữa loại 1", 25000, R.drawable.img_food_bubbletea, "Món sữa chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t4_2", "Sữa loại 2", 35000, R.drawable.img_food_bubbletea, "Món sữa chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t4_3", "Sữa loại 3", 45000, R.drawable.img_food_bubbletea, "Món sữa chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t4_4", "Sữa loại 4", 55000, R.drawable.img_food_bubbletea, "Món sữa chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t4_5", "Sữa loại 5", 65000, R.drawable.img_food_bubbletea, "Món sữa chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Nước ngọt", "Giải khát tức thì, bùng nổ sảng khoái", java.util.Arrays.asList(
                    new FoodMenuItem("t5_1", "Nước ngọt loại 1", 25000, R.drawable.img_food_bubbletea, "Món nước ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t5_2", "Nước ngọt loại 2", 35000, R.drawable.img_food_bubbletea, "Món nước ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t5_3", "Nước ngọt loại 3", 45000, R.drawable.img_food_bubbletea, "Món nước ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t5_4", "Nước ngọt loại 4", 55000, R.drawable.img_food_bubbletea, "Món nước ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t5_5", "Nước ngọt loại 5", 65000, R.drawable.img_food_bubbletea, "Món nước ngọt chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Nước ép trái cây - Sinh tố", "Tươi ngon, bổ sung vitamin", java.util.Arrays.asList(
                    new FoodMenuItem("t6_1", "Nước ép trái cây - Sinh tố loại 1", 25000, R.drawable.img_food_bubbletea, "Món nước ép trái cây - sinh tố chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t6_2", "Nước ép trái cây - Sinh tố loại 2", 35000, R.drawable.img_food_bubbletea, "Món nước ép trái cây - sinh tố chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t6_3", "Nước ép trái cây - Sinh tố loại 3", 45000, R.drawable.img_food_bubbletea, "Món nước ép trái cây - sinh tố chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t6_4", "Nước ép trái cây - Sinh tố loại 4", 55000, R.drawable.img_food_bubbletea, "Món nước ép trái cây - sinh tố chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t6_5", "Nước ép trái cây - Sinh tố loại 5", 65000, R.drawable.img_food_bubbletea, "Món nước ép trái cây - sinh tố chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Mì ăn liền", "Nhanh gọn, cứu đói đêm khuya", java.util.Arrays.asList(
                    new FoodMenuItem("t7_1", "Mì ăn liền loại 1", 25000, R.drawable.img_food_pizza, "Món mì ăn liền chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t7_2", "Mì ăn liền loại 2", 35000, R.drawable.img_food_pizza, "Món mì ăn liền chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t7_3", "Mì ăn liền loại 3", 45000, R.drawable.img_food_pizza, "Món mì ăn liền chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t7_4", "Mì ăn liền loại 4", 55000, R.drawable.img_food_pizza, "Món mì ăn liền chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t7_5", "Mì ăn liền loại 5", 65000, R.drawable.img_food_pizza, "Món mì ăn liền chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Lẩu", "Quây quần bên bạn bè, ấm áp mùa đông", java.util.Arrays.asList(
                    new FoodMenuItem("t8_1", "Lẩu loại 1", 25000, R.drawable.img_food_pizza, "Món lẩu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t8_2", "Lẩu loại 2", 35000, R.drawable.img_food_pizza, "Món lẩu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t8_3", "Lẩu loại 3", 45000, R.drawable.img_food_pizza, "Món lẩu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t8_4", "Lẩu loại 4", 55000, R.drawable.img_food_pizza, "Món lẩu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t8_5", "Lẩu loại 5", 65000, R.drawable.img_food_pizza, "Món lẩu chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Hải sản", "Tôm cua cá mực, tươi ngon mỗi ngày", java.util.Arrays.asList(
                    new FoodMenuItem("t9_1", "Hải sản loại 1", 25000, R.drawable.img_food_pizza, "Món hải sản chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t9_2", "Hải sản loại 2", 35000, R.drawable.img_food_pizza, "Món hải sản chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t9_3", "Hải sản loại 3", 45000, R.drawable.img_food_pizza, "Món hải sản chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t9_4", "Hải sản loại 4", 55000, R.drawable.img_food_pizza, "Món hải sản chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t9_5", "Hải sản loại 5", 65000, R.drawable.img_food_pizza, "Món hải sản chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Fastfood", "Tiện lợi, nhanh chóng, ngon miệng", java.util.Arrays.asList(
                    new FoodMenuItem("t10_1", "Fastfood loại 1", 25000, R.drawable.img_food_chicken, "Món fastfood chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t10_2", "Fastfood loại 2", 35000, R.drawable.img_food_chicken, "Món fastfood chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t10_3", "Fastfood loại 3", 45000, R.drawable.img_food_chicken, "Món fastfood chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t10_4", "Fastfood loại 4", 55000, R.drawable.img_food_chicken, "Món fastfood chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t10_5", "Fastfood loại 5", 65000, R.drawable.img_food_chicken, "Món fastfood chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Đồ nướng", "Xèo xèo thơm lừng, đậm đà gia vị", java.util.Arrays.asList(
                    new FoodMenuItem("t11_1", "Đồ nướng loại 1", 25000, R.drawable.img_food_chicken, "Món đồ nướng chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t11_2", "Đồ nướng loại 2", 35000, R.drawable.img_food_chicken, "Món đồ nướng chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t11_3", "Đồ nướng loại 3", 45000, R.drawable.img_food_chicken, "Món đồ nướng chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t11_4", "Đồ nướng loại 4", 55000, R.drawable.img_food_chicken, "Món đồ nướng chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t11_5", "Đồ nướng loại 5", 65000, R.drawable.img_food_chicken, "Món đồ nướng chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Đồ chay", "Thanh tịnh, nhẹ nhàng, tốt cho sức khỏe", java.util.Arrays.asList(
                    new FoodMenuItem("t12_1", "Đồ chay loại 1", 25000, R.drawable.img_food_pizza, "Món đồ chay chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t12_2", "Đồ chay loại 2", 35000, R.drawable.img_food_pizza, "Món đồ chay chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t12_3", "Đồ chay loại 3", 45000, R.drawable.img_food_pizza, "Món đồ chay chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t12_4", "Đồ chay loại 4", 55000, R.drawable.img_food_pizza, "Món đồ chay chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t12_5", "Đồ chay loại 5", 65000, R.drawable.img_food_pizza, "Món đồ chay chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Gỏi - Cuốn - Salad", "Tươi mát, healthy, đầy đủ dưỡng chất", java.util.Arrays.asList(
                    new FoodMenuItem("t13_1", "Gỏi - Cuốn - Salad loại 1", 25000, R.drawable.img_food_pizza, "Món gỏi - cuốn - salad chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t13_2", "Gỏi - Cuốn - Salad loại 2", 35000, R.drawable.img_food_pizza, "Món gỏi - cuốn - salad chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t13_3", "Gỏi - Cuốn - Salad loại 3", 45000, R.drawable.img_food_pizza, "Món gỏi - cuốn - salad chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t13_4", "Gỏi - Cuốn - Salad loại 4", 55000, R.drawable.img_food_pizza, "Món gỏi - cuốn - salad chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t13_5", "Gỏi - Cuốn - Salad loại 5", 65000, R.drawable.img_food_pizza, "Món gỏi - cuốn - salad chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Cơm", "Bữa cơm ấm bụng, giá cả phải chăng", java.util.Arrays.asList(
                    new FoodMenuItem("t14_1", "Cơm loại 1", 25000, R.drawable.img_food_chicken, "Món cơm chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t14_2", "Cơm loại 2", 35000, R.drawable.img_food_chicken, "Món cơm chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t14_3", "Cơm loại 3", 45000, R.drawable.img_food_chicken, "Món cơm chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t14_4", "Cơm loại 4", 55000, R.drawable.img_food_chicken, "Món cơm chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t14_5", "Cơm loại 5", 65000, R.drawable.img_food_chicken, "Món cơm chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Cháo", "Nóng hổi, dễ tiêu, bổ dưỡng", java.util.Arrays.asList(
                    new FoodMenuItem("t15_1", "Cháo loại 1", 25000, R.drawable.img_food_chicken, "Món cháo chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t15_2", "Cháo loại 2", 35000, R.drawable.img_food_chicken, "Món cháo chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t15_3", "Cháo loại 3", 45000, R.drawable.img_food_chicken, "Món cháo chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t15_4", "Cháo loại 4", 55000, R.drawable.img_food_chicken, "Món cháo chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t15_5", "Cháo loại 5", 65000, R.drawable.img_food_chicken, "Món cháo chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Cafe", "Đánh thức tinh thần, bắt đầu ngày mới", java.util.Arrays.asList(
                    new FoodMenuItem("t16_1", "Cafe loại 1", 25000, R.drawable.img_food_coffee, "Món cafe chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t16_2", "Cafe loại 2", 35000, R.drawable.img_food_coffee, "Món cafe chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t16_3", "Cafe loại 3", 45000, R.drawable.img_food_coffee, "Món cafe chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t16_4", "Cafe loại 4", 55000, R.drawable.img_food_coffee, "Món cafe chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t16_5", "Cafe loại 5", 65000, R.drawable.img_food_coffee, "Món cafe chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Bún - Phở - Hủ tiếu", "Top món ăn truyền thống được yêu thích", java.util.Arrays.asList(
                    new FoodMenuItem("t17_1", "Bún - Phở - Hủ tiếu loại 1", 25000, R.drawable.img_food_chicken, "Món bún - phở - hủ tiếu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t17_2", "Bún - Phở - Hủ tiếu loại 2", 35000, R.drawable.img_food_chicken, "Món bún - phở - hủ tiếu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t17_3", "Bún - Phở - Hủ tiếu loại 3", 45000, R.drawable.img_food_chicken, "Món bún - phở - hủ tiếu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t17_4", "Bún - Phở - Hủ tiếu loại 4", 55000, R.drawable.img_food_chicken, "Món bún - phở - hủ tiếu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t17_5", "Bún - Phở - Hủ tiếu loại 5", 65000, R.drawable.img_food_chicken, "Món bún - phở - hủ tiếu chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Bia rượu", "Chill cùng bạn bè ngày cuối tuần", java.util.Arrays.asList(
                    new FoodMenuItem("t18_1", "Bia rượu loại 1", 25000, R.drawable.img_food_bubbletea, "Món bia rượu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t18_2", "Bia rượu loại 2", 35000, R.drawable.img_food_bubbletea, "Món bia rượu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t18_3", "Bia rượu loại 3", 45000, R.drawable.img_food_bubbletea, "Món bia rượu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t18_4", "Bia rượu loại 4", 55000, R.drawable.img_food_bubbletea, "Món bia rượu chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t18_5", "Bia rượu loại 5", 65000, R.drawable.img_food_bubbletea, "Món bia rượu chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Bánh mì", "Ổ bánh mì nóng giòn, đậm đà hương vị", java.util.Arrays.asList(
                    new FoodMenuItem("t19_1", "Bánh mì loại 1", 25000, R.drawable.img_food_chicken, "Món bánh mì chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t19_2", "Bánh mì loại 2", 35000, R.drawable.img_food_chicken, "Món bánh mì chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t19_3", "Bánh mì loại 3", 45000, R.drawable.img_food_chicken, "Món bánh mì chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t19_4", "Bánh mì loại 4", 55000, R.drawable.img_food_chicken, "Món bánh mì chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t19_5", "Bánh mì loại 5", 65000, R.drawable.img_food_chicken, "Món bánh mì chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Bánh ngọt", "Ngọt ngào, xoa dịu mọi muộn phiền", java.util.Arrays.asList(
                    new FoodMenuItem("t20_1", "Bánh ngọt loại 1", 25000, R.drawable.img_food_bubbletea, "Món bánh ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t20_2", "Bánh ngọt loại 2", 35000, R.drawable.img_food_bubbletea, "Món bánh ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t20_3", "Bánh ngọt loại 3", 45000, R.drawable.img_food_bubbletea, "Món bánh ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t20_4", "Bánh ngọt loại 4", 55000, R.drawable.img_food_bubbletea, "Món bánh ngọt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t20_5", "Bánh ngọt loại 5", 65000, R.drawable.img_food_bubbletea, "Món bánh ngọt chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Bánh bao", "Mềm xốp, nhân đậm đà", java.util.Arrays.asList(
                    new FoodMenuItem("t21_1", "Bánh bao loại 1", 25000, R.drawable.img_food_chicken, "Món bánh bao chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t21_2", "Bánh bao loại 2", 35000, R.drawable.img_food_chicken, "Món bánh bao chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t21_3", "Bánh bao loại 3", 45000, R.drawable.img_food_chicken, "Món bánh bao chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t21_4", "Bánh bao loại 4", 55000, R.drawable.img_food_chicken, "Món bánh bao chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t21_5", "Bánh bao loại 5", 65000, R.drawable.img_food_chicken, "Món bánh bao chuẩn vị, ngon miệng")
            )});
            topicPool.add(new Object[]{"Ăn vặt", "Món ngon đường phố, nhớ mãi không quên", java.util.Arrays.asList(
                    new FoodMenuItem("t22_1", "Ăn vặt loại 1", 25000, R.drawable.img_food_pizza, "Món ăn vặt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t22_2", "Ăn vặt loại 2", 35000, R.drawable.img_food_pizza, "Món ăn vặt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t22_3", "Ăn vặt loại 3", 45000, R.drawable.img_food_pizza, "Món ăn vặt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t22_4", "Ăn vặt loại 4", 55000, R.drawable.img_food_pizza, "Món ăn vặt chuẩn vị, ngon miệng"),
                    new FoodMenuItem("t22_5", "Ăn vặt loại 5", 65000, R.drawable.img_food_pizza, "Món ăn vặt chuẩn vị, ngon miệng")
            )});

            return topicPool;
        }


        public java.util.List<com.example.uitpayapp.recommendeddeal.RecommendedDealModel> getRecommendedDeals() {
            java.util.List<com.example.uitpayapp.recommendeddeal.RecommendedDealModel> allDeals = new java.util.ArrayList<>();
            allDeals.add(new com.example.uitpayapp.recommendeddeal.RecommendedDealModel(
                    "Gà Rán Popeyes - Võ Văn Ngân",
                    9.1, 9,
                    R.drawable.img_food_chicken,
                    "-52%",
                    "1 MIẾNG GÀ RÁN GIÒN + 1 GÀ POPCORN + 1 KHOAI TÂY CHIÊN",
                    100,
                    118000.0,
                    57000.0
            ));
            
            allDeals.add(new com.example.uitpayapp.recommendeddeal.RecommendedDealModel(
                    "The Coffee House - Kha Vạn Cân",
                    1.2, 10,
                    R.drawable.img_food_bubbletea,
                    "-30%",
                    "Trà Đào Cam Sả (L) + Bánh Mì Que",
                    50,
                    75000.0,
                    52000.0
            ));

            allDeals.add(new com.example.uitpayapp.recommendeddeal.RecommendedDealModel(
                    "Phúc Long Tea & Coffee",
                    3.5, 25,
                    R.drawable.img_food_coffee,
                    "-20%",
                    "Trà Sữa Phúc Long + Thạch Cafe",
                    200,
                    65000.0,
                    52000.0
            ));

            allDeals.add(new com.example.uitpayapp.recommendeddeal.RecommendedDealModel(
                    "KFC - Đặng Văn Bi",
                    0.5, 10,
                    R.drawable.img_food_chicken,
                    "-15%",
                    "Combo Gà Rán Hạnh Phúc",
                    80,
                    150000.0,
                    125000.0
            ));
            return allDeals;
        }
    }
}
