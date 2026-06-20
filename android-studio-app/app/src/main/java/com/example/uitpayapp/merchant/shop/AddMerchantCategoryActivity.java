package com.example.uitpayapp.merchant.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuCategory;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;
import com.example.uitpayapp.merchant.shop.viewmodel.MerchantMenuViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddMerchantCategoryActivity extends AppCompatActivity {

    private EditText etCategoryName;
    private TextView tvTitle, tvExampleHint, tvFooterHint, tvFoodSelector;
    private LinearLayout selectorFoodContainer;

    private boolean isToppingGroup = false;
    private boolean isEditMode = false;
    private String existingName = "";
    private Long categoryId;

    // Món ăn được người dùng chọn từ popup
    private Long selectedFoodId = null;
    private String selectedFoodName = "";

    // Danh sách tất cả món ăn (phẳng) từ ViewModel để hiển thị trong popup
    private final List<MerchantMenuItem> allFoodItems = new ArrayList<>();
    private final List<String> allFoodLabels = new ArrayList<>();  // "Tên món (Danh mục)"

    private MerchantMenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_add_merchant_category);

        isToppingGroup = getIntent().getBooleanExtra("is_topping_group", false);
        isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);
        existingName = getIntent().getStringExtra("category_name");
        categoryId = getIntent().getLongExtra("category_id", -1L);

        viewModel = new androidx.lifecycle.ViewModelProvider(this)
                .get(MerchantMenuViewModel.class);

        initViews();

        if (isEditMode && existingName != null) {
            etCategoryName.setText(existingName);
            etCategoryName.setSelection(existingName.length());
        }

        viewModel.initializeRestaurant(this, () -> viewModel.loadMenu());

        viewModel.getDishCategories().observe(this, state -> {
            if (state != null && state.isSuccess() && state.getData() != null) {
                allFoodItems.clear();
                allFoodLabels.clear();
                for (MerchantMenuCategory cat : state.getData()) {
                    if (cat.getItems() != null) {
                        for (MerchantMenuItem item : cat.getItems()) {
                            allFoodItems.add(item);
                            allFoodLabels.add(item.getName() + " (" + cat.getCategoryName() + ")");
                        }
                    }
                }
            }
        });

        // Quan sát kết quả thao tác (thêm/sửa)
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
        etCategoryName = findViewById(R.id.et_category_name);
        tvExampleHint = findViewById(R.id.tv_example_hint);
        tvFooterHint = findViewById(R.id.tv_footer_hint);
        selectorFoodContainer = findViewById(R.id.selector_food_container);
        tvFoodSelector = findViewById(R.id.tv_food_selector);

        if (isToppingGroup) {
            tvTitle.setText(isEditMode ? "Chỉnh sửa nhóm topping" : "Thêm nhóm topping");
            etCategoryName.setHint("Nhập tên nhóm topping (Chọn size, Thêm món...)");
            tvExampleHint.setText("Ví dụ: Chọn size");
            tvFooterHint.setText("Nhóm topping giúp khách hàng dễ dàng chọn lựa thêm");
        } else {
            tvTitle.setText(isEditMode ? "Chỉnh sửa danh mục" : "Thêm danh mục");
            etCategoryName.setHint("Nhập tên danh mục (Quán bụi phía Nam, Thức ăn...)");
            tvExampleHint.setText("Ví dụ: Phở");
            tvFooterHint.setText("Không nên nhập 'Món chính' hoặc 'Món phụ'");
        }

        if (isToppingGroup && !isEditMode) {
            selectorFoodContainer.setVisibility(View.VISIBLE);
        } else {
            selectorFoodContainer.setVisibility(View.GONE);
        }

        tvFoodSelector.setOnClickListener(v -> showFoodPickerDialog());

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.add_merchant_category_container), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            String name = etCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isToppingGroup) {
                if (isEditMode) {
                    viewModel.updateToppingGroup(categoryId, name, false, 1);
                } else {
                    if (selectedFoodId == null) {
                        Toast.makeText(this,
                                "Vui lòng chọn món ăn để gán nhóm topping!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    viewModel.addToppingGroup(selectedFoodId, name, false, 1,
                            new java.util.ArrayList<>());
                }
            } else {
                if (isEditMode) {
                    viewModel.updateCategory(categoryId, name);
                } else {
                    viewModel.createCategory(name);
                }
            }
        });
    }

    private void showFoodPickerDialog() {
        if (allFoodItems.isEmpty()) {
            Toast.makeText(this,
                    "Chưa có món ăn nào. Vui lòng thêm món ăn trước!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String[] labels = allFoodLabels.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Chọn món ăn gán cho nhóm topping")
                .setItems(labels, (dialog, which) -> {
                    MerchantMenuItem chosen = allFoodItems.get(which);
                    selectedFoodId = chosen.getId();
                    selectedFoodName = chosen.getName();
                    tvFoodSelector.setText(selectedFoodName);
                    tvFoodSelector.setTextColor(getResources().getColor(
                            android.R.color.black, getTheme()));
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
