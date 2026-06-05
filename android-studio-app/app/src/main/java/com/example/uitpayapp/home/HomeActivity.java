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
import com.example.uitpayapp.home.home_adapters.ImageSliderAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.home_models.Restaurant;
import com.example.uitpayapp.home.home_models.TopicStore;
import com.example.uitpayapp.recommendeddeal.RecommendedDealActivity;
import com.example.uitpayapp.recommendeddeal.RecommendedDealModel;
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
    private List<CartItem> globalCart;

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

        globalCart = CartManager.getInstance().getCart();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_header_content), (v, insets) -> {
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
        ViewCompat.setOnApplyWindowInsetsListener(stickyTabLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            statusBarHeight = systemBars.top;
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        setupImageSlider();

        findViewById(R.id.btn_cart).setOnClickListener(v -> checkoutGlobalCart());

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

    @Override
    protected void onResume() {
        super.onResume();
        if (globalCart != null) {
            updateGlobalCartBadge();
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
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navGift = findViewById(R.id.navGift);
        LinearLayout navAccount = findViewById(R.id.navAccount);

        navHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.history.TransactionHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navGift.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.gift.GiftActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.profile.ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void showAddressSelection() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_destination, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_destination_title)).setText("Chá»n Ä‘á»‹a chá»‰ giao");

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
            Toast.makeText(this, "Giao tá»›i: " + ADDRESSES[0], Toast.LENGTH_SHORT).show();
        });

        llSaving.setOnClickListener(v -> {
            tvDeliveryAddress.setText(ADDRESSES[1]);
            dialog.dismiss();
            Toast.makeText(this, "Giao tá»›i: " + ADDRESSES[1], Toast.LENGTH_SHORT).show();
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
        // Categories filter only affects restaurant list, deals are independent
    }

    private void setupRestaurants() {
        restaurants = new ArrayList<>();

        restaurants.add(new Restaurant("KFC", "KFC", Color.parseColor("#E4002B"), "Gà rán\nBurger", Arrays.asList(
                new FoodMenuItem("Gà rán truyền thống", 45000, R.drawable.img_food_chicken, "1 miếng gà rán giòn"),
                new FoodMenuItem("Combo gà rán + khoai", 89000, R.drawable.img_food_chicken, "2 miếng gà + khoai tây"),
                new FoodMenuItem("Burger gà giòn", 39000, R.drawable.img_food_chicken, "Burger gà với rau tươi"),
                new FoodMenuItem("Cơm gà sốt cay", 55000, R.drawable.img_food_chicken, "Cơm trắng + gà sốt cay")
        )));

        restaurants.add(new Restaurant("Phúc Long", "PL", Color.parseColor("#006241"), "Cà phê\nTrà sữa", Arrays.asList(
                new FoodMenuItem("Trà sen vàng", 45000, R.drawable.img_food_bubbletea, "Trà ướp sen thơm mát"),
                new FoodMenuItem("Trà đào cam sả", 55000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"),
                new FoodMenuItem("Cà phê sữa đá", 35000, R.drawable.img_food_coffee, "Cà phê phin truyền thống"),
                new FoodMenuItem("Bánh mì chà bông", 25000, R.drawable.img_food_chicken, "Bánh mì nướng giòn")
        )));

        restaurants.add(new Restaurant("Jollibee", "JB", Color.parseColor("#E31837"), "Gà rán\nBurger", Arrays.asList(
                new FoodMenuItem("Gà giòn vui vẻ 1 miếng", 35000, R.drawable.img_food_chicken, "Gà giòn đặc biệt"),
                new FoodMenuItem("Combo Jolly 1", 79000, R.drawable.img_food_chicken, "Gà + cơm + nước"),
                new FoodMenuItem("Mì Ý sốt bò bằm", 55000, R.drawable.img_food_pizza, "Mì Ý với sốt bò đậm đà"),
                new FoodMenuItem("Burger Yumm", 49000, R.drawable.img_food_chicken, "Burger bò phô mai")
        )));

        restaurants.add(new Restaurant("Highlands\nCoffee", "HC", Color.parseColor("#6F4E37"), "Cà phê\nTrà sữa", Arrays.asList(
                new FoodMenuItem("Phin sữa đá", 39000, R.drawable.img_food_coffee, "Cà phê phin sữa đặc"),
                new FoodMenuItem("Freeze trà xanh", 55000, R.drawable.img_food_bubbletea, "Trà xanh đá xay"),
                new FoodMenuItem("Bánh mì thịt nguội", 35000, R.drawable.img_food_chicken, "Bánh mì kiểu Việt"),
                new FoodMenuItem("Freeze sô-cô-la", 55000, R.drawable.img_food_coffee, "Sô-cô-la đá xay kem")
        )));

        restaurants.add(new Restaurant("TEXAS\nCHICKEN", "TX", Color.parseColor("#FF6900"), "Gà rán\nBurger", Arrays.asList(
                new FoodMenuItem("Gà rán Texas 1 miếng", 42000, R.drawable.img_food_chicken, "Gà giòn kiểu Texas"),
                new FoodMenuItem("Combo Texas Big", 99000, R.drawable.img_food_chicken, "3 miếng gà + khoai + nước"),
                new FoodMenuItem("Burger gà cay", 45000, R.drawable.img_food_chicken, "Burger gà sốt cay"),
                new FoodMenuItem("Khoai tây chiên", 25000, R.drawable.img_food_pizza, "Khoai tây giòn tan")
        )));

        restaurants.add(new Restaurant("MAYCHA", "MC", Color.parseColor("#FF69B4"), "Cà phê\nTrà sữa", Arrays.asList(
                new FoodMenuItem("Trà sữa truyền thống", 35000, R.drawable.img_food_bubbletea, "Trà sữa trân châu đường đen"),
                new FoodMenuItem("Trà sữa matcha", 45000, R.drawable.img_food_bubbletea, "Matcha Nhật Bản"),
                new FoodMenuItem("Trà đào", 40000, R.drawable.img_food_bubbletea, "Trà đào tươi mát"),
                new FoodMenuItem("Sữa tươi trân châu", 38000, R.drawable.img_food_bubbletea, "Sữa tươi + trân châu đen")
        )));

        restaurants.add(new Restaurant("Burger\nKing", "BK", Color.parseColor("#FF8C00"), "Gà rán\nBurger", Arrays.asList(
                new FoodMenuItem("Whopper", 79000, R.drawable.img_food_chicken, "Burger bò nướng lửa cỡ lớn"),
                new FoodMenuItem("Combo Whopper", 109000, R.drawable.img_food_chicken, "Whopper + khoai + nÆ°á»›c"),
                new FoodMenuItem("Chicken Nuggets 6pc", 45000, R.drawable.img_food_chicken, "6 miáº¿ng gĂ  viĂªn chiĂªn"),
                new FoodMenuItem("Onion Rings", 35000, R.drawable.img_food_pizza, "HĂ nh tĂ¢y chiĂªn giĂ²n")
        )));

        restaurants.add(new Restaurant("Domino's\nPizza", "DP", Color.parseColor("#006491"), "Cơm\nPizza", Arrays.asList(
                new FoodMenuItem("Pizza Hải sản Pesto", 169000, R.drawable.img_food_pizza, "Pizza hải sản sốt pesto"),
                new FoodMenuItem("Pizza Pepperoni", 149000, R.drawable.img_food_pizza, "Pizza pepperoni cổ điển"),
                new FoodMenuItem("Gà viên phô mai", 59000, R.drawable.img_food_chicken, "Gà viên nhân phô mai"),
                new FoodMenuItem("Khoai tây xoắn", 49000, R.drawable.img_food_pizza, "Khoai tây xoắn giòn")
        )));

        restaurants.add(new Restaurant("Tous Les\nJours", "TJ", Color.parseColor("#C62828"), "Bánh\nKem", Arrays.asList(
                new FoodMenuItem("Bánh kem dâu tây", 189000, R.drawable.img_food_pizza, "Bánh kem tươi vị dâu"),
                new FoodMenuItem("Bánh mì bơ tỏi", 25000, R.drawable.img_food_chicken, "Bánh mì nướng bơ tỏi giòn"),
                new FoodMenuItem("Croissant trứng muối", 35000, R.drawable.img_food_chicken, "Croissant nhân trứng muối"),
                new FoodMenuItem("Bánh su kem", 29000, R.drawable.img_food_bubbletea, "Bánh su kem tươi mát")
        )));

        filteredRestaurants = new ArrayList<>(restaurants);
    }

    private void setupTopics() {
        List<Object[]> topicPool = new ArrayList<>();
        topicPool.add(new Object[]{"Bún Phở Hội Tụ", "Top quán bún phở được yêu thích nhất!", Arrays.asList(
                new TopicStore("Phở Bò Lý Quốc Sư", R.drawable.img_food_chicken),
                new TopicStore("Bún Bò Huế O Xuân", R.drawable.img_food_pizza),
                new TopicStore("Phở 24 - Võ Văn Ngân", R.drawable.img_food_chicken),
                new TopicStore("Bún Riêu Cua Hà Nội", R.drawable.img_food_pizza),
                new TopicStore("Hủ Tiếu Nam Vang", R.drawable.img_food_chicken))});
        topicPool.add(new Object[]{"Gà Rán Chất Lượng", "Giòn tan, thơm lừng – đậm đà vị gà!", Arrays.asList(
                new TopicStore("KFC - Đặng Văn Bi", R.drawable.img_food_chicken),
                new TopicStore("Jollibee - Phạm Văn Đồng", R.drawable.img_food_chicken),
                new TopicStore("Texas Chicken", R.drawable.img_food_chicken),
                new TopicStore("Popeyes - Võ Văn Ngân", R.drawable.img_food_chicken),
                new TopicStore("Gà Rán Ông Già", R.drawable.img_food_chicken))});
        topicPool.add(new Object[]{"Cà Phê & Trà Sữa", "Nạp năng lượng, thưởng thức từng giọt!", Arrays.asList(
                new TopicStore("Phúc Long Tea & Coffee", R.drawable.img_food_coffee),
                new TopicStore("Highlands Coffee", R.drawable.img_food_coffee),
                new TopicStore("The Coffee House", R.drawable.img_food_bubbletea),
                new TopicStore("MAYCHA - Trà Sữa", R.drawable.img_food_bubbletea),
                new TopicStore("Ông Bầu Coffee", R.drawable.img_food_coffee))});
        topicPool.add(new Object[]{"Cơm Ngon Mỗi Ngày", "Bữa cơm ấm bụng, giá cả phải chăng!", Arrays.asList(
                new TopicStore("Cơm Tấm Phúc Lộc Thọ", R.drawable.img_food_chicken),
                new TopicStore("Cơm Gà Xối Mỡ", R.drawable.img_food_chicken),
                new TopicStore("Cơm Văn Phòng Sài Gòn", R.drawable.img_food_pizza),
                new TopicStore("Cơm Tấm Cali - Thủ Đức", R.drawable.img_food_chicken),
                new TopicStore("Cơm Chiên Dương Châu", R.drawable.img_food_pizza))});
        topicPool.add(new Object[]{"Lẩu Quây Quần", "Quây quần bên bạn bè, ấm áp mùa đông!", Arrays.asList(
                new TopicStore("Lẩu Bò Nhúng Dấm", R.drawable.img_food_pizza),
                new TopicStore("Lẩu Thái Chua Cay", R.drawable.img_food_pizza),
                new TopicStore("Lẩu Hải Sản Phú Quốc", R.drawable.img_food_bubbletea),
                new TopicStore("Lẩu Gà Lá É", R.drawable.img_food_chicken),
                new TopicStore("Lẩu Nấm Chay Tịnh", R.drawable.img_food_pizza))});
        topicPool.add(new Object[]{"Bánh Mì Sài Gòn", "Ổ bánh mì nóng giòn, đậm đà hương vị!", Arrays.asList(
                new TopicStore("Bánh Mì Huynh Hoa", R.drawable.img_food_chicken),
                new TopicStore("Bánh Mì Bảy Hổ", R.drawable.img_food_chicken),
                new TopicStore("Bánh Mì Phượng Hội An", R.drawable.img_food_pizza),
                new TopicStore("Bánh Mì Chảo Ốp La", R.drawable.img_food_pizza),
                new TopicStore("Bánh Mì Doner Kebab", R.drawable.img_food_chicken))});
        topicPool.add(new Object[]{"Hải Sản Tươi Sống", "Tôm cua cá mực, tươi ngon mỗi ngày!", Arrays.asList(
                new TopicStore("Hải Sản Bé Mặn", R.drawable.img_food_bubbletea),
                new TopicStore("Ốc Đào - Nguyễn Trãi", R.drawable.img_food_bubbletea),
                new TopicStore("Cua Biển 1 Pound", R.drawable.img_food_pizza),
                new TopicStore("Tôm Hùm BBQ", R.drawable.img_food_chicken),
                new TopicStore("Sò Điệp Nướng Mỡ Hành", R.drawable.img_food_pizza))});
        topicPool.add(new Object[]{"Ăn Vặt Đường Phố", "Món ngon vừa hè, nhớ mãi không quên!", Arrays.asList(
                new TopicStore("Bánh Tráng Trộn", R.drawable.img_food_pizza),
                new TopicStore("Xiên Que Nướng", R.drawable.img_food_chicken),
                new TopicStore("Chè Khúc Bạch", R.drawable.img_food_bubbletea),
                new TopicStore("Takoyaki Bạch Tuộc", R.drawable.img_food_pizza),
                new TopicStore("Khoai Lắc Phô Mai", R.drawable.img_food_chicken))});

        java.util.Collections.shuffle(topicPool);
        int[] sectionIds = {R.id.topic_section_1, R.id.topic_section_2, R.id.topic_section_3, R.id.topic_section_4};
        for (int i = 0; i < 4; i++) {
            Object[] topic = topicPool.get(i);
            @SuppressWarnings("unchecked")
            List<TopicStore> stores = (List<TopicStore>) topic[2];
            setupTopicSection(findViewById(sectionIds[i]), (String) topic[0], (String) topic[1], stores);
        }
    }


    private void setupTopicSection(View sectionView, String title, String subtitle, List<TopicStore> stores) {
        TextView tvTitle = sectionView.findViewById(R.id.tv_topic_title);
        TextView tvSubtitle = sectionView.findViewById(R.id.tv_topic_subtitle);
        TextView tvSeeMore = sectionView.findViewById(R.id.tv_topic_see_more);
        RecyclerView rvStores = sectionView.findViewById(R.id.rv_topic_stores);

        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);

        rvStores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStores.setAdapter(new TopicStoreAdapter(stores));

        tvSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecommendedDealActivity.class);
            startActivity(intent);
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

            if (tabTopOnScreen < statusBarHeight) {
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
                boolean exists = false;
                for (CartItem gc : globalCart) {
                    if (gc.getMenuItem().getName().equals(item.getMenuItem().getName())) {
                        int newQuantity = gc.getQuantity() + item.getQuantity();
                        globalCart.remove(gc);
                        globalCart.add(new CartItem(item.getMenuItem(), newQuantity));
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    globalCart.add(item);
                }
            }

            updateGlobalCartBadge();
            dialog.dismiss();
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void updateGlobalCartBadge() {
        TextView tvBadge = findViewById(R.id.tv_global_cart_badge);
        int count = 0;
        if (globalCart != null) {
            for (CartItem ci : globalCart) {
                count += ci.getQuantity();
            }
        }
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
}
