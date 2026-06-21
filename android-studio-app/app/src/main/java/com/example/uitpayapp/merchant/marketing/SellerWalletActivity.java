package com.example.uitpayapp.merchant.marketing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class SellerWalletActivity extends AppCompatActivity {

    private RecyclerView rvTransactionHistory;
    private TransactionAdapter adapter;
    private List<TransactionModel> transactionList;
    private java.math.BigDecimal currentBalance = java.math.BigDecimal.ZERO;
    private Long currentStoreId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_wallet);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        android.content.SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        long storeId = prefs.getLong("current_store_id", -1L);
        if (storeId != -1L) {
            currentStoreId = storeId;
        }
        
        findViewById(R.id.btn_withdraw).setOnClickListener(v -> {
            if (currentBalance == null || currentBalance.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                Toast.makeText(this, "Số dư không đủ để rút", Toast.LENGTH_SHORT).show();
                return;
            }
            com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(SellerWalletActivity.this);
            builder.setBackground(androidx.core.content.ContextCompat.getDrawable(SellerWalletActivity.this, R.drawable.bg_white_rounded_corner));
            builder.setTitle("Chọn phương thức rút tiền");
            String[] options = {"Điều chuyển về Ví Cá Nhân", "Rút về Tài khoản Ngân hàng"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    handleTransferToPersonal();
                } else if (which == 1) {
                    handleWithdrawToBank();
                }
            });
            builder.show();
        });
        View mainContainer= findViewById(R.id.seller_wallet_container);
        View rlHeader = findViewById(R.id.rl_header);
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvTransactionHistory = findViewById(R.id.rv_transaction_history);
        rvTransactionHistory.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new TransactionAdapter(transactionList);
        rvTransactionHistory.setAdapter(adapter);
        
        fetchBalance();
        fetchTransactions();
    }

    private void fetchBalance() {
        com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
        walletRepo.getMerchantBalance(currentStoreId, new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse data) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    if (data != null && data.getBalance() != null) {
                        currentBalance = data.getBalance();
                        android.widget.TextView tvBalance = findViewById(R.id.tv_balance);
                        java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                        tvBalance.setText(format.format(currentBalance) + "đ");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    android.widget.TextView tvBalance = findViewById(R.id.tv_balance);
                    tvBalance.setText("Lỗi tải");
                });
            }
        });
    }

    private void fetchTransactions() {
        com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
        walletRepo.getMerchantTransactionHistory(currentStoreId, new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.wallet.models.responses.TransactionResponse>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.modules.wallet.models.responses.TransactionResponse> data) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    transactionList = new ArrayList<>();
                    java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                    for (com.example.uitpayapp.modules.wallet.models.responses.TransactionResponse tx : data) {
                        String sign = tx.getAmount().compareTo(java.math.BigDecimal.ZERO) >= 0 ? "+" : "";
                        String amountStr = sign + format.format(tx.getAmount()) + "đ";
                        int color = tx.getAmount().compareTo(java.math.BigDecimal.ZERO) >= 0 ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");
                        String dateStr = "N/A";
                        if (tx.getCreatedAt() != null && tx.getCreatedAt().length() >= 10) {
                            dateStr = tx.getCreatedAt().substring(8, 10) + "-" + tx.getCreatedAt().substring(5, 7) + "-" + tx.getCreatedAt().substring(0, 4);
                        }
                        transactionList.add(new TransactionModel(dateStr, tx.getDescription(), amountStr, color));
                    }
                    adapter = new TransactionAdapter(transactionList);
                    rvTransactionHistory.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String errorMessage) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    Toast.makeText(SellerWalletActivity.this, "Lỗi tải lịch sử: " + errorMessage, Toast.LENGTH_SHORT).show();
                    transactionList = new ArrayList<>();
                    adapter = new TransactionAdapter(transactionList);
                    rvTransactionHistory.setAdapter(adapter);
                });
            }
        });
    }

    private void handleTransferToPersonal() {
        promptForAmount("Điều chuyển về Ví Cá Nhân", amount -> {
            com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
            com.example.uitpayapp.modules.wallet.models.requests.MerchantWalletTransferRequest request = 
                new com.example.uitpayapp.modules.wallet.models.requests.MerchantWalletTransferRequest();
            request.setAmount(amount);
            request.setRestaurantId(currentStoreId);
            
            walletRepo.transferMerchantToPersonal(request, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        Toast.makeText(SellerWalletActivity.this, "Đã điều chuyển doanh thu về Ví Cá Nhân!", Toast.LENGTH_LONG).show();
                        fetchBalance();
                        fetchTransactions();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        Toast.makeText(SellerWalletActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private void handleWithdrawToBank() {
        promptForAmount("Rút về Tài khoản Ngân hàng", amount -> {
            com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
            com.example.uitpayapp.modules.wallet.models.requests.MerchantWalletWithdrawRequest request = 
                new com.example.uitpayapp.modules.wallet.models.requests.MerchantWalletWithdrawRequest();
            request.setAmount(amount);
            request.setRestaurantId(currentStoreId);
            
            walletRepo.withdrawMerchantBalance(request, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        Toast.makeText(SellerWalletActivity.this, "Đã gửi yêu cầu rút tiền về Ngân hàng!", Toast.LENGTH_LONG).show();
                        fetchBalance();
                        fetchTransactions();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        Toast.makeText(SellerWalletActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private void promptForAmount(String title, java.util.function.Consumer<java.math.BigDecimal> onConfirm) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Nhập số tiền (VNĐ)...");
        
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (20 * getResources().getDisplayMetrics().density);
        params.setMargins(margin, margin/2, margin, margin/2);
        input.setLayoutParams(params);
        container.addView(input);

        com.google.android.material.dialog.MaterialAlertDialogBuilder builder = new com.google.android.material.dialog.MaterialAlertDialogBuilder(this);
        builder.setBackground(androidx.core.content.ContextCompat.getDrawable(this, R.drawable.bg_white_rounded_corner));
        builder.setTitle(title);
        builder.setView(container);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String val = input.getText().toString();
            try {
                java.math.BigDecimal amount = new java.math.BigDecimal(val);
                if (amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount.compareTo(currentBalance) > 0) {
                    Toast.makeText(this, "Số dư không đủ", Toast.LENGTH_SHORT).show();
                    return;
                }
                onConfirm.accept(amount);
            } catch (Exception e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
