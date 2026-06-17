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

public class AddMerchantToppingActivity extends AppCompatActivity {

    private EditText etToppingName, etToppingPrice;
    private TextView tvCategorySelector, btnSave, btnDoneHeader, tvTitle;
    private boolean isEditMode = false;
    private MerchantMenuItem toppingData;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_add_merchant_topping);

        isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);
        toppingData = (MerchantMenuItem) getIntent().getSerializableExtra("topping_data");
        categoryName = getIntent().getStringExtra("category_name");

        initViews();
        setupListeners();

        if (isEditMode && toppingData != null) {
            populateData();
        }
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
        String[] categories = {"Ban Muốn Dùng Cơm Nào", "Topping Thêm", "Chọn Size", "Mức đường", "Mức đá"};
        for (String item : categories) {
            popup.getMenu().add(item);
        }
        popup.setOnMenuItemClickListener(item -> {
            tvCategorySelector.setText(item.getTitle());
            tvCategorySelector.setTextColor(Color.BLACK);
            return true;
        });
        popup.show();
    }

    private void performSave() {
        if (etToppingName.getText().toString().trim().isEmpty() || etToppingPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        String message = isEditMode ? "Đã cập nhật topping thành công!" : "Đã lưu topping thành công!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
