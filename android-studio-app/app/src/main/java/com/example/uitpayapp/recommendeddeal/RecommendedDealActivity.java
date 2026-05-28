package com.example.uitpayapp.recommendeddeal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecommendedDealActivity extends AppCompatActivity {

    private RecyclerView rvDeals;
    private RecommendedDealAdapter adapter;
    private List<RecommendedDealModel> allDeals;
    private List<RecommendedDealModel> displayDeals;
    private TabLayout tabLayout;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_deal);

        initView();
        setupRecyclerView();
        loadDummyData();
        setupListeners();
    }

    private void initView() {
        rvDeals = findViewById(R.id.rv_recommended_deals);
        tabLayout = findViewById(R.id.tab_recommend_deal);
        etSearch = findViewById(R.id.et_search_deal);
        View topBar = findViewById(R.id.top_bar_recommend_deal);
        View mainContainer = findViewById(R.id.recommend_deal_container);
        findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.top_bar_title);
        if (tvTitle != null) {
            tvTitle.setText("Deal hời cho bạn");
        }
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top,systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
    }

    private void setupRecyclerView() {
        allDeals = new ArrayList<>();
        displayDeals = new ArrayList<>();
        adapter = new RecommendedDealAdapter(displayDeals);
        rvDeals.setLayoutManager(new LinearLayoutManager(this));
        rvDeals.setAdapter(adapter);
    }

    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterDeals();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                filterDeals();
                return true;
            }
            return false;
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void filterDeals() {
        int selectedTab = tabLayout.getSelectedTabPosition();
        String query = etSearch != null ? etSearch.getText().toString().toLowerCase().trim() : "";
        displayDeals.clear();
        List<RecommendedDealModel> filteredList = new ArrayList<>();
        for (RecommendedDealModel deal : allDeals) {
            if (deal.getFoodTitle().toLowerCase().contains(query) || 
                deal.getStoreName().toLowerCase().contains(query)) {
                filteredList.add(deal);
            }
        }
        switch (selectedTab) {
            case 0:
                displayDeals.addAll(filteredList);
                break;
            case 1:
                List<RecommendedDealModel> saleList = new ArrayList<>(filteredList);
                Collections.sort(saleList, (d1, d2) -> Double.compare(d2.getSoldCount(),d1.getSoldCount()));
                displayDeals.addAll(saleList);
                break;
            case 2:
                List<RecommendedDealModel> nearMeList = new ArrayList<>(filteredList);
                Collections.sort(nearMeList, (d1, d2) -> Double.compare(d1.getDistance(), d2.getDistance()));
                displayDeals.addAll(nearMeList);
                break;
        }
        adapter.notifyDataSetChanged();
    }

    private void loadDummyData() {
        allDeals.add(new RecommendedDealModel(
                "Gà Rán Popeyes - Võ Văn Ngân",
                9.1, 9,
                R.drawable.img_food_chicken,
                "-52%",
                "1 MIẾNG GÀ RÁN GIÒN + 1 GÀ POPCORN + 1 KHOAI TÂY CHIÊN",
                100,
                118000.0,
                57000.0
        ));
        
        allDeals.add(new RecommendedDealModel(
                "The Coffee House - Kha Vạn Cân",
                1.2, 10,
                R.drawable.img_food_bubbletea,
                "-30%",
                "Trà Đào Cam Sả (L) + Bánh Mì Que",
                50,
                75000.0,
                52000.0
        ));

        allDeals.add(new RecommendedDealModel(
                "Phúc Long Tea & Coffee",
                3.5, 25,
                R.drawable.img_food_coffee,
                "-20%",
                "Trà Sữa Phúc Long + Thạch Cafe",
                200,
                65000.0,
                52000.0
        ));

        allDeals.add(new RecommendedDealModel(
                "KFC - Đặng Văn Bi",
                0.5, 10,
                R.drawable.img_food_chicken,
                "-15%",
                "Combo Gà Rán Hạnh Phúc",
                80,
                150000.0,
                125000.0
        ));
        filterDeals();
    }
}
