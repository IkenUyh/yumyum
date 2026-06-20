package com.example.uitpayapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchInput;
    private SearchFragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_search_header), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + 16, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        etSearchInput = findViewById(R.id.et_search_input);
        etSearchInput.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        findViewById(R.id.btn_cart).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        setupTabs();
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_search);
        ViewPager2 viewPager = findViewById(R.id.view_pager_search);

        pagerAdapter = new SearchFragmentPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Cửa hàng");
            } else {
                tab.setText("Món ăn");
            }
        }).attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGlobalCartBadge();
    }

    public void updateGlobalCartBadge() {
        TextView tvBadge = findViewById(R.id.tv_global_cart_badge);
        int count = CartManager.getInstance().getTotalItemCount();
        if (count > 0) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(String.valueOf(count));
        } else {
            tvBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public EditText getSearchInput() {
        return etSearchInput;
    }
}
