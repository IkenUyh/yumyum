package com.example.uitpayapp.merchant.shop;

import android.content.SharedPreferences;
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
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;

public class AddMerchantDishActivity extends AppCompatActivity {

    private ImageView ivDishImage;
    private EditText etDishName, etDishPrice, etDishDescription;
    private TextView tvCategory, tvSalesTime, tvToppingGroup;

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
        setContentView(R.layout.activity_add_merchant_dish);

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        ivDishImage = findViewById(R.id.iv_dish_image);
        etDishName = findViewById(R.id.et_dish_name);
        etDishPrice = findViewById(R.id.et_dish_price);
        etDishDescription = findViewById(R.id.et_dish_description);
        tvCategory = findViewById(R.id.tv_category_selector);
        tvSalesTime = findViewById(R.id.tv_sales_time_selector);
        tvToppingGroup = findViewById(R.id.tv_topping_group_selector);
        View mainView = findViewById(R.id.add_merchant_dish_container);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        tvSalesTime.setText("Cả ngày");

        findViewById(R.id.btn_pick_image).setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            if (validateInput()) {
                Toast.makeText(this, "Đã lưu món ăn thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        tvCategory.setOnClickListener(v -> showCategoryPopup());
        tvSalesTime.setOnClickListener(v -> showSalesTimePopup());
        tvToppingGroup.setOnClickListener(v -> showToppingGroupPopup());
    }

    private void showCategoryPopup() {
        PopupMenu popup = new PopupMenu(this, tvCategory);
        String[] items = {"FLASH SALE", "Món chính", "Món khai vị", "Tráng miệng", "Đồ uống"};
        for (String item : items) {
            popup.getMenu().add(item);
        }
        popup.setOnMenuItemClickListener(item -> {
            tvCategory.setText(item.getTitle());
            return true;
        });
        popup.show();
    }

    private void showSalesTimePopup() {
        PopupMenu popup = new PopupMenu(this, tvSalesTime);
        String[] items = {"Cả ngày", "Buổi sáng", "Buổi chiều"};
        for (String item : items) {
            popup.getMenu().add(item);
        }
        popup.setOnMenuItemClickListener(item -> {
            tvSalesTime.setText(item.getTitle());
            return true;
        });
        popup.show();
    }

    private void showToppingGroupPopup() {
        PopupMenu popup = new PopupMenu(this, tvToppingGroup);
        String[] items = {"Size", "Topping", "Mức đường", "Mức đá"};
        for (String item : items) {
            popup.getMenu().add(item);
        }
        popup.setOnMenuItemClickListener(item -> {
            tvToppingGroup.setText(item.getTitle());
            tvToppingGroup.setTextColor(getResources().getColor(android.R.color.black));
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
