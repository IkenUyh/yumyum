package com.example.uitpayapp.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_adapters.AllCategoriesAdapter;
import com.example.uitpayapp.home.home_models.FoodCategory;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.view.View;

public class AllCategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        
        setContentView(R.layout.activity_all_categories);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            View header = findViewById(R.id.layout_header_bar);
            if (header != null) {
                header.setPadding(header.getPaddingLeft(), systemBars.top + 16, header.getPaddingRight(), header.getPaddingBottom());
            }
            
            View rv = findViewById(R.id.rv_all_categories);
            if (rv != null) {
                int padding8dp = (int) (8 * getResources().getDisplayMetrics().density);
                rv.setPadding(padding8dp, padding8dp, padding8dp, systemBars.bottom + padding8dp);
            }
            
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        List<FoodCategory> categories = new ArrayList<>();
        int defaultIcon = R.drawable.ic_cat_all;
        int defaultColor = Color.parseColor("#E65100");
        categories.add(new FoodCategory("Xôi", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Thức ăn khác", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Trà", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Sữa", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Nước ngọt", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Nước ép trái cây - Sinh tố", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Mì ăn liền", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Lẩu", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Hải sản", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Fastfood", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Đồ nướng", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Đồ chay", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Gỏi - Cuốn - Salad", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Cơm", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Cháo", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Cafe", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Bún - Phở - Hủ tiếu", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Bia rượu", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Bánh mì", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Bánh ngọt", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Bánh bao", defaultIcon, defaultColor));
        categories.add(new FoodCategory("Ăn vặt", defaultIcon, defaultColor));

        RecyclerView rv = findViewById(R.id.rv_all_categories);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        
        AllCategoriesAdapter adapter = new AllCategoriesAdapter(categories, category -> {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY, category.getName());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        rv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
