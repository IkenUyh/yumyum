package com.example.uitpayapp.home.exchange_voucher;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.List;

public class ExchangeVoucherActivity extends AppCompatActivity {

    private TextView btnClose;
    private LinearLayout tabUitPay, tabPartner;
    private TextView tvTabUitPay, tvTabPartner;
    private View indicatorUitPay, indicatorPartner;

    private RecyclerView rvExchangeVoucher;
    private ExchangeVoucherAdapter adapter;
    private List<ExchangeVoucherItem> currentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_voucher);

        initViews();
        setupRecyclerView();
        setupListeners();

        selectTabUitPay();
    }

    private void initViews() {
        btnClose = findViewById(R.id.btn_close);

        tabUitPay = findViewById(R.id.tab_uit_pay);
        tvTabUitPay = findViewById(R.id.tv_tab_uit_pay);
        indicatorUitPay = findViewById(R.id.indicator_uit_pay);

        tabPartner = findViewById(R.id.tab_partner);
        tvTabPartner = findViewById(R.id.tv_tab_partner);
        indicatorPartner = findViewById(R.id.indicator_partner);

        rvExchangeVoucher = findViewById(R.id.rv_exchange_voucher);
    }

    private void setupRecyclerView() {
        currentList = new ArrayList<>();
        rvExchangeVoucher.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExchangeVoucherAdapter(currentList, item -> {
            Toast.makeText(this, "Bạn đã chọn đổi: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
        rvExchangeVoucher.setAdapter(adapter);
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());

        tabUitPay.setOnClickListener(v -> selectTabUitPay());
        tabPartner.setOnClickListener(v -> selectTabPartner());
    }

    private void selectTabUitPay() {
        tvTabUitPay.setTextColor(Color.parseColor("#0A46A6"));
        tvTabUitPay.setTypeface(null, android.graphics.Typeface.BOLD);
        indicatorUitPay.setBackgroundColor(Color.parseColor("#0A46A6"));

        tvTabPartner.setTextColor(Color.parseColor("#757575"));
        tvTabPartner.setTypeface(null, android.graphics.Typeface.NORMAL);
        indicatorPartner.setBackgroundColor(Color.parseColor("#00000000"));

        loadUitPayVouchers();
    }

    private void selectTabPartner() {
        tvTabPartner.setTextColor(Color.parseColor("#0A46A6"));
        tvTabPartner.setTypeface(null, android.graphics.Typeface.BOLD);
        indicatorPartner.setBackgroundColor(Color.parseColor("#0A46A6"));

        tvTabUitPay.setTextColor(Color.parseColor("#757575"));
        tvTabUitPay.setTypeface(null, android.graphics.Typeface.NORMAL);
        indicatorUitPay.setBackgroundColor(Color.parseColor("#00000000"));

        loadPartnerVouchers();
    }

    private void loadUitPayVouchers() {
        currentList.clear();
        currentList.add(new ExchangeVoucherItem(1, R.drawable.ic_uitpay_demo, "Giảm 20.000đ thanh toán hóa đơn Điện", 500));
        currentList.add(new ExchangeVoucherItem(2, R.drawable.ic_uitpay_demo, "Giảm 50.000đ khi thanh toán Học phí", 1000));
        currentList.add(new ExchangeVoucherItem(3, R.drawable.ic_uitpay_demo, "Hoàn 10% tối đa 100.000đ nạp thẻ", 800));
        currentList.add(new ExchangeVoucherItem(4, R.drawable.ic_uitpay_demo, "Voucher giảm 15.000đ thanh toán Nước", 400));
        adapter.notifyDataSetChanged();
    }

    private void loadPartnerVouchers() {
        currentList.clear();
        currentList.add(new ExchangeVoucherItem(5, R.drawable.ic_search, "Highlands Coffee giảm 20%", 300));
        currentList.add(new ExchangeVoucherItem(6, R.drawable.ic_search, "CGV Mua 2 tính tiền 1", 1200));
        currentList.add(new ExchangeVoucherItem(7, R.drawable.ic_search, "ShopeeFood Freeship 15k", 200));
        adapter.notifyDataSetChanged();
    }
}