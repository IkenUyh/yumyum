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

        List<Restaurant> restaurants = generateMockRestaurants();
        AllBrandsAdapter adapter = new AllBrandsAdapter(restaurants, restaurant -> {
            Intent intent = new Intent(this, StoreDetailActivity.class);
            intent.putExtra(StoreDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
            startActivity(intent);
        });
        rvBrands.setAdapter(adapter);
    }

    private List<Restaurant> generateMockRestaurants() {
        // Reuse the dummy food list
        List<FoodMenuItem> dummyFoods = Arrays.asList(
                new FoodMenuItem("1", "Gà rán", 50000, R.drawable.img_food_chicken, "1 MIẾNG GÀ RÁN GIÒN + 1 GÀ POPCORN..."),
                new FoodMenuItem("2", "Pizza", 150000, R.drawable.img_food_pizza, "Pizza thập cẩm..."),
                new FoodMenuItem("3", "Trà sữa", 45000, R.drawable.img_food_bubbletea, "Trà sữa trân châu...")
        );

        return Arrays.asList(
                new Restaurant("Phúc Long - Giga Mall", "PL", Color.parseColor("#00603A"), "Trà & Cà phê", dummyFoods, R.drawable.img_food_bubbletea, 4.8, 1250, 25, "Giga Mall, Thủ Đức"),
                new Restaurant("Gà Rán Popeyes", "PP", Color.parseColor("#E65100"), "Gà rán", dummyFoods, R.drawable.img_food_chicken, 4.5, 980, 20, "Võ Văn Ngân, Thủ Đức"),
                new Restaurant("The Pizza Company", "PC", Color.parseColor("#1B5E20"), "Pizza & Pasta", dummyFoods, R.drawable.img_food_pizza, 4.2, 450, 35, "Vincom Thủ Đức"),
                new Restaurant("Highlands Coffee", "HL", Color.parseColor("#B71C1C"), "Trà & Cà phê", dummyFoods, R.drawable.img_food_coffee, 4.7, 3200, 15, "Làng Đại Học"),
                new Restaurant("KFC - Xa lộ Hà Nội", "KFC", Color.parseColor("#D32F2F"), "Gà rán", dummyFoods, R.drawable.img_food_chicken, 4.4, 2100, 25, "Xa lộ Hà Nội, Quận 9"),
                new Restaurant("Domino's Pizza", "DP", Color.parseColor("#0D47A1"), "Pizza", dummyFoods, R.drawable.img_food_pizza, 4.6, 850, 30, "Lê Văn Việt, Quận 9")
        );
    }
}
