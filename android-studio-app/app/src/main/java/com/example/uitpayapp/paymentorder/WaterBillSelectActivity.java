package com.example.uitpayapp.paymentorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class WaterBillSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_bill_select);
        setInitView();
        setupRecyclerView();
    }

    private void setInitView() {
        View topBar = findViewById(R.id.top_bar_water_select);
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Chọn khu vực thanh toán");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        View mainContainer = findViewById(R.id.water_bill_select_container);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom+10;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rv_water_providers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<WaterProvider> providers = new ArrayList<>();
        providers.add(new WaterProvider("Nước Đà Nẵng", "Tất cả các quận huyện của Đà Nẵng", R.drawable.ic_uitpay_demo));
        providers.add(new WaterProvider("Nước Hồ Chí Minh", "Tất cả các quận huyện của TP.HCM", R.drawable.ic_uitpay_demo));
        providers.add(new WaterProvider("Nước Hà Nội", "Tất cả các quận huyện của Hà Nội", R.drawable.ic_uitpay_demo));
        providers.add(new WaterProvider("Nước Miền Nam", "Tất cả các tỉnh, thành phố thuộc miền Nam", R.drawable.ic_uitpay_demo));
        providers.add(new WaterProvider("Nước Miền Bắc", "Tất cả các tỉnh, thành phố thuộc miền Bắc", R.drawable.ic_uitpay_demo));
        providers.add(new WaterProvider("Nước Miền Trung", "Tất cả các tỉnh, thành phố thuộc miền Trung", R.drawable.ic_uitpay_demo));

        WaterProviderAdapter adapter = new WaterProviderAdapter(providers, provider -> {
            Intent intent = new Intent(this, WaterBillInputActivity.class);
            intent.putExtra("PROVIDER_NAME", provider.getName());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }
}
