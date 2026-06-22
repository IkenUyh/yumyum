package com.example.uitpayapp.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_adapters.AllBrandsAdapter;
import com.example.uitpayapp.home.home_models.FoodMenuItem;
import com.example.uitpayapp.home.home_models.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllBrandsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_all_brands);

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            View header = findViewById(R.id.layout_header_bar);
            if (header != null) {
                header.setPadding(header.getPaddingLeft(), systemBars.top + 16, header.getPaddingRight(), header.getPaddingBottom());
            }
            
            View rv = findViewById(R.id.rv_all_brands);
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

        RecyclerView rvBrands = findViewById(R.id.rv_all_brands);
        rvBrands.setLayoutManager(new LinearLayoutManager(this));

        AllBrandsAdapter adapter = new AllBrandsAdapter(new ArrayList<>(), restaurant -> {
            Intent intent = new Intent(this, StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
            startActivity(intent);
        });
        rvBrands.setAdapter(adapter);

        fetchBrands(adapter);
    }

    private void fetchBrands(AllBrandsAdapter adapter) {
        com.example.uitpayapp.network.RetrofitClient.getRestaurantService().getAllRestaurants().enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Restaurant> mappedRestaurants = new ArrayList<>();
                    for (com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO dto : response.body().getData()) {
                        String shortName = dto.getName() != null && dto.getName().length() > 0 ? dto.getName().substring(0, 1) : "A";
                        mappedRestaurants.add(new Restaurant(
                                dto.getId(), dto.getName(), shortName,
                                Color.parseColor("#E4002B"), "Danh mục",
                                new ArrayList<>(), 0,
                                4.5, 100, 30, dto.getAddress(), dto.getImageUrl()));
                    }
                    adapter.updateData(mappedRestaurants);
                } else {
                    android.widget.Toast.makeText(AllBrandsActivity.this, "Lỗi khi tải danh sách thương hiệu", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>> call, Throwable t) {
                android.widget.Toast.makeText(AllBrandsActivity.this, "Lỗi kết nối", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
