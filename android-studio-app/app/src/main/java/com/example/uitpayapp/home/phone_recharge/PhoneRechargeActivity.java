package com.example.uitpayapp.home.phone_recharge;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PhoneRechargeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_recharge);

        tabLayout = findViewById(R.id.main_tab_layout);
        viewPager = findViewById(R.id.recharge_viewpager);
        btnClose = findViewById(R.id.btn_close);

        android.widget.Button btnRechargeAction = findViewById(R.id.btn_recharge_action);

        RechargePagerAdapter adapter = new RechargePagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(0f);
            page.setVisibility(View.VISIBLE);
            if (position >= -1 && position <= 1) {
                page.setAlpha(1 - Math.abs(position));
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Điện thoại");
                    break;
                case 1:
                    tab.setText("Data");
                    break;
            }
        }).attach();

        tabLayout.post(() -> {
            ViewGroup slidingTabIndicator = (ViewGroup) tabLayout.getChildAt(0);

            float density = getResources().getDisplayMetrics().density;

            if (slidingTabIndicator.getChildCount() > 0) {
                View tab0 = slidingTabIndicator.getChildAt(0);
                android.widget.LinearLayout.LayoutParams params0 = (android.widget.LinearLayout.LayoutParams) tab0.getLayoutParams();

                params0.leftMargin = (int) (-8 * density);
                tab0.setLayoutParams(params0);
            }

            for (int i = 1; i < slidingTabIndicator.getChildCount(); i++) {
                View tabView = slidingTabIndicator.getChildAt(i);
                android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) tabView.getLayoutParams();
                params.leftMargin = (int) (-18 * density);

                tabView.setLayoutParams(params);
            }
        });

        formatTabs();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                formatTabs();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                formatTabs();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        btnClose.setOnClickListener(v -> finish());

        btnRechargeAction.setOnClickListener(v -> {
            String buttonText = btnRechargeAction.getText().toString();

            String rawAmount = buttonText.replace("Nạp ngay • ", "")
                    .replace("Mua ngay • ", "")
                    .replace(".", "")
                    .replace("đ", "").trim();

            boolean isRecharge = buttonText.contains("Nạp ngay");
            boolean isBuyCard = buttonText.contains("Mua ngay");

            boolean isDataTab = (viewPager.getCurrentItem() == 1);

            android.widget.EditText etPhone = findViewById(R.id.et_phone_number);
            String phoneNumber = "0329815572";
            if (etPhone != null && !etPhone.getText().toString().trim().isEmpty()) {
                phoneNumber = etPhone.getText().toString().trim();
            }

            android.content.Intent intent = new android.content.Intent(
                    PhoneRechargeActivity.this,
                    com.example.uitpayapp.home.money_transfer.TransferConfirmationActivity.class
            );

            intent.putExtra("KEY_AMOUNT", rawAmount);
            intent.putExtra("KEY_NAME", phoneNumber);
            intent.putExtra("KEY_IS_RECHARGE", isRecharge);
            intent.putExtra("KEY_IS_BUY_CARD", isBuyCard);
            intent.putExtra("KEY_IS_DATA", isDataTab);

            startActivity(intent);
        });
    }

    private void formatTabs() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.view != null) {
                for (int j = 0; j < tab.view.getChildCount(); j++) {
                    View child = tab.view.getChildAt(j);
                    if (child instanceof TextView) {
                        ((TextView) child).setAllCaps(false);
                        ((TextView) child).setTypeface(Typeface.DEFAULT_BOLD);
                        ((TextView) child).setTextSize(15);
                    }
                }
            }
        }
    }

    public void updateRechargeButton(String fullText) {
        Button btnRecharge = findViewById(R.id.btn_recharge_action);
        if (btnRecharge != null) {
            btnRecharge.setText(fullText);
        }
    }

    private static class RechargePagerAdapter extends FragmentStateAdapter {
        public RechargePagerAdapter(@NonNull AppCompatActivity accomplishment) {
            super(accomplishment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return new DataFragment();
            }
            return new PhoneFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}