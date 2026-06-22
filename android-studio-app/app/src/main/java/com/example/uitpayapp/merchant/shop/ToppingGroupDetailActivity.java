package com.example.uitpayapp.merchant.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuCategory;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;

import java.util.ArrayList;
import java.util.List;

public class ToppingGroupDetailActivity extends AppCompatActivity {

    private TextView tvGroupName;
    private RecyclerView rvToppings;
    private MerchantMenuAdapter adapter;
    private String groupName;
    private Long groupId;
    private List<MerchantMenuItem> toppings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_topping_group_detail);
        groupId = getIntent().getLongExtra("group_id", -1L);
        groupName = getIntent().getStringExtra("group_name");
        toppings = (List<MerchantMenuItem>) getIntent().getSerializableExtra("toppings");

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        tvGroupName = findViewById(R.id.tv_group_name);
        rvToppings = findViewById(R.id.rv_toppings);
        tvGroupName.setText(groupName);

        findViewById(R.id.btn_edit_group).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMerchantCategoryActivity.class);
            intent.putExtra("is_edit_mode", true);
            intent.putExtra("is_topping_group", true);
            intent.putExtra("category_name", groupName);
            intent.putExtra("category_id", groupId);
            startActivity(intent);
        });

        List<MerchantMenuCategory> categories = new ArrayList<>();
        // Set category name to groupName so it passes correctly to sub-items if needed
        categories.add(new MerchantMenuCategory(groupName, toppings));

        adapter = new MerchantMenuAdapter(categories, true);
        rvToppings.setLayoutManager(new LinearLayoutManager(this));
        rvToppings.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topping_group_detail_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
