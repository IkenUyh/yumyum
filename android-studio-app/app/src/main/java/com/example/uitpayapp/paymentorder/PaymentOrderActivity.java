package com.example.uitpayapp.paymentorder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentOrderActivity extends AppCompatActivity {

    private RecyclerView rvPaymentMethods;
    private PaymentMethodAdapter adapter;
    private List<PaymentMethod> paymentMethods;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_order);
        initView();
        setUpDataPayment();
        setupRecyclerView();
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_payment_order);
        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        tvTitle.setText("Thứ tự thanh toán tự động");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        View bottomPanel = findViewById(R.id.bottom_panel);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom+10;
            if (bottomPanel != null) {
                bottomPanel.setPadding(bottomPanel.getPaddingLeft(), bottomPanel.getPaddingTop(), bottomPanel.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
    }

    private void setUpDataPayment() {
        paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethod("Số dư sinh lời", "Số dư: 0đ", R.drawable.ic_accmulated_balance, true));
        paymentMethods.add(new PaymentMethod("Zalopay", "Số dư: 0đ", R.drawable.ic_wallet, true));
        paymentMethods.add(new PaymentMethod("VCB", "Số dư: 0đ", R.drawable.ic_link_bank, true));
    }

    private void setupRecyclerView() {
        rvPaymentMethods = findViewById(R.id.rvPaymentMethods);
        adapter = new PaymentMethodAdapter(paymentMethods, viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        });
        rvPaymentMethods.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adapter.onItemMove(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        };
        //cho helper giu callback
        itemTouchHelper = new ItemTouchHelper(callback);
        //kem helper vo rv
        itemTouchHelper.attachToRecyclerView(rvPaymentMethods);
    }
}