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

        RecyclerView rv = findViewById(R.id.rv_all_categories);
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        AllCategoriesAdapter adapter = new AllCategoriesAdapter(new ArrayList<>(), category -> {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY, category.getCategoryName());
            intent.putExtra(CategoryActivity.EXTRA_SELECTED_CATEGORY_ID, category.getCategoryId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btn_retry).setOnClickListener(v -> loadCategories(adapter));

        loadCategories(adapter);
    }

    private void loadCategories(AllCategoriesAdapter adapter) {
        View loadingView = findViewById(R.id.layout_loading);
        View errorView = findViewById(R.id.layout_error);
        RecyclerView rv = findViewById(R.id.rv_all_categories);

        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);

        com.example.uitpayapp.home.network.CategoryApiService apiService = 
            com.example.uitpayapp.network.RetrofitClient.getCategoryApiService();

        apiService.getCategoryFoodCounts().enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.CategoryFoodCountResponseDTO>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.CategoryFoodCountResponseDTO>>> call, 
                                   retrofit2.Response<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.CategoryFoodCountResponseDTO>>> response) {
                loadingView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    adapter.updateData(response.body().getData());
                    rv.setVisibility(View.VISIBLE);
                } else {
                    errorView.setVisibility(View.VISIBLE);
                    android.widget.TextView tvError = findViewById(R.id.tv_error_message);
                    tvError.setText("Không thể lấy dữ liệu (Lỗi: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.food.models.responses.CategoryFoodCountResponseDTO>>> call, Throwable t) {
                loadingView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                android.widget.TextView tvError = findViewById(R.id.tv_error_message);
                tvError.setText("Không kết nối được server");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
