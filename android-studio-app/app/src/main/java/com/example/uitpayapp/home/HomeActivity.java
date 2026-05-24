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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.checkout.TransferConfirmationActivity;
import com.example.uitpayapp.home.home_adapters.BrandAdapter;
import com.example.uitpayapp.home.home_adapters.FoodCategoryAdapter;
import com.example.uitpayapp.home.home_adapters.FoodMenuAdapter;
import com.example.uitpayapp.home.home_adapters.FoodVoucherAdapter;
import com.example.uitpayapp.home.home_adapters.ImageSliderAdapter;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.home_models.FoodVoucher;
import com.example.uitpayapp.home.home_models.Restaurant;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private List<Restaurant> restaurants;
    private List<Restaurant> filteredRestaurants;
    private BrandAdapter brandAdapter;
    private FoodCategoryAdapter categoryAdapter;
    private List<CartItem> globalCart = new ArrayList<>();
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

        // Cấu hình tràn viền status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_home);

        // Xử lý padding để UI không bị che khuất bởi tai thỏ và thanh điều hướng hệ thống
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

        // Thiết lập Image Slider cho Banner quảng cáo mẫu
        setupImageSlider();

        // Nút giỏ hàng ở Header
        findViewById(R.id.btn_cart).setOnClickListener(v -> checkoutGlobalCart());

        // Thanh chọn địa chỉ giao hàng
        tvDeliveryAddress = findViewById(R.id.tv_delivery_address);
        findViewById(R.id.layout_address_bar).setOnClickListener(v -> showAddressSelection());

        setupRestaurants();
        setupCategories();
        setupBrands();
        setupSearch();
        setupVouchers();

        // Thanh điều hướng Navigation dưới
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
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navGift = findViewById(R.id.navGift);
        LinearLayout navAccount = findViewById(R.id.navAccount);

        navHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.transaction.TransactionHistoryActivity.class);
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
        // GridLayoutManager(HORIZONTAL, spanCount=2) fills column-by-column:
        // Column 0: item[0](row0), item[1](row1)
        // Column 1: item[2](row0), item[3](row1)
        // So we interleave row0 and row1 items:
        // Row 0: Cà phê, Gà rán, Cơm, Bánh, Bún, Lẩu, Chọn tất cả
        // Row 1: Đồ uống, Trái cây, Sushi, Đồ ăn vặt, Chè, BBQ
        categories.add(new FoodCategory("Cà phê\nTrà sữa", "☕", Color.parseColor("#FFF3E0")));
        categories.add(new FoodCategory("Đồ uống", "🧃", Color.parseColor("#E8F5E9")));
        categories.add(new FoodCategory("Gà rán\nBurger", "🍔", Color.parseColor("#FBE9E7")));
        categories.add(new FoodCategory("Trái cây", "🍓", Color.parseColor("#FCE4EC")));
        categories.add(new FoodCategory("Cơm\nPizza", "🍕", Color.parseColor("#FCE4EC")));
        categories.add(new FoodCategory("Sushi", "🍣", Color.parseColor("#FFF9C4")));
        categories.add(new FoodCategory("Bánh\nKem", "🍰", Color.parseColor("#F3E5F5")));
        categories.add(new FoodCategory("Đồ ăn vặt", "🍿", Color.parseColor("#FFF3E0")));
        categories.add(new FoodCategory("Bún\nPhở", "🍜", Color.parseColor("#E0F7FA")));
        categories.add(new FoodCategory("Chè\nTráng miệng", "🍧", Color.parseColor("#F3E5F5")));
        categories.add(new FoodCategory("Lẩu", "🍲", Color.parseColor("#FBE9E7")));
        categories.add(new FoodCategory("BBQ\nNướng", "🍖", Color.parseColor("#FFEBEE")));
        // Nút "Chọn tất cả" — cuối dòng 1 (row 0)
        categories.add(new FoodCategory("Chọn\ntất cả", "»", Color.parseColor("#E8EAF6"), true));

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
        brandAdapter.updateData(filteredRestaurants);
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
                new FoodMenuItem("Combo Whopper", 109000, R.drawable.img_food_chicken, "Whopper + khoai + nước"),
                new FoodMenuItem("Chicken Nuggets 6pc", 45000, R.drawable.img_food_chicken, "6 miếng gà viên chiên"),
                new FoodMenuItem("Onion Rings", 35000, R.drawable.img_food_pizza, "Hành tây chiên giòn")
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

    private void setupBrands() {
        RecyclerView rv = findViewById(R.id.rv_brands);
        brandAdapter = new BrandAdapter(filteredRestaurants, this::showRestaurantMenu);
        rv.setAdapter(brandAdapter);
    }

    private void setupVouchers() {
        List<FoodVoucher> vouchers = new ArrayList<>();
        vouchers.add(new FoodVoucher("KFC", "Giảm 30.000đ", "Cho đơn từ 150.000đ", Color.parseColor("#E4002B")));
        vouchers.add(new FoodVoucher("Phúc Long", "Giảm 10.000đ", "Khi dùng Tài khoản UIT Pay", Color.parseColor("#006241")));
        vouchers.add(new FoodVoucher("Jollibee", "Giảm 20.000đ", "Cho đơn từ 100.000đ", Color.parseColor("#E31837")));
        vouchers.add(new FoodVoucher("Highlands", "Giảm 15.000đ", "Cho đơn từ 80.000đ", Color.parseColor("#6F4E37")));
        vouchers.add(new FoodVoucher("Texas", "Giảm 30.000đ", "Khi dùng Tài khoản UIT Pay", Color.parseColor("#FF6900")));
        vouchers.add(new FoodVoucher("MAYCHA", "Giảm 10.000đ", "Cho đơn từ 50.000đ", Color.parseColor("#FF69B4")));
        vouchers.add(new FoodVoucher("Burger King", "Giảm 35.000đ", "Khi dùng Tài khoản UIT Pay", Color.parseColor("#FF8C00")));
        vouchers.add(new FoodVoucher("Domino's", "Giảm 25.000đ", "Cho đơn từ 200.000đ", Color.parseColor("#006491")));
        vouchers.add(new FoodVoucher("Tous Les Jours", "Giảm 20.000đ", "Cho đơn từ 100.000đ", Color.parseColor("#C62828")));

        RecyclerView rv = findViewById(R.id.rv_vouchers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new FoodVoucherAdapter(vouchers));
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
        btnOrder.setText("Thêm vào giỏ");

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
                tvCartCount.setText(totalItems + " món");
                tvCartTotal.setText(formatter.format(totalPrice) + "đ");
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
        for (CartItem ci : globalCart) {
            count += ci.getQuantity();
        }
        if (count > 0) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(String.valueOf(count));
        } else {
            tvBadge.setVisibility(View.GONE);
        }
    }

    private void checkoutGlobalCart() {
        if (globalCart.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        long totalAmount = 0;
        StringBuilder productNames = new StringBuilder();
        for (CartItem ci : globalCart) {
            totalAmount += ci.getTotalPrice();
            if (productNames.length() > 0) {
                productNames.append(", ");
            }
            productNames.append(ci.getQuantity()).append("x ").append(ci.getMenuItem().getName());
        }

        Intent intent = new Intent(this, TransferConfirmationActivity.class);
        intent.putExtra("KEY_AMOUNT", String.valueOf(totalAmount));
        intent.putExtra("KEY_IS_FOOD_ORDER", true);
        intent.putExtra("KEY_FOOD_PRODUCTS", productNames.toString());
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