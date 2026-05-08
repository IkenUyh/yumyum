package com.example.uitpayapp.home.food_order;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FoodOrderActivity extends AppCompatActivity {

    private List<Restaurant> restaurants;
    private List<Restaurant> filteredRestaurants;
    private BrandAdapter brandAdapter;
    private FoodCategoryAdapter categoryAdapter;
    private String currentPin = "";
    private View[] pinDots = new View[6];
    private List<CartItem> pendingCart;
    private String pendingRestaurantName;
    private String selectedCategory = null;
    private String currentSearchQuery = "";
    private TextView tvDeliveryAddress;

    // Danh sách địa chỉ mẫu
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
        setContentView(R.layout.activity_food_order);

        // Handle system insets
        View scrollView = findViewById(R.id.food_main_scroll);
        if (scrollView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, insets) -> {
                Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), navInsets.bottom);
                return insets;
            });
        }

        // Header close button
        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        // Address bar
        tvDeliveryAddress = findViewById(R.id.tv_delivery_address);
        findViewById(R.id.layout_address_bar).setOnClickListener(v -> showAddressSelection());

        setupRestaurants();
        setupCategories();
        setupBrands();
        setupSearch();
        setupVouchers();
    }

    // ============ Address Selection ============

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

        // Reuse the 2 existing buttons for the first 2 addresses
        TextView btnWallet = ((android.widget.LinearLayout) view.findViewById(R.id.btn_dest_wallet))
                .findViewById(android.R.id.text1) != null ? null : null;

        // Override button texts and behaviors for address selection
        android.widget.LinearLayout llWallet = view.findViewById(R.id.btn_dest_wallet);
        android.widget.LinearLayout llSaving = view.findViewById(R.id.btn_dest_saving);

        // Override texts
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

    // ============ Search ============

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

    // ============ Categories ============

    private void setupCategories() {
        List<FoodCategory> categories = new ArrayList<>();
        categories.add(new FoodCategory("Cà phê\nTrà sữa", "☕", Color.parseColor("#FFF3E0")));
        categories.add(new FoodCategory("Gà rán\nBurger", "🍔", Color.parseColor("#FBE9E7")));
        categories.add(new FoodCategory("Cơm\nPizza", "🍕", Color.parseColor("#FCE4EC")));
        categories.add(new FoodCategory("Bánh\nKem", "🍰", Color.parseColor("#F3E5F5")));

        RecyclerView rv = findViewById(R.id.rv_categories);
        categoryAdapter = new FoodCategoryAdapter(categories, category -> {
            if (category == null) {
                // Deselected — show all
                selectedCategory = null;
            } else {
                selectedCategory = category.getName();
            }
            applyFilters();
        });
        rv.setAdapter(categoryAdapter);
    }

    // ============ Filter logic ============

    private void applyFilters() {
        filteredRestaurants = new ArrayList<>();
        for (Restaurant r : restaurants) {
            boolean matchesCategory = true;
            boolean matchesSearch = true;

            // Category filter
            if (selectedCategory != null) {
                matchesCategory = selectedCategory.equals(r.getCategory());
            }

            // Search filter
            if (!currentSearchQuery.isEmpty()) {
                String name = r.getName().replace("\n", " ").toLowerCase();
                boolean nameMatch = name.contains(currentSearchQuery);
                // Also search menu items
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

    // ============ Restaurant data ============

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

        restaurants.add(new Restaurant("Texas\nChicken", "TX", Color.parseColor("#FF6900"), "Gà rán\nBurger", Arrays.asList(
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

    // ============ Brands grid ============

    private void setupBrands() {
        RecyclerView rv = findViewById(R.id.rv_brands);
        brandAdapter = new BrandAdapter(filteredRestaurants, this::showRestaurantMenu);
        rv.setAdapter(brandAdapter);
    }

    // ============ Vouchers ============

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

    // ============ Restaurant menu bottom sheet ============

    private void showRestaurantMenu(Restaurant restaurant) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_restaurant_menu, null);
        dialog.setContentView(view);

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        // Set restaurant name
        ((TextView) view.findViewById(R.id.tv_restaurant_name)).setText(restaurant.getName().replace("\n", " "));

        view.findViewById(R.id.btn_close_menu).setOnClickListener(v -> dialog.dismiss());

        // Setup menu RecyclerView
        RecyclerView rvMenu = view.findViewById(R.id.rv_menu_items);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        View layoutCartSummary = view.findViewById(R.id.layout_cart_summary);
        TextView tvCartCount = view.findViewById(R.id.tv_cart_count);
        TextView tvCartTotal = view.findViewById(R.id.tv_cart_total);
        View btnOrder = view.findViewById(R.id.btn_order);

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

        // Order button
        btnOrder.setOnClickListener(v -> {
            List<CartItem> cart = menuAdapter.getCart();
            if (cart.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 món", Toast.LENGTH_SHORT).show();
                return;
            }
            pendingCart = new ArrayList<>(cart);
            pendingRestaurantName = restaurant.getName().replace("\n", " ");
            dialog.dismiss();
            showDestinationSelection();
        });

        dialog.show();
    }

    // ============ Payment flow (reuse pattern) ============

    private void showDestinationSelection() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_destination, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_destination_title)).setText("Chọn nguồn tiền");

        View bottomSheet = (View) view.getParent();
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btn_close_destination).setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.btn_dest_wallet).setOnClickListener(v -> {
            dialog.dismiss();
            showPasscodeBottomSheet("Ví UIT Pay");
        });

        view.findViewById(R.id.btn_dest_saving).setOnClickListener(v -> {
            dialog.dismiss();
            showPasscodeBottomSheet("Quỹ tiết kiệm");
        });

        dialog.show();
    }

    private void showPasscodeBottomSheet(String source) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_passcode, null);
        dialog.setContentView(sheetView);

        pinDots[0] = sheetView.findViewById(R.id.dot_1);
        pinDots[1] = sheetView.findViewById(R.id.dot_2);
        pinDots[2] = sheetView.findViewById(R.id.dot_3);
        pinDots[3] = sheetView.findViewById(R.id.dot_4);
        pinDots[4] = sheetView.findViewById(R.id.dot_5);
        pinDots[5] = sheetView.findViewById(R.id.dot_6);

        sheetView.findViewById(R.id.btn_close_passcode).setOnClickListener(v -> dialog.dismiss());

        int[] numberButtonIds = {
            R.id.btn_pin_0, R.id.btn_pin_1, R.id.btn_pin_2, R.id.btn_pin_3, R.id.btn_pin_4,
            R.id.btn_pin_5, R.id.btn_pin_6, R.id.btn_pin_7, R.id.btn_pin_8, R.id.btn_pin_9
        };

        for (int id : numberButtonIds) {
            sheetView.findViewById(id).setOnClickListener(v -> {
                if (currentPin.length() < 6) {
                    currentPin += v.getTag().toString();
                    updatePinDots();

                    if (currentPin.length() == 6) {
                        new Handler().postDelayed(() -> {
                            dialog.dismiss();
                            onOrderComplete(source);
                        }, 300);
                    }
                }
            });
        }

        sheetView.findViewById(R.id.btn_pin_delete).setOnClickListener(v -> {
            if (currentPin.length() > 0) {
                currentPin = currentPin.substring(0, currentPin.length() - 1);
                updatePinDots();
            }
        });

        currentPin = "";
        updatePinDots();
        dialog.show();
    }

    private void updatePinDots() {
        int colorBlue = Color.parseColor("#0A46A6");
        int colorGray = Color.parseColor("#E0E0E0");
        for (int i = 0; i < 6; i++) {
            if (i < currentPin.length()) {
                pinDots[i].setBackgroundTintList(ColorStateList.valueOf(colorBlue));
            } else {
                pinDots[i].setBackgroundTintList(ColorStateList.valueOf(colorGray));
            }
        }
    }

    private void onOrderComplete(String source) {
        if (pendingCart == null || pendingCart.isEmpty()) return;

        long total = 0;
        int itemCount = 0;
        for (CartItem ci : pendingCart) {
            total += ci.getTotalPrice();
            itemCount += ci.getQuantity();
        }

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String message = "Đặt hàng thành công! " + itemCount + " món từ " + pendingRestaurantName
                + " - " + formatter.format(total) + "đ (thanh toán từ " + source + ")";

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        pendingCart = null;
        pendingRestaurantName = null;
    }
}
