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
import com.example.uitpayapp.home.home_adapters.TopicStoreAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.home_models.Restaurant;
import com.example.uitpayapp.home.home_models.TopicStore;
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

public class HomeActivity extends AppCompatActivity {

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
            "Nhà C, Thủ Đức, Hồ Chí Minh",
            "KTX Khu B, ĐHQG, Thủ Đức",
            "Phòng Lab, Tòa E, ĐH CNTT",
            "Căn tin ĐH CNTT, Thủ Đức",
            "Số 1 Võ Văn Ngân, Thủ Đức"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

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

        setupRestaurants();
        setupCategories();
        setupSearch();
        setupTopics();
        setupDeals();
        setupStickyTab();
        setupBottomNavigation();
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

        LinearLayout llWallet = view.findViewById(R.id.btn_dest_wallet);
        LinearLayout llSaving = view.findViewById(R.id.btn_dest_saving);

        ((TextView) llWallet.getChildAt(1)).setText(ADDRESSES[0]);
        ((TextView) llSaving.getChildAt(1)).setText(ADDRESSES[1]);

        llWallet.setOnClickListener(v -> {
            tvDeliveryAddress.setText(ADDRESSES[0]);
            dialog.dismiss();
            Toast.makeText(this, "Giao tới: " + ADDRESSES[0], Toast.LENGTH_SHORT).show();
        });

        llSaving.setOnClickListener(v -> {
            tvDeliveryAddress.setText(ADDRESSES[1]);
            dialog.dismiss();
            Toast.makeText(this, "Giao tới: " + ADDRESSES[1], Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                applyFilters();
            }
        });
    }

    private void setupCategories() {
        List<FoodCategory> categories = new ArrayList<>();
        categories.add(new FoodCategory("Cơm", R.drawable.ic_cat_com, Color.parseColor("#E65100")));
        categories.add(new FoodCategory("Bún\nPhở", R.drawable.ic_cat_bun_pho, Color.parseColor("#00838F")));
        categories.add(new FoodCategory("Bánh mì", R.drawable.ic_cat_banh_mi, Color.parseColor("#BF360C")));
        categories.add(new FoodCategory("Đồ Ăn\nnhanh", R.drawable.ic_cat_fastfood, Color.parseColor("#C62828")));
        categories.add(new FoodCategory("Lẩu", R.drawable.ic_cat_lau, Color.parseColor("#D84315")));
        categories.add(new FoodCategory("Đồ nướng\nBBQ", R.drawable.ic_cat_bbq, Color.parseColor("#B71C1C")));
        categories.add(new FoodCategory("Cà phê\nTrà sữa", R.drawable.ic_cat_ca_phe, Color.parseColor("#4E342E")));
        categories.add(new FoodCategory("Ăn vặt\nBánh ngọt", R.drawable.ic_cat_an_vat, Color.parseColor("#6A1B9A")));
        categories.add(new FoodCategory("Hải sản", R.drawable.ic_cat_hai_san, Color.parseColor("#1B5E20")));
        categories.add(new FoodCategory("Tất cả", R.drawable.ic_cat_all, Color.parseColor("#283593"), true));

        RecyclerView rv = findViewById(R.id.rv_categories);
        rv.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(
                this, 2, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false));
        categoryAdapter = new FoodCategoryAdapter(categories, category -> {
            if (category == null) {
                selectedCategory = null;
            } else {
                selectedCategory = category.getName();
            }
            applyFilters();
        });
        rv.setAdapter(categoryAdapter);
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
        int[] sectionIds = {R.id.topic_section_1, R.id.topic_section_2, R.id.topic_section_3, R.id.topic_section_4};
        for (int i = 0; i < 4; i++) {
            Object[] topic = topicPool.get(i);
            @SuppressWarnings("unchecked")
            List<FoodMenuItem> foods = (List<FoodMenuItem>) topic[2];
            setupTopicSection(findViewById(sectionIds[i]), (String) topic[0], (String) topic[1], foods);
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
            Intent intent = new Intent(this, RecommendedDealActivity.class);
            startActivity(intent);
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
            CartManager.getInstance().addItem(new CartItem(item, popupQty[0], new java.util.ArrayList<>(selectedToppings)));
            
            View btnCart = findViewById(R.id.btn_cart);
            CartAnimationHelper.animateFlyToCart(this, ivFoodImage, btnCart, () -> {
                updateGlobalCartBadge();
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
        dealItems.clear();
        dealItemCount = 0;
        nextBannerAt = 5 + bannerRandom.nextInt(3); // 5-7
        homeDealAdapter.notifyDataSetChanged();
        isLoadingMore = false;
        loadMoreDeals();
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    private void loadMoreDeals() {
        if (isLoadingMore) return;
        isLoadingMore = true;

        // Add loading indicator
        dealItems.add(null);
        homeDealAdapter.notifyItemInserted(dealItems.size() - 1);

        int tabIndex = tabHomeDeals.getSelectedTabPosition();
        int delay = 800 + bannerRandom.nextInt(700); // 800-1500ms

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Remove loading indicator
            int loadingPos = dealItems.indexOf(null);
            if (loadingPos >= 0) {
                dealItems.remove(loadingPos);
                homeDealAdapter.notifyItemRemoved(loadingPos);
            }

            // Generate 5 fake deals
            List<RecommendedDealModel> newDeals = FakeDealGenerator.generateDeals(5, tabIndex);

            for (RecommendedDealModel deal : newDeals) {
                dealItems.add(deal);
                dealItemCount++;

                // Insert banner every 5-7 deal items
                if (dealItemCount >= nextBannerAt) {
                    dealItems.add(HomeDealAdapter.BannerItem.random());
                    nextBannerAt = dealItemCount + 5 + bannerRandom.nextInt(3);
                }
            }

            homeDealAdapter.notifyDataSetChanged();
            isLoadingMore = false;
        }, delay);
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
                    loadMoreDeals();
                }
            }
        });
    }

    private void showRestaurantMenu(Restaurant restaurant) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_restaurant_menu, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        ((TextView) view.findViewById(R.id.tv_restaurant_name)).setText(restaurant.getName().replace("\n", " "));
        view.findViewById(R.id.btn_close_menu).setOnClickListener(v -> dialog.dismiss());

        RecyclerView rvMenu = view.findViewById(R.id.rv_menu_items);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        View layoutCartSummary = view.findViewById(R.id.layout_cart_summary);
        TextView tvCartCount = view.findViewById(R.id.tv_cart_count);
        TextView tvCartTotal = view.findViewById(R.id.tv_cart_total);
        TextView btnOrder = (TextView) view.findViewById(R.id.btn_order);
        btnOrder.setText("ThĂªm vĂ o giá»");

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        FoodMenuAdapter menuAdapter = new FoodMenuAdapter(restaurant.getMenu(), cart -> {
            if (cart.isEmpty()) {
                layoutCartSummary.setVisibility(View.GONE);
            } else {
                layoutCartSummary.setVisibility(View.VISIBLE);
                int totalItems = 0;
                long totalPrice = 0;
                for (CartItem ci : cart) {
                    totalItems += ci.getQuantity();
                    totalPrice += ci.getTotalPrice();
                }
                tvCartCount.setText(totalItems + " mĂ³n");
                tvCartTotal.setText(formatter.format(totalPrice) + "Ä‘");
            }
        });

        rvMenu.setAdapter(menuAdapter);

        btnOrder.setOnClickListener(v -> {
            List<CartItem> cart = menuAdapter.getCart();
            if (cart.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 món", Toast.LENGTH_SHORT).show();
                return;
            }

            for (CartItem item : cart) {
                CartManager.getInstance().addItem(item);
            }

            updateGlobalCartBadge();
            dialog.dismiss();
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGlobalCartBadge();
    }

    private void updateGlobalCartBadge() {
        TextView tvBadge = findViewById(R.id.tv_global_cart_badge);
        int count = CartManager.getInstance().getTotalItemCount();
        if (count > 0) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(String.valueOf(count));
        } else {
            tvBadge.setVisibility(View.GONE);
        }
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

        public java.util.List<com.example.uitpayapp.home.home_models.Restaurant> getRestaurants() {
            java.util.List<com.example.uitpayapp.home.home_models.Restaurant> restaurants = new java.util.ArrayList<>();
            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("KFC", "KFC", android.graphics.Color.parseColor("#E4002B"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r1_1", "Gà rán truyền thống", 45000, R.drawable.img_food_chicken, "1 miếng gà rán giòn"),
                    new FoodMenuItem("r1_2", "Combo gà rán + khoai", 89000, R.drawable.img_food_chicken, "2 miếng gà + khoai tây"),
                    new FoodMenuItem("r1_3", "Burger gà giòn", 39000, R.drawable.img_food_chicken, "Burger gà với rau tươi"),
                    new FoodMenuItem("r1_4", "Cơm gà sốt cay", 55000, R.drawable.img_food_chicken, "Cơm trắng + gà sốt cay")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Phúc Long", "PL", android.graphics.Color.parseColor("#006241"), "Cà phê\nTrà sữa", java.util.Arrays.asList(
                    new FoodMenuItem("r2_1", "Trà sen vàng", 45000, R.drawable.img_food_bubbletea, "Trà ướp sen thơm mát"),
                    new FoodMenuItem("r2_2", "Trà đào cam sả", 55000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"),
                    new FoodMenuItem("r2_3", "Cà phê sữa đá", 35000, R.drawable.img_food_coffee, "Cà phê phin truyền thống"),
                    new FoodMenuItem("r2_4", "Bánh mì chà bông", 25000, R.drawable.img_food_chicken, "Bánh mì nướng giòn")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Jollibee", "JB", android.graphics.Color.parseColor("#E31837"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r3_1", "Gà giòn vui vẻ 1 miếng", 35000, R.drawable.img_food_chicken, "Gà giòn đặc biệt"),
                    new FoodMenuItem("r3_2", "Combo Jolly 1", 79000, R.drawable.img_food_chicken, "Gà + cơm + nước"),
                    new FoodMenuItem("r3_3", "Mì Ý sốt bò bằm", 55000, R.drawable.img_food_pizza, "Mì Ý với sốt bò đậm đà"),
                    new FoodMenuItem("r3_4", "Burger Yumm", 49000, R.drawable.img_food_chicken, "Burger bò phô mai")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Highlands\nCoffee", "HC", android.graphics.Color.parseColor("#6F4E37"), "Cà phê\nTrà sữa", java.util.Arrays.asList(
                    new FoodMenuItem("r4_1", "Phin sữa đá", 39000, R.drawable.img_food_coffee, "Cà phê phin sữa đặc"),
                    new FoodMenuItem("r4_2", "Freeze trà xanh", 55000, R.drawable.img_food_bubbletea, "Trà xanh đá xay"),
                    new FoodMenuItem("r4_3", "Bánh mì thịt nguội", 35000, R.drawable.img_food_chicken, "Bánh mì kiểu Việt"),
                    new FoodMenuItem("r4_4", "Freeze sô-cô-la", 55000, R.drawable.img_food_coffee, "Sô-cô-la đá xay kem")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("TEXAS\nCHICKEN", "TX", android.graphics.Color.parseColor("#FF6900"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r5_1", "Gà rán Texas 1 miếng", 42000, R.drawable.img_food_chicken, "Gà giòn kiểu Texas"),
                    new FoodMenuItem("r5_2", "Combo Texas Big", 99000, R.drawable.img_food_chicken, "3 miếng gà + khoai + nước"),
                    new FoodMenuItem("r5_3", "Burger gà cay", 45000, R.drawable.img_food_chicken, "Burger gà sốt cay"),
                    new FoodMenuItem("r5_4", "Khoai tây chiên", 25000, R.drawable.img_food_pizza, "Khoai tây giòn tan")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("MAYCHA", "MC", android.graphics.Color.parseColor("#FF69B4"), "Cà phê\nTrà sữa", java.util.Arrays.asList(
                    new FoodMenuItem("r6_1", "Trà sữa truyền thống", 35000, R.drawable.img_food_bubbletea, "Trà sữa trân châu đường đen"),
                    new FoodMenuItem("r6_2", "Trà sữa matcha", 45000, R.drawable.img_food_bubbletea, "Matcha Nhật Bản"),
                    new FoodMenuItem("r6_3", "Trà đào", 40000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"),
                    new FoodMenuItem("r6_4", "Sữa tươi trân châu", 38000, R.drawable.img_food_bubbletea, "Sữa tươi + trân châu đen")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Burger\nKing", "BK", android.graphics.Color.parseColor("#FF8C00"), "Gà rán\nBurger", java.util.Arrays.asList(
                    new FoodMenuItem("r7_1", "Whopper", 79000, R.drawable.img_food_chicken, "Burger bò nướng lửa cỡ lớn"),
                    new FoodMenuItem("r7_2", "Combo Whopper", 109000, R.drawable.img_food_chicken, "Whopper + khoai + nước"),
                    new FoodMenuItem("r7_3", "Chicken Nuggets 6pc", 45000, R.drawable.img_food_chicken, "6 miếng gà viên chiên"),
                    new FoodMenuItem("r7_4", "Onion Rings", 35000, R.drawable.img_food_pizza, "Hành tây chiên giòn")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Domino's\nPizza", "DP", android.graphics.Color.parseColor("#006491"), "Cơm\nPizza", java.util.Arrays.asList(
                    new FoodMenuItem("r8_1", "Pizza Hải sản Pesto", 169000, R.drawable.img_food_pizza, "Pizza hải sản sốt pesto"),
                    new FoodMenuItem("r8_2", "Pizza Pepperoni", 149000, R.drawable.img_food_pizza, "Pizza pepperoni cổ điển"),
                    new FoodMenuItem("r8_3", "Gà viên phô mai", 59000, R.drawable.img_food_chicken, "Gà viên nhân phô mai"),
                    new FoodMenuItem("r8_4", "Khoai tây xoắn", 49000, R.drawable.img_food_pizza, "Khoai tây xoắn giòn")
            )));

            restaurants.add(new com.example.uitpayapp.home.home_models.Restaurant("Tous Les\nJours", "TJ", android.graphics.Color.parseColor("#C62828"), "Bánh\nKem", java.util.Arrays.asList(
                    new FoodMenuItem("r9_1", "Bánh kem dâu tây", 189000, R.drawable.img_food_pizza, "Bánh kem tươi vị dâu"),
                    new FoodMenuItem("r9_2", "Bánh mì bơ tỏi", 25000, R.drawable.img_food_chicken, "Bánh mì nướng bơ tỏi giòn"),
                    new FoodMenuItem("r9_3", "Croissant trứng muối", 35000, R.drawable.img_food_chicken, "Croissant nhân trứng muối"),
                    new FoodMenuItem("r9_4", "Bánh su kem", 29000, R.drawable.img_food_bubbletea, "Bánh su kem tươi mát")
            )));

            return restaurants;
        }

        public java.util.List<Object[]> getTopics() {
            java.util.List<Object[]> topicPool = new java.util.ArrayList<>();
            topicPool.add(new Object[]{"Bún Phở Hội Tụ", "Top món bún phở được yêu thích nhất!", java.util.Arrays.asList(
                    new FoodMenuItem("t1_1", "Phở Bò Tái Nạm", 45000, R.drawable.img_food_chicken, "Phở bò truyền thống với nước dùng đậm đà"),
                    new FoodMenuItem("t1_2", "Bún Bò Huế Chả Cua", 55000, R.drawable.img_food_pizza, "Bún bò Huế cay nồng, chả cua dai ngon"),
                    new FoodMenuItem("t1_3", "Bún Riêu Cua Ốc", 40000, R.drawable.img_food_chicken, "Bún riêu ốc đậu nóng hổi"),
                    new FoodMenuItem("t1_4", "Hủ Tiếu Nam Vang", 50000, R.drawable.img_food_pizza, "Hủ tiếu tôm thịt, trứng cút"),
                    new FoodMenuItem("t1_5", "Bún Thịt Nướng", 35000, R.drawable.img_food_chicken, "Bún thịt nướng chả giò"))});
            topicPool.add(new Object[]{"Gà Rán Chất Lượng", "Giòn tan, thơm lừng – đậm đà vị gà!", java.util.Arrays.asList(
                    new FoodMenuItem("t2_1", "Combo Gà Giòn", 89000, R.drawable.img_food_chicken, "2 miếng gà giòn + khoai tây + nước ngọt"),
                    new FoodMenuItem("t2_2", "Gà Cay Phô Mai", 65000, R.drawable.img_food_chicken, "Gà phủ sốt cay và phô mai chảy"),
                    new FoodMenuItem("t2_3", "Cơm Gà Xối Mỡ", 45000, R.drawable.img_food_chicken, "Cơm chiên đùi gà góc phần tư"),
                    new FoodMenuItem("t2_4", "Burger Gà Giòn", 39000, R.drawable.img_food_chicken, "Burger gà kèm rau tươi"),
                    new FoodMenuItem("t2_5", "Gà Viên Lắc Phô Mai", 40000, R.drawable.img_food_chicken, "Gà viên chiên lắc bột phô mai"))});
            topicPool.add(new Object[]{"Cà Phê & Trà Sữa", "Nạp năng lượng, thưởng thức từng giọt!", java.util.Arrays.asList(
                    new FoodMenuItem("t3_1", "Trà Sữa Trân Châu", 35000, R.drawable.img_food_bubbletea, "Trà sữa truyền thống, trân châu đen dai"),
                    new FoodMenuItem("t3_2", "Cà Phê Sữa Đá", 25000, R.drawable.img_food_coffee, "Cà phê phin pha sữa đặc"),
                    new FoodMenuItem("t3_3", "Trà Đào Cam Sả", 45000, R.drawable.img_food_bubbletea, "Trà đào thơm mát sảng khoái"),
                    new FoodMenuItem("t3_4", "Sữa Tươi Đường Đen", 40000, R.drawable.img_food_bubbletea, "Sữa tươi dalat milk và đường đen"),
                    new FoodMenuItem("t3_5", "Bạc Xỉu Lắc", 30000, R.drawable.img_food_coffee, "Bạc xỉu ba tầng thơm béo"))});
            topicPool.add(new Object[]{"Ăn Vặt Đường Phố", "Món ngon vừa hè, nhớ mãi không quên!", java.util.Arrays.asList(
                    new FoodMenuItem("t4_1", "Bánh Tráng Trộn", 20000, R.drawable.img_food_pizza, "Bánh tráng, bò khô, trứng cút, mỡ hành"),
                    new FoodMenuItem("t4_2", "Cá Viên Chiên Mắm", 35000, R.drawable.img_food_chicken, "Cá viên thập cẩm chiên nước mắm"),
                    new FoodMenuItem("t4_3", "Takoyaki Bạch Tuộc", 40000, R.drawable.img_food_pizza, "Bánh bạch tuộc nướng Nhật Bản"),
                    new FoodMenuItem("t4_4", "Xúc Xích Phô Mai", 15000, R.drawable.img_food_chicken, "Xúc xích Đức bọc phô mai"),
                    new FoodMenuItem("t4_5", "Khoai Lang Lắc Xí Muội", 25000, R.drawable.img_food_chicken, "Khoai lang chiên lắc xí muội"))});
            topicPool.add(new Object[]{"Cơm Ngon Mỗi Ngày", "Bữa cơm ấm bụng, giá cả phải chăng!", java.util.Arrays.asList(
                    new FoodMenuItem("t5_1", "Cơm Tấm Sườn Bì Cả", 55000, R.drawable.img_food_chicken, "Sườn nướng than hoa, bì chả thơm lừng"),
                    new FoodMenuItem("t5_2", "Cơm Gà Hải Nam", 60000, R.drawable.img_food_chicken, "Gà luộc mềm ngọt, cơm nấu nước luộc gà"),
                    new FoodMenuItem("t5_3", "Cơm Chiên Dương Châu", 45000, R.drawable.img_food_pizza, "Cơm chiên thập cẩm tôm lạp xưởng"),
                    new FoodMenuItem("t5_4", "Cơm Thố Xá Xíu", 50000, R.drawable.img_food_chicken, "Cơm thố nóng hổi thịt xá xíu đậm đà"),
                    new FoodMenuItem("t5_5", "Cơm Gà Rán Sốt Cay", 45000, R.drawable.img_food_chicken, "Cơm đùi gà chiên giòn xốt cay Hàn Quốc"))});
            topicPool.add(new Object[]{"Lẩu Quây Quần", "Quây quần bên bạn bè, ấm áp mùa đông!", java.util.Arrays.asList(
                    new FoodMenuItem("t6_1", "Set Lẩu Thái Hải Sản", 250000, R.drawable.img_food_pizza, "Lẩu Thái chua cay tôm mực nghêu"),
                    new FoodMenuItem("t6_2", "Lẩu Bò Nhúng Dấm", 200000, R.drawable.img_food_pizza, "Bò Mỹ nhúng dấm chua thanh, cuốn bánh tráng"),
                    new FoodMenuItem("t6_3", "Lẩu Gà Lá É", 180000, R.drawable.img_food_chicken, "Gà ta thả vườn nấu lá é Đà Lạt"),
                    new FoodMenuItem("t6_4", "Set Lẩu Nấm Chay", 150000, R.drawable.img_food_pizza, "Thanh đạm với nấm đùi gà, kim châm"),
                    new FoodMenuItem("t6_5", "Lẩu Ếch Măng Cay", 190000, R.drawable.img_food_chicken, "Ếch xào măng cay nồng nhúng lẩu"))});
            topicPool.add(new Object[]{"Bánh Mì Sài Gòn", "Ổ bánh mì nóng giòn, đậm đà hương vị!", java.util.Arrays.asList(
                    new FoodMenuItem("t7_1", "Bánh Mì Heo Quay", 25000, R.drawable.img_food_chicken, "Heo quay bì giòn, nước sốt đậm đà"),
                    new FoodMenuItem("t7_2", "Bánh Mì Chả Lụa Pâté", 20000, R.drawable.img_food_chicken, "Pâté gan béo ngậy, chả lụa lá chuối"),
                    new FoodMenuItem("t7_3", "Bánh Mì Trứng Ốp La", 15000, R.drawable.img_food_pizza, "Trứng lòng đào béo ngậy ăn kèm rau dưa"),
                    new FoodMenuItem("t7_4", "Bánh Mì Chảo", 45000, R.drawable.img_food_pizza, "Chảo xíu mại trứng pate xúc xích nóng hổi"),
                    new FoodMenuItem("t7_5", "Bánh Mì Gà Xé", 20000, R.drawable.img_food_chicken, "Gà xé sốt bơ trứng"))});
            topicPool.add(new Object[]{"Hải Sản Tươi Sống", "Tôm cua cá mực, tươi ngon mỗi ngày!", java.util.Arrays.asList(
                    new FoodMenuItem("t8_1", "Tôm Hùm Phô Mai", 350000, R.drawable.img_food_pizza, "Tôm hùm nướng phô mai đút lò"),
                    new FoodMenuItem("t8_2", "Cua Ghẹ Rang Me", 180000, R.drawable.img_food_pizza, "Ghẹ tươi sống rang xốt me chua ngọt"),
                    new FoodMenuItem("t8_3", "Ốc Hương Hoàng Kim", 90000, R.drawable.img_food_bubbletea, "Ốc hương xào trứng muối béo ngậy"),
                    new FoodMenuItem("t8_4", "Nghêu Hấp Sả", 60000, R.drawable.img_food_bubbletea, "Nghêu tươi hấp sả thơm lừng"),
                    new FoodMenuItem("t8_5", "Hàu Nướng Mỡ Hành", 80000, R.drawable.img_food_pizza, "Hàu sữa nướng mỡ hành đậu phộng"))});

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
