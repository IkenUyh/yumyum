package com.example.uitpayapp.merchant.shop;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.uitpayapp.R;

public class AddMerchantCategoryActivity extends AppCompatActivity {

    private EditText etCategoryName;
    private TextView tvTitle, tvExampleHint, tvFooterHint;
    private boolean isToppingGroup = false;
    private boolean isEditMode = false;
    private String existingName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_merchant_category);

        isToppingGroup = getIntent().getBooleanExtra("is_topping_group", false);
        isEditMode = getIntent().getBooleanExtra("is_edit_mode", false);
        existingName = getIntent().getStringExtra("category_name");

        initViews();
        
        if (isEditMode && existingName != null) {
            etCategoryName.setText(existingName);
            // Move cursor to end
            etCategoryName.setSelection(existingName.length());
        }
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        tvTitle = findViewById(R.id.tv_title);
        etCategoryName = findViewById(R.id.et_category_name);
        tvExampleHint = findViewById(R.id.tv_example_hint);
        tvFooterHint = findViewById(R.id.tv_footer_hint);

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_merchant_category_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            String name = etCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            } else {
                String message = isEditMode ? "Đã cập nhật thành công!" : "Đã lưu thành công!";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
