package com.example.uitpayapp.merchant.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuCategory;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;
import com.example.uitpayapp.merchant.shop.viewmodel.MerchantMenuViewModel;

import java.util.List;

public class AddMerchantDishActivity extends AppCompatActivity {

    private ImageView ivDishImage;
    private EditText etDishName, etDishPrice, etDishDescription;
    private TextView tvCategory, tvTitle;
    private boolean isEditMode = false;
    private MerchantMenuItem dishData;
    private String categoryName;
    private Long categoryId;
    private final List<MerchantMenuCategory> categoriesList = new java.util.ArrayList<>();
    private MerchantMenuViewModel viewModel;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    if (ivDishImage != null) {
                        ivDishImage.setImageTintList(null);
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.yumyum_demo_logo)
                                .into(ivDishImage);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_add_merchant_dish);

        isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);
        dishData = (MerchantMenuItem) getIntent().getSerializableExtra("dish_data");
        categoryName = getIntent().getStringExtra("category_name");

        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(com.example.uitpayapp.merchant.shop.viewmodel.MerchantMenuViewModel.class);

        initViews();
        
        if (isEditMode && dishData != null) {
            populateData();
        }

        viewModel.initializeRestaurant(this, () -> {
            viewModel.loadMenu();
        });

        viewModel.getDishCategories().observe(this, state -> {
            if (state != null && state.isSuccess() && state.getData() != null) {
                categoriesList.clear();
                categoriesList.addAll(state.getData());
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
        ivDishImage = findViewById(R.id.iv_dish_image);
        etDishName = findViewById(R.id.et_dish_name);
        etDishPrice = findViewById(R.id.et_dish_price);
        etDishDescription = findViewById(R.id.et_dish_description);
        tvCategory = findViewById(R.id.tv_category_selector);
        if (isEditMode) {
            tvTitle.setText("Chỉnh sửa món ăn");
        } else {
            tvTitle.setText("Thêm món mới");
        }

        View mainView = findViewById(R.id.add_merchant_dish_container);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        findViewById(R.id.btn_pick_image).setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            if (validateInput()) {
                String name = etDishName.getText().toString().trim();
                String priceStr = etDishPrice.getText().toString().trim();
                String desc = etDishDescription.getText().toString().trim();
                double price = Double.parseDouble(priceStr);
                
                if (isEditMode && dishData != null) {
                    viewModel.updateFood(dishData.getId(), name, desc, price, categoryId);
                } else {
                    viewModel.createFood(name, desc, price, categoryId);
                }
            }
        });
        
        tvCategory.setOnClickListener(v -> showCategoryPopup());
    }

    private void populateData() {
        etDishName.setText(dishData.getName());
        etDishPrice.setText(String.valueOf((int) dishData.getPrice()));
        etDishDescription.setText(dishData.getDescription() != null ? dishData.getDescription() : "");
        categoryId = dishData.getCategoryId();
        if (categoryName != null) {
            tvCategory.setText(categoryName);
        }
        if (dishData.getImageUrl() != null && !dishData.getImageUrl().isEmpty()) {
            ivDishImage.setImageTintList(null);
            Glide.with(this)
                    .load(dishData.getImageUrl())
                    .placeholder(R.drawable.yumyum_demo_logo)
                    .into(ivDishImage);
        } else if (dishData.getImageRes() != 0) {
            ivDishImage.setImageTintList(null);
            ivDishImage.setImageResource(dishData.getImageRes());
        }
    }

    private void showCategoryPopup() {
        PopupMenu popup = new PopupMenu(this, tvCategory);
        for (int i = 0; i < categoriesList.size(); i++) {
            MerchantMenuCategory cat = categoriesList.get(i);
            popup.getMenu().add(0, i, 0, cat.getCategoryName());
        }
        popup.setOnMenuItemClickListener(item -> {
            int index = item.getItemId();
            MerchantMenuCategory selected = categoriesList.get(index);
            tvCategory.setText(selected.getCategoryName());
            categoryId = selected.getId();
            return true;
        });
        popup.show();
    }

    private boolean validateInput() {
        if (etDishName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên món", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDishPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập giá món", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
