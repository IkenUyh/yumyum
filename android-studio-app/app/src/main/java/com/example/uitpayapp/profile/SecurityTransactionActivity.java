package com.example.uitpayapp.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;

public class SecurityTransactionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_security);
        View topBar = findViewById(R.id.top_bar_transaction_security);
        View mainContainer = findViewById(R.id.transaction_security_container);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        //Thanh top bar
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView)topBar.findViewById(R.id.top_bar_title)).setText("Bảo mật giao dịch");
        //Nut lich su
        View btn_transaction_history = findViewById(R.id.btn_transaction_history);
        btn_transaction_history.setOnClickListener(v ->HanleTransactionHistoryClick());
        ((ImageView)btn_transaction_history.findViewById(R.id.menu_icon)).setImageResource(R.drawable.ic_history_24px);
        ((TextView)btn_transaction_history.findViewById(R.id.menu_title)).setText("Lịch sử giao dịch");
    }
    private void HanleTransactionHistoryClick() {
    }
}
