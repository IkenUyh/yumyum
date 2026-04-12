package com.example.uitpayapp.transaction;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private TransactionHistoryAdapter adapter;

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


        recyclerView = findViewById(R.id.recyclerView);
        allTransactions = new ArrayList<>();
        displayTransactions = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(displayTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        createDummyData();
        setupTabs();
        filterTransactions("Tất cả");
        setupBottomNavigation();
    }
    private void createDummyData() {
        // Thứ tự truyền vào: ID, Tiêu đề, Số tiền, Số dư, Thời gian, Category (để lọc Tab), Trạng thái, Nguồn tiền, isIncome (+ hay -)

        allTransactions.add(new TransactionHistory("1", R.drawable.ic_giaodich_1,"Rút tiền về thẻ/ tài khoản đã liên kết", 380000, 5000000, "19:28 - 26/10/2024", "Nhận tiền", "Thành công", "Ví UITpay", false));

        allTransactions.add(new TransactionHistory("2", R.drawable.ic_giaodich_1,"FUTA - Thanh toán vé online", 190000, 5000000, "19:27 - 26/10/2024", "Thanh toán", "Thất bại", "Ví UITpay", false));

        allTransactions.add(new TransactionHistory("3",R.drawable.ic_giaodich_1 ,"Nạp tiền vào tài khoản UITpay", 190000, 5190000, "19:27 - 26/10/2024", "Nạp tiền", "Thành công", "MBBank", true));

        allTransactions.add(new TransactionHistory("4", R.drawable.ic_giaodich_1,"Nạp tiền điện thoại Viettel", 50000, 5000000, "10:00 - 25/10/2024", "Điện thoại", "Thành công", "Ví UITpay", false));
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

        // Bắt sự kiện khi người dùng nhấn vào một Tab bất kỳ
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    String selectedCategory = tab.getText().toString();
                    filterTransactions(selectedCategory); // Gọi hàm lọc dữ liệu
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });
    }

    // Hàm thực hiện chức năng LỌC dữ liệu
    private void filterTransactions(String category) {
        // 1. Xóa sạch dữ liệu đang hiển thị cũ
        displayTransactions.clear();

        // 2. Lọc dữ liệu mới từ danh sách gốc
        if (category.equals("Tất cả")) {
            displayTransactions.addAll(allTransactions); // Lấy hết
        } else {
            // Duyệt qua từng giao dịch, nếu khớp "Category" thì mới cho hiển thị
            for (TransactionHistory transaction : allTransactions) {
                if (transaction.getCategory().equals(category)) {
                    displayTransactions.add(transaction);
                }
            }
        }

        // 3. "Báo cáo" cho Adapter biết dữ liệu đã thay đổi để nó vẽ lại giao diện
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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