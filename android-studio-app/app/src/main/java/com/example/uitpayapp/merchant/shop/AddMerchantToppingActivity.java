package com.example.uitpayapp.merchant.shop;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;
import com.example.uitpayapp.merchant.shop.shop_model.ToppingGroup;
import com.example.uitpayapp.merchant.shop.viewmodel.MerchantMenuViewModel;

import java.util.List;

public class AddMerchantToppingActivity extends AppCompatActivity {

    private EditText etToppingName, etToppingPrice;
    private TextView tvCategorySelector, btnSave, btnDoneHeader, tvTitle;
    private boolean isEditMode = false;
    private MerchantMenuItem toppingData;
    private String categoryName;
    private Long groupId;
    private final List<ToppingGroup> toppingGroupsList = new java.util.ArrayList<>();
    private MerchantMenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_add_merchant_topping);

        isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);
        toppingData = (MerchantMenuItem) getIntent().getSerializableExtra("topping_data");
        categoryName = getIntent().getStringExtra("category_name");

        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(com.example.uitpayapp.merchant.shop.viewmodel.MerchantMenuViewModel.class);

        initViews();
        setupListeners();

        if (isEditMode && toppingData != null) {
            populateData();
        }

        viewModel.initializeRestaurant(this, () -> {
            viewModel.loadMenu();
        });

        viewModel.getToppingGroups().observe(this, state -> {
            if (state != null && state.isSuccess() && state.getData() != null) {
                toppingGroupsList.clear();
                toppingGroupsList.addAll(state.getData());
            }
        });

        viewModel.getOperationStatus().observe(this, state -> {
            if (state != null) {
                if (state.isSuccess()) {
                    Toast.makeText(this, state.getData(), Toast.LENGTH_SHORT).show();
                    finish();
                } else if (state.isError()) {
                    Toast.makeText(this, state.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tv_title);
        etToppingName = findViewById(R.id.et_topping_name);
        etToppingPrice = findViewById(R.id.et_topping_price);
        tvCategorySelector = findViewById(R.id.tv_category_selector);
        btnSave = findViewById(R.id.btn_save);
        btnDoneHeader = findViewById(R.id.btn_done_header);

        if (isEditMode) {
            if (tvTitle != null) tvTitle.setText("Chỉnh sửa Topping");
        } else {
            if (tvTitle != null) tvTitle.setText("Thêm Topping");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_merchant_topping_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void populateData() {
        etToppingName.setText(toppingData.getName());
        etToppingPrice.setText(String.valueOf((int) toppingData.getPrice()));
        groupId = toppingData.getCategoryId();
        if (categoryName != null) {
            tvCategorySelector.setText(categoryName);
            tvCategorySelector.setTextColor(Color.BLACK);
        }
        checkInputs();
    }

    private void setupListeners() {
        tvCategorySelector.setOnClickListener(v -> showCategoryPopup());

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etToppingName.addTextChangedListener(watcher);
        etToppingPrice.addTextChangedListener(watcher);

        btnSave.setOnClickListener(v -> performSave());
        btnDoneHeader.setOnClickListener(v -> performSave());
    }

    private void checkInputs() {
        String name = etToppingName.getText().toString().trim();
        String price = etToppingPrice.getText().toString().trim();

        if (!name.isEmpty() && !price.isEmpty()) {
            btnSave.setEnabled(true);
            btnSave.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F24405")));
        } else {
            btnSave.setEnabled(false);
            btnSave.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D1D5DB")));
        }
    }

    private void showCategoryPopup() {
        PopupMenu popup = new PopupMenu(this, tvCategorySelector);
        for (int i = 0; i < toppingGroupsList.size(); i++) {
            com.example.uitpayapp.merchant.shop.shop_model.ToppingGroup group = toppingGroupsList.get(i);
            popup.getMenu().add(0, i, 0, group.getName());
        }
        popup.setOnMenuItemClickListener(item -> {
            int index = item.getItemId();
            com.example.uitpayapp.merchant.shop.shop_model.ToppingGroup selected = toppingGroupsList.get(index);
            tvCategorySelector.setText(selected.getName());
            tvCategorySelector.setTextColor(Color.BLACK);
            groupId = selected.getId();
            return true;
        });
        popup.show();
    }

    private void performSave() {
        String name = etToppingName.getText().toString().trim();
        String priceStr = etToppingPrice.getText().toString().trim();
        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        double price = Double.parseDouble(priceStr);
        if (isEditMode && toppingData != null) {
            viewModel.updateTopping(toppingData.getId(), name, price);
        } else {
            if (groupId == null) {
                Toast.makeText(this, "Vui lòng chọn nhóm topping", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.addToppingToGroup(groupId, name, price);
        }
    }
}
