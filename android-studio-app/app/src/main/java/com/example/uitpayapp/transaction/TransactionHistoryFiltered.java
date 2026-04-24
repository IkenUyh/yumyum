package com.example.uitpayapp.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TransactionHistoryFiltered extends AppCompatActivity {

    private TransactionHistoryAdapter adapter;
    private List<TransactionHistory> allTransactions;
    private List<TransactionHistory> displayTransactions;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history_filtered);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        allTransactions = new ArrayList<>();
        displayTransactions = new ArrayList<>();
        createDummyData();
        List<Object> list = new ArrayList<>(displayTransactions);
        adapter = new TransactionHistoryAdapter(list, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditText etSearch = findViewById(R.id.etSearchMonth);
        ImageView imCalendar=findViewById(R.id.ivCalendar);
        ImageView imDropDown=findViewById(R.id.ivDropdown);
        Calendar cal = Calendar.getInstance();
        int thang = cal.get(Calendar.MONTH) + 1;
        int nam = cal.get(Calendar.YEAR);
        etSearch.setHint("Tháng " + thang+"/"+nam);
        filterGiaoDichTheoThang(thang, nam);

        etSearch.setFocusable(false);
        etSearch.setClickable(true);

        imDropDown.setOnClickListener(v->openBottomSheetChonThang());
        imCalendar.setOnClickListener(v->openBottomSheetChonThang());
        etSearch.setOnClickListener(v -> openBottomSheetChonThang());

        Button CLoseButton=findViewById(R.id.closebutton);
        CLoseButton.setOnClickListener(v->{
            Intent intent=new Intent(TransactionHistoryFiltered.this,TransactionHistoryActivity.class);
            startActivity(intent);
        });

    }

    private void openBottomSheetChonThang() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        View sheetView = getLayoutInflater().inflate(R.layout.transaction_history_month_filtered, null);
        bottomSheetDialog.setContentView(sheetView);

        ImageView btnClose = sheetView.findViewById(R.id.btnClose);
        RecyclerView recyclerViewThang = sheetView.findViewById(R.id.recyclerViewThang);

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        List<MonthYearClass> listData = generateCalendarData();
        ChonThangAdapter adapter = new ChonThangAdapter(listData, new ChonThangAdapter.OnThangClickListener() {
            @Override
            public void onThangClick(int thang, int nam) {
                EditText etSearchMonth = findViewById(R.id.etSearchMonth);
                etSearchMonth.setText("Tháng " + thang + "/" + nam);

                filterGiaoDichTheoThang(thang, nam);
            }
        });

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == MonthYearClass.TYPE_NAM) {
                    return 3;
                }
                return 1;
            }
        });

        recyclerViewThang.setLayoutManager(manager);
        recyclerViewThang.setAdapter(adapter);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;

            View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheetInternal != null) {
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                ViewGroup.LayoutParams layoutParams = bottomSheetInternal.getLayoutParams();
                layoutParams.height = (int) (screenHeight * 0.6);
                bottomSheetInternal.setLayoutParams(layoutParams);

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });

        bottomSheetDialog.show();
    }

    private void createDummyData() {

        allTransactions.add(new TransactionHistory("1", R.drawable.ic_giaodich_1,"Rút tiền về thẻ/ tài khoản đã liên kết", 380000, 5000000, "19:28 - 26/10/2024", "Nhận tiền", "Thành công", "Ví UITpay", false));

        allTransactions.add(new TransactionHistory("2", R.drawable.ic_giaodich_1,"FUTA - Thanh toán vé online", 190000, 5000000, "19:27 - 26/10/2024", "Thanh toán", "Thất bại", "Ví UITpay", false));

        allTransactions.add(new TransactionHistory("3",R.drawable.ic_giaodich_1 ,"Nạp tiền vào tài khoản UITpay", 190000, 5190000, "19:27 - 26/10/2024", "Nạp tiền", "Thành công", "MBBank", true));

        allTransactions.add(new TransactionHistory("4", R.drawable.ic_giaodich_1,"Nạp tiền điện thoại Viettel", 50000, 5000000, "10:00 - 12/3/2026", "Điện thoại", "Thành công", "Ví UITpay", false));
        displayTransactions.clear();
        displayTransactions.addAll(allTransactions);
        kiemTraGiaoDichTrong();

    }

    private void filterGiaoDichTheoThang(int thangChon, int namChon) {
        displayTransactions.clear();
        for (TransactionHistory gd : allTransactions) {
            if (gd.getMonth() == thangChon && gd.getYear() == namChon) {
                displayTransactions.add(gd);
            }
        }
        List<Object> list = new ArrayList<>(displayTransactions);
        adapter.setData(list);
        kiemTraGiaoDichTrong();
    }
    private List<MonthYearClass> generateCalendarData() {
        List<MonthYearClass> list = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth=Calendar.getInstance().get(Calendar.MONTH);
        for (int year = currentYear; year >= currentYear - 2; year--) {
            list.add(new YearHeader(year));
            for (int month = 12; month >= 1; month--) {
                if(year==currentYear&&month>currentMonth+1) continue;
                list.add(new MonthItem(month, year));
            }
        }
        return list;
    }

    private void kiemTraGiaoDichTrong() {
        View layoutEmpty = findViewById(R.id.emptylayout);

        if (displayTransactions.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}