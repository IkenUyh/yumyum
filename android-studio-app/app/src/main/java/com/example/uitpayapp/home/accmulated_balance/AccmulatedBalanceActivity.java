package com.example.uitpayapp.home.accmulated_balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.deposit_withdraw.DepositWithdrawActivity;

import java.util.ArrayList;
import java.util.List;

public class AccmulatedBalanceActivity extends AppCompatActivity {
    LinearLayout detail_accmulated_balance;
    boolean isBalanceVisible = false;
    private static final long MOCK_BALANCE = 10000000; // 10 triệu
    private static final double INTEREST_RATE = 3.8; // 3.8%/năm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accmulated_balance);
        View topBar = findViewById(R.id.layout_header);
        View mainContainer = findViewById(R.id.accmulated_balance_screen_main_data);

        // Chỉ áp dụng padding cho thanh điều hướng dưới cùng
        if (mainContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
                Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                int safeBottomPadding = navInsets.bottom + 10;
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), safeBottomPadding);
                return insets;
            });
        }

        detail_accmulated_balance = findViewById(R.id.detail_accmulated_balance);
        topBar.findViewById(R.id.btn_close).setOnClickListener(v -> this.finish());
        findViewById(R.id.show_detail_accmulated_balance).setOnClickListener(v -> ShowDetailAccmulated());

        // Xử lý nút ẩn hiện số dư
        TextView tvBalance = findViewById(R.id.tv_balance_hidden);
        ImageView btnToggle = findViewById(R.id.btn_toggle_balance_visibility);
        btnToggle.setOnClickListener(v -> {
            isBalanceVisible = !isBalanceVisible;
            if (isBalanceVisible) {
                tvBalance.setText(formatCurrency(MOCK_BALANCE));
                btnToggle.setImageResource(R.drawable.ic_invisible_eye);
            } else {
                tvBalance.setText("********");
                btnToggle.setImageResource(R.drawable.ic_eye);
            }
        });

        // Xử lý nút nạp tiền
        findViewById(R.id.btn_deposit_accmulated).setOnClickListener(v -> {
            Intent intent = new Intent(this, DepositWithdrawActivity.class);
            startActivity(intent);
        });

        // Xử lý nút rút tiền
        findViewById(R.id.btn_withdraw_accmulated).setOnClickListener(v -> {
            Intent intent = new Intent(this, DepositWithdrawActivity.class);
            intent.putExtra("KEY_TAB", "WITHDRAW");
            startActivity(intent);
        });

        // Cập nhật lãi suất ước tính
        updateInterestInfo();

        // Danh sách lịch sử giao dịch giả lập
        setupTransactionHistory();
    }

    private void updateInterestInfo() {
        TextView tvRate = findViewById(R.id.tv_interest_rate);
        TextView tvEstimated = findViewById(R.id.tv_estimated_profit);
        tvRate.setText(INTEREST_RATE + "%");
        long estimatedMonthly = (long) (MOCK_BALANCE * INTEREST_RATE / 100 / 12);
        tvEstimated.setText("~" + formatCurrency(estimatedMonthly));
    }

    private void setupTransactionHistory() {
        List<AccmulatedTransaction> transactions = new ArrayList<>();
        transactions.add(new AccmulatedTransaction("Nạp tiền từ MB Bank", "07/05/2026", "+2.000.000đ", true));
        transactions.add(new AccmulatedTransaction("Rút tiền về Ví", "05/05/2026", "-500.000đ", false));
        transactions.add(new AccmulatedTransaction("Nạp tiền từ VietinBank", "01/05/2026", "+5.000.000đ", true));
        transactions.add(new AccmulatedTransaction("Tiền lời tháng 4", "30/04/2026", "+31.667đ", true));
        transactions.add(new AccmulatedTransaction("Nạp tiền từ BIDV", "15/04/2026", "+3.000.000đ", true));
        transactions.add(new AccmulatedTransaction("Rút tiền về Ví", "10/04/2026", "-1.000.000đ", false));

        RecyclerView rvHistory = findViewById(R.id.rv_transaction_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(new AccmulatedTransactionAdapter(transactions));
    }

    private String formatCurrency(long amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }

    private void ShowDetailAccmulated() {
        if (detail_accmulated_balance.getVisibility() == View.GONE) {
            detail_accmulated_balance.setVisibility(View.VISIBLE);
            return;
        }
        detail_accmulated_balance.setVisibility(View.GONE);
    }
}