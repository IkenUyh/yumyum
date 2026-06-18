package com.example.uitpayapp.home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.CartManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_CATEGORY = "extra_selected_category";
    public static final String EXTRA_SELECTED_CATEGORY_ID = "extra_selected_category_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_category);

        // Handle system bars inset
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_header_bar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + 16, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Cart button
        findViewById(R.id.btn_cart).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, CartActivity.class));
        });
        updateCartBadge();

        TabLayout tabLayout = findViewById(R.id.tab_categories);
        ViewPager2 viewPager = findViewById(R.id.view_pager_categories);

        String selectedCategory = getIntent().getStringExtra(EXTRA_SELECTED_CATEGORY);
        long categoryId = getIntent().getLongExtra(EXTRA_SELECTED_CATEGORY_ID, -1L);

        if (selectedCategory != null) {
            TextView tvTitle = findViewById(R.id.tv_category_title);
            tvTitle.setText(selectedCategory);
        } else {
            selectedCategory = "Tất cả";
        }

        List<String> filters = java.util.Arrays.asList("Gần tôi", "Bán chạy", "Đánh giá");

        CategoryPagerAdapter adapter = new CategoryPagerAdapter(this, selectedCategory, categoryId, filters);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(filters.get(position));
        }).attach();

        // Add vertical dividers between tabs
        addTabDividers(tabLayout);
    }

    private void addTabDividers(TabLayout tabLayout) {
        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        tabStrip.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        tabStrip.setDividerDrawable(androidx.core.content.ContextCompat.getDrawable(this, R.drawable.divider_vertical));
        tabStrip.setDividerPadding((int) (10 * getResources().getDisplayMetrics().density));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        TextView tvBadge = findViewById(R.id.tv_global_cart_badge);
        int count = CartManager.getInstance().getTotalItemCount();
        if (count > 0) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(String.valueOf(count));
        } else {
            tvBadge.setVisibility(View.GONE);
        }
    }
}

