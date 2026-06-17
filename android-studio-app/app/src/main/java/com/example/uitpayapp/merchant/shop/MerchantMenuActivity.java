package com.example.uitpayapp.merchant.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuCategory;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;
import com.example.uitpayapp.merchant.shop.shop_model.ToppingGroup;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MerchantMenuActivity extends AppCompatActivity {

    private RecyclerView rvMenu;
    private MerchantMenuAdapter dishAdapter;
    private MerchantToppingAdapter toppingAdapter;
    private TabLayout tabLayout;
    private View btnAdd;
    private EditText etSearch;

    private List<MerchantMenuCategory> dishCategories = new ArrayList<>();
    private List<ToppingGroup> toppingGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_merchant_menu);

        initViews();
        setupData();
        setupTabLayout();

        rvMenu.setAdapter(dishAdapter);
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        rvMenu = findViewById(R.id.rv_merchant_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        
        tabLayout = findViewById(R.id.tab_layout);
        btnAdd = findViewById(R.id.btn_add_dish_and_topping);
        etSearch = findViewById(R.id.et_seller_search_dish_and_topping);

        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> showAddBottomSheet());
        }

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                TabLayout tabLayout = findViewById(R.id.tab_vouchers);
                filterSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });

        View mainView = findViewById(R.id.merchant_menu_container);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void filterSearch(String query) {
        if (query.isEmpty()) {
            dishAdapter.updateList(dishCategories);
            toppingAdapter.updateList(toppingGroups);
            return;
        }

        String lowerCaseQuery = query.toLowerCase().trim();

        List<MerchantMenuCategory> filteredDishes = new ArrayList<>();
        for (MerchantMenuCategory category : dishCategories) {
            List<MerchantMenuItem> filteredItems = new ArrayList<>();
            for (MerchantMenuItem item : category.getItems()) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredItems.add(item);
                }
            }
            if (!filteredItems.isEmpty()) {
                filteredDishes.add(new MerchantMenuCategory(category.getCategoryName(), filteredItems));
            }
        }
        dishAdapter.updateList(filteredDishes);

        //loc topping
        List<ToppingGroup> filteredToppings = new ArrayList<>();
        for (ToppingGroup group : toppingGroups) {
            List<MerchantMenuItem> filteredItems = new ArrayList<>();
            for (MerchantMenuItem item : group.getToppings()) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredItems.add(item);
                }
            }
            if (!filteredItems.isEmpty() || group.getName().toLowerCase().contains(lowerCaseQuery)) {
                filteredToppings.add(new ToppingGroup(group.getName(), filteredItems.isEmpty() ? group.getToppings() : filteredItems));
            }
        }
        toppingAdapter.updateList(filteredToppings);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rvMenu.setAdapter(dishAdapter);
                } else {
                    rvMenu.setAdapter(toppingAdapter);
                }
                // Khi chuyển tab, thực hiện lọc lại theo query hiện tại
                filterSearch(etSearch.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupData() {
        List<MerchantMenuItem> flashSaleItems = new ArrayList<>();
        flashSaleItems.add(new MerchantMenuItem("Trà Sữa Trân Châu", 25000, R.drawable.ic_food, true));
        flashSaleItems.add(new MerchantMenuItem("Cơm Gà Xối Mỡ", 35000, R.drawable.ic_food, true));
        dishCategories.add(new MerchantMenuCategory("FLASH SALE", flashSaleItems));

        List<MerchantMenuItem> bestSellerItems = new ArrayList<>();
        bestSellerItems.add(new MerchantMenuItem("Phở Bò Đặc Biệt", 45000, R.drawable.ic_food, true));
        bestSellerItems.add(new MerchantMenuItem("Bún Chả Hà Nội", 40000, R.drawable.ic_food, true));
        dishCategories.add(new MerchantMenuCategory("BEST SELLER", bestSellerItems));

        dishAdapter = new MerchantMenuAdapter(dishCategories);

        List<MerchantMenuItem> riceOptions = new ArrayList<>();
        riceOptions.add(new MerchantMenuItem("Cơm Trắng (Mẻ)", 5000, 0, true));
        riceOptions.add(new MerchantMenuItem("Cơm Vàng (Chiên)", 8000, 0, true));
        toppingGroups.add(new ToppingGroup("Ban Muốn Dùng Cơm Nào", riceOptions));

        List<MerchantMenuItem> additionalToppings = new ArrayList<>();
        additionalToppings.add(new MerchantMenuItem("Thêm Trứng", 5000, 0, true));
        additionalToppings.add(new MerchantMenuItem("Thêm Chả", 10000, 0, true));
        additionalToppings.add(new MerchantMenuItem("Thêm Canh", 5000, 0, false));
        toppingGroups.add(new ToppingGroup("Topping Thêm", additionalToppings));

        toppingAdapter = new MerchantToppingAdapter(toppingGroups);
    }

    private void showAddBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_add_menu_selection, null);
        
        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        TextView btnAddCategory = view.findViewById(R.id.btn_add_category);
        TextView btnAddItem = view.findViewById(R.id.btn_add_item);
        TextView btnCancel = view.findViewById(R.id.btn_cancel_sheet);

        boolean isToppingTab = tabLayout.getSelectedTabPosition() == 1;

        if (!isToppingTab) {
            tvTitle.setText("Thêm món");
            btnAddCategory.setText("Thêm danh mục món");
            btnAddItem.setText("Thêm món ăn");
        } else {
            tvTitle.setText("Thêm topping");
            btnAddCategory.setText("Thêm nhóm topping");
            btnAddItem.setText("Thêm topping");
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnAddCategory.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, AddMerchantCategoryActivity.class);
            intent.putExtra("is_topping_group", isToppingTab);
            startActivity(intent);
        });

        btnAddItem.setOnClickListener(v -> {
            dialog.dismiss();
            if (!isToppingTab) {
                startActivity(new Intent(this, AddMerchantDishActivity.class));
            } else {
                startActivity(new Intent(this, AddMerchantToppingActivity.class));
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
}
