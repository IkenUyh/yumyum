package com.example.uitpayapp.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private View layoutEmpty;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private TransactionHistoryAdapter adapter;
    private String currentCategory = "Tất cả";
    private String currentQuery = "";
    // 1. Danh sách gốc (constant)
    private List<TransactionHistory> allTransactions;
    // 2. Danh sách dùng để hiển thị trên màn hình (đưa vào Adapter)
    private List<TransactionHistory> displayTransactions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        tabLayout = findViewById(R.id.layoutTabs);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmpty = findViewById(R.id.tvEmpty);
        recyclerView = findViewById(R.id.recyclerView);
        allTransactions = new ArrayList<>();
        displayTransactions = new ArrayList<>();
        List<Object> groupedList = buildGroupedList(displayTransactions);
        adapter = new TransactionHistoryAdapter(groupedList,true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        createDummyData();
        setupTabs();
        //filterTransactions("Tất cả");
        applyFilter();
        setupBottomNavigation();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        String current = sdf.format(new Date());

        ImageView ivFilter=findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(v->{
            Intent filterintent=new Intent(TransactionHistoryActivity.this,TransactionHistoryFiltered.class);
            startActivity(filterintent);
        });
        EditText edtSearch = findViewById(R.id.etSearchQuery);
        ImageView ivClear = findViewById(R.id.ivClear);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();

                // Hiện hoặc ẩn nút clear
                if (s.length() > 0) {
                    ivClear.setVisibility(View.VISIBLE);
                } else {
                    ivClear.setVisibility(View.GONE);
                }

                applyFilter();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        ivClear.setOnClickListener(v -> {
            edtSearch.setText("");
            currentQuery = "";
            applyFilter();
        });

    }
    private void createDummyData() {

        allTransactions.add(new TransactionHistory("1", R.drawable.ic_giaodich_1,"Rút tiền về thẻ/ tài khoản đã liên kết", 380000, 5000000, "19:28 - 26/10/2024", "Nhận tiền", "Thành công", "Ví UITpay", false));

        allTransactions.add(new TransactionHistory("2", R.drawable.ic_giaodich_1,"FUTA - Thanh toán vé online", 190000, 5000000, "19:27 - 26/10/2024", "Thanh toán", "Thất bại", "Ví UITpay", false));

        allTransactions.add(new TransactionHistory("3",R.drawable.ic_giaodich_1 ,"Nạp tiền vào tài khoản UITpay", 190000, 5190000, "19:27 - 26/10/2024", "Nạp tiền", "Thành công", "MBBank", true));

        allTransactions.add(new TransactionHistory("4", R.drawable.ic_giaodich_1,"Nạp tiền điện thoại Viettel", 50000, 5000000, "10:00 - 25/10/2024", "Điện thoại", "Thành công", "Ví UITpay", false));
        allTransactions.add(new TransactionHistory("5", R.drawable.ic_giaodich_1,"Nạp tiền điện thoại Viettel", 50000, 5000000, "10:00 - 25/10/2024", "Điện thoại", "Thành công", "Ví UITpay", false));
        allTransactions.add(new TransactionHistory("6", R.drawable.ic_giaodich_1,"Nạp tiền điện thoại Viettel", 50000, 5000000, "10:00 - 25/09/2024", "Điện thoại", "Thành công", "Ví UITpay", false));
        allTransactions.add(new TransactionHistory("7", R.drawable.ic_giaodich_1,"Nạp tiền điện thoại Viettel", 50000, 5000000, "10:00 - 25/09/2024", "Điện thoại", "Thành công", "Ví UITpay", false));
        allTransactions.add(new TransactionHistory("8", R.drawable.ic_giaodich_1,"Nạp tiền điện thoại Viettel", 50000, 5000000, "10:00 - 25/09/2024", "Điện thoại", "Thành công", "Ví UITpay", false));

    }

    private void setupTabs() {

        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"),true);
        tabLayout.addTab(tabLayout.newTab().setText("Điện thoại"));
        tabLayout.addTab(tabLayout.newTab().setText("Nạp tiền"));
        tabLayout.addTab(tabLayout.newTab().setText("Nhận tiền"));
        tabLayout.addTab(tabLayout.newTab().setText("Hoàn tiền"));
        tabLayout.addTab(tabLayout.newTab().setText("Rút tiền"));
        tabLayout.addTab(tabLayout.newTab().setText("Chuyển tiền"));
        tabLayout.addTab(tabLayout.newTab().setText("Thanh toán"));
        tabLayout.addTab(tabLayout.newTab().setText("Tài chính"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    String selectedCategory = tab.getText().toString();
                    currentCategory = selectedCategory;
                    applyFilter();                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });
    }

  /*  private void filterTransactions(String category) {
        displayTransactions.clear();
        if (category.trim().equals("Tất cả")) {
            displayTransactions.addAll(allTransactions);
        } else {
            for (TransactionHistory transaction : allTransactions) {
                if (transaction.getCategory().trim().equalsIgnoreCase(category.trim())) {
                    displayTransactions.add(transaction);
                }
            }
        }
        List<Object> groupedList = buildGroupedList(displayTransactions);
        adapter.setData(groupedList);
    }*/

    private void applyFilter() {
        displayTransactions.clear();

        for (TransactionHistory transaction : allTransactions) {

            // 1. Lọc theo category
            boolean matchCategory = currentCategory.equals("Tất cả") ||
                    transaction.getCategory().equalsIgnoreCase(currentCategory);

            // 2. Lọc theo title (search)
            boolean matchSearch;

            if (currentQuery == null || currentQuery.trim().isEmpty()) {
                matchSearch = true; // không search → cho qua hết
            } else {
                matchSearch = transaction.getTitle()
                        .toLowerCase()
                        .contains(currentQuery.toLowerCase());
            }

            // 3. Nếu match cả 2 thì thêm
            if (matchCategory && matchSearch) {
                displayTransactions.add(transaction);
            }
        }

        List<Object> groupedList = buildGroupedList(displayTransactions);
        adapter.setData(groupedList);
        if (displayTransactions.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            if (currentQuery.isEmpty()) {
                tvEmpty.setText("Bạn chưa có giao dịch nào");
            } else {
                tvEmpty.setText("Không tìm thấy giao dịch phù hợp");
            }

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private List<Object> buildGroupedList(List<TransactionHistory> list) {
        List<Object> result = new ArrayList<>();

        Map<String, List<TransactionHistory>> map = new LinkedHashMap<>();

        for (TransactionHistory item : list) {
            String monthYear = item.getDate().substring(item.getDate().length() - 7);

            if (!map.containsKey(monthYear)) {
                map.put(monthYear, new ArrayList<>());
            }
            map.get(monthYear).add(item);
        }

        for (String key : map.keySet()) {
            List<TransactionHistory> items = map.get(key);

            long income = 0;
            long expense = 0;

            for (TransactionHistory t : items) {
                if (t.isIncome()) income += t.getAmount();
                else expense += t.getAmount();
            }

            result.add(new HeaderItem(key, income, expense)); // header
            result.addAll(items); // item
        }

        return result;
    }

    private void setupBottomNavigation() {
        android.widget.LinearLayout navHome = findViewById(R.id.navHome);
        android.widget.LinearLayout navHistory = findViewById(R.id.navHistory);
        android.widget.LinearLayout navGift = findViewById(R.id.navGift);
        android.widget.LinearLayout navAccount = findViewById(R.id.navAccount);

        // 1. Luồng bấm về TRANG CHỦ (Đã mở khóa)
        navHome.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.home.HomeActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // 3. Luồng bấm qua SĂN QUÀ
        navGift.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.gift.GiftActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // 4. Luồng bấm qua TÀI KHOẢN
        navAccount.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.profile.ProfileActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }
}