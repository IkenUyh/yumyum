package com.example.uitpayapp.merchant.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.example.uitpayapp.merchant.shop.viewmodel.MerchantMenuViewModel;
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
    private ProgressBar pbLoading;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;

    private List<MerchantMenuCategory> dishCategories = new ArrayList<>();
    private List<ToppingGroup> toppingGroups = new ArrayList<>();
    private MerchantMenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_merchant_menu);

        viewModel = new androidx.lifecycle.ViewModelProvider(this)
                .get(MerchantMenuViewModel.class);

        initViews();
        setupData();
        setupTabLayout();

        rvMenu.setAdapter(dishAdapter);

        viewModel.initializeRestaurant(this, () -> viewModel.loadMenu());

        // Observe Dish Categories
        viewModel.getDishCategories().observe(this, state -> {
            if (state == null) return;

            if (state.isLoading()) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    showLoading();
                }
                return;
            }

            if (state.isSuccess()) {
                dishCategories.clear();
                if (state.getData() != null) {
                    dishCategories.addAll(state.getData());
                }
                dishAdapter.updateList(dishCategories);
                if (tabLayout.getSelectedTabPosition() == 0) {
                    if (dishCategories.isEmpty()) {
                        showEmpty("Chưa có món ăn nào\nNhấn Thêm để tạo thực đơn");
                    } else {
                        showContent();
                    }
                }
            } else if (state.isError()) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    showEmpty(state.getMessage() != null ? state.getMessage() : "Không tải được thực đơn");
                }
            }
        });

        // Observe Topping Groups
        viewModel.getToppingGroups().observe(this, state -> {
            if (state == null) return;

            if (state.isLoading()) {
                if (tabLayout.getSelectedTabPosition() == 1) {
                    showLoading();
                }
                return;
            }

            if (state.isSuccess()) {
                toppingGroups.clear();
                if (state.getData() != null) {
                    toppingGroups.addAll(state.getData());
                }
                toppingAdapter.updateList(toppingGroups);
                if (tabLayout.getSelectedTabPosition() == 1) {
                    if (toppingGroups.isEmpty()) {
                        showEmpty("Chưa có nhóm topping nào\nNhấn Thêm để tạo nhóm topping");
                    } else {
                        showContent();
                    }
                }
            } else if (state.isError()) {
                if (tabLayout.getSelectedTabPosition() == 1) {
                    showEmpty(state.getMessage() != null ? state.getMessage() : "Không tải được nhóm topping");
                }
            }
        });

        // Observe Operation Status for Toast
        viewModel.getOperationStatus().observe(this, state -> {
            if (state != null) {
                if (state.isLoading()) {
                    // Could show a small loader, but we don't want to block the UI for a switch toggle
                } else if (state.isSuccess()) {
                    android.widget.Toast.makeText(this, state.getData(), android.widget.Toast.LENGTH_SHORT).show();
                } else if (state.isError()) {
                    android.widget.Toast.makeText(this, state.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        rvMenu = findViewById(R.id.rv_merchant_menu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));

        tabLayout = findViewById(R.id.tab_layout);
        btnAdd = findViewById(R.id.btn_add_dish_and_topping);
        etSearch = findViewById(R.id.et_seller_search_dish_and_topping);
        pbLoading = findViewById(R.id.pb_loading);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);

        if (btnAdd != null) btnAdd.setOnClickListener(v -> showAddBottomSheet());

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                filterSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Tìm kiếm realtime khi gõ
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
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

    // ----------------------------------------------------------------
    // UI State helpers
    // ----------------------------------------------------------------

    private void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        rvMenu.setVisibility(View.GONE);
    }

    private void showContent() {
        pbLoading.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        rvMenu.setVisibility(View.VISIBLE);
    }

    private void showEmpty(String message) {
        pbLoading.setVisibility(View.GONE);
        rvMenu.setVisibility(View.GONE);
        tvEmptyMessage.setText(message);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    // ----------------------------------------------------------------
    // Filter / Search
    // ----------------------------------------------------------------

    private void filterSearch(String query) {
        if (query.isEmpty()) {
            dishAdapter.updateList(dishCategories);
            toppingAdapter.updateList(toppingGroups);
            updateEmptyStateForCurrentTab();
            return;
        }

        String lowerCaseQuery = query.toLowerCase().trim();

        // Lọc món ăn
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

        // Lọc topping
        List<ToppingGroup> filteredToppings = new ArrayList<>();
        for (ToppingGroup group : toppingGroups) {
            List<MerchantMenuItem> filteredItems = new ArrayList<>();
            for (MerchantMenuItem item : group.getToppings()) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredItems.add(item);
                }
            }
            if (!filteredItems.isEmpty() || group.getName().toLowerCase().contains(lowerCaseQuery)) {
                filteredToppings.add(new ToppingGroup(
                        group.getName(),
                        filteredItems.isEmpty() ? group.getToppings() : filteredItems
                ));
            }
        }
        toppingAdapter.updateList(filteredToppings);

        updateEmptyStateForCurrentTab();
    }

    /**
     * Sau khi filter, cập nhật lại empty state nếu kết quả rỗng.
     */
    private void updateEmptyStateForCurrentTab() {
        boolean isToppingTab = tabLayout.getSelectedTabPosition() == 1;
        boolean isEmpty = isToppingTab ? toppingAdapter.getItemCount() == 0
                : dishAdapter.getItemCount() == 0;
        if (isEmpty) {
            showEmpty(isToppingTab ? "Không tìm thấy nhóm topping nào" : "Không tìm thấy món ăn nào");
        } else {
            showContent();
        }
    }

    // ----------------------------------------------------------------
    // Tab
    // ----------------------------------------------------------------

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean isToppingTab = tab.getPosition() == 1;
                if (isToppingTab) {
                    rvMenu.setAdapter(toppingAdapter);
                    // Kiểm tra trạng thái hiện tại của topping
                    if (toppingGroups.isEmpty()) {
                        showEmpty("Chưa có nhóm topping nào\nNhấn Thêm để tạo nhóm topping");
                    } else {
                        showContent();
                    }
                } else {
                    rvMenu.setAdapter(dishAdapter);
                    if (dishCategories.isEmpty()) {
                        showEmpty("Chưa có món ăn nào\nNhấn Thêm để tạo thực đơn");
                    } else {
                        showContent();
                    }
                }
                filterSearch(etSearch.getText().toString());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupData() {
        dishAdapter = new MerchantMenuAdapter(dishCategories);
        dishAdapter.setOnFoodStatusChangeListener((foodId, isAvailable) -> {
            if (viewModel != null) {
                viewModel.updateFoodStatus(foodId, isAvailable);
            }
        });
        toppingAdapter = new MerchantToppingAdapter(toppingGroups);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null && viewModel.getRestaurantId().getValue() != null) {
            viewModel.loadMenu();
        }
    }

    // ----------------------------------------------------------------
    // Bottom sheet thêm món/topping
    // ----------------------------------------------------------------

    private void showAddBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.layout_bottom_sheet_add_menu_selection, null);

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
