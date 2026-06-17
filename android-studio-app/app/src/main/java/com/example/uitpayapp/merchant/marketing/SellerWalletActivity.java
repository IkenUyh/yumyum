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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_wallet);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_withdraw).setOnClickListener(v -> {
            Toast.makeText(this, "Rút tiền thành công", Toast.LENGTH_SHORT).show();
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
        
        loadDummyTransactions();
        
        adapter = new TransactionAdapter(transactionList);
        rvTransactionHistory.setAdapter(adapter);
    }

    private void loadDummyTransactions() {
        transactionList = new ArrayList<>();
        transactionList.add(new TransactionModel("22-12-2024", "Giao dịch trên ShopeeFood", "+298.000đ", Color.parseColor("#4CAF50")));
        transactionList.add(new TransactionModel("18-12-2024", "Rút tiền", "-279.258đ", Color.parseColor("#F44336")));
        transactionList.add(new TransactionModel("17-12-2024", "Giao dịch trên ShopeeFood", "+513.750đ", Color.parseColor("#4CAF50")));
        transactionList.add(new TransactionModel("04-11-2024", "Giao dịch trên ShopeeFood", "+925.192đ", Color.parseColor("#4CAF50")));
    }
}
