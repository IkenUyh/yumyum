package com.example.uitpayapp.merchant.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.home_model.SellerHistoryOrder;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView rvOrders;
    private SellerHistoryOrderAdapter adapter;
    private SellerOrderViewModel viewModel;

    private LinearLayout llHistoryFilters;
    private TextView tvFilterAll, tvFilterConfirmed, tvFilterCancelled, tvDateRange;
    private View btnOpenDatePicker;

    private String currentHistoryTypeFilter = "all";
    private Long filterStartDate = null;
    private Long filterEndDate = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        rvOrders = view.findViewById(R.id.rv_orders_history);
        llHistoryFilters = view.findViewById(R.id.ll_history_filters);
        tvFilterAll = view.findViewById(R.id.tv_filter_all);
        tvFilterConfirmed = view.findViewById(R.id.tv_filter_confirmed);
        tvFilterCancelled = view.findViewById(R.id.tv_filter_cancelled);
        tvDateRange = view.findViewById(R.id.tv_date_range);
        btnOpenDatePicker = view.findViewById(R.id.btn_open_date_picker);

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SellerOrderViewModel.class);

        adapter = new SellerHistoryOrderAdapter(getContext(), new ArrayList<>());
        rvOrders.setAdapter(adapter);

        setupFilters();

        viewModel.getHistoryOrders().observe(getViewLifecycleOwner(), orders -> {
            applyHistoryFilters();
        });
    }

    private void setupFilters() {
        tvFilterAll.setOnClickListener(v -> {
            currentHistoryTypeFilter = "all";
            applyHistoryFilters();
        });
        tvFilterConfirmed.setOnClickListener(v -> {
            currentHistoryTypeFilter = "confirmed";
            applyHistoryFilters();
        });
        tvFilterCancelled.setOnClickListener(v -> {
            currentHistoryTypeFilter = "cancelled";
            applyHistoryFilters();
        });
        btnOpenDatePicker.setOnClickListener(v -> showDateRangePicker());
    }

    private void showDateRangePicker() {
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Chọn khoảng thời gian")
                        .setTheme(R.style.CustomMaterialDatePicker)
                        .setSelection(new Pair<>(
                                MaterialDatePicker.todayInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
                        ))
                        .build();

        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                filterStartDate = selection.first;
                filterEndDate = selection.second;

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                
                String startStr = sdf.format(new Date(filterStartDate));
                String endStr = sdf.format(new Date(filterEndDate));
                
                tvDateRange.setText(startStr + " - " + endStr);
                applyHistoryFilters();
            }
        });

        dateRangePicker.show(getChildFragmentManager(), "DATE_RANGE_PICKER");
    }

    private void applyHistoryFilters() {
        List<SellerHistoryOrder> allOrders = viewModel.getHistoryOrders().getValue();
        if (allOrders == null) return;

        List<SellerHistoryOrder> filteredList = new ArrayList<>();
        
        // Update UI
        tvFilterAll.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvFilterAll.setTextColor(Color.WHITE);
        tvFilterConfirmed.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvFilterConfirmed.setTextColor(Color.WHITE);
        tvFilterCancelled.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvFilterCancelled.setTextColor(Color.WHITE);

        if (currentHistoryTypeFilter.equals("all")) {
            tvFilterAll.setBackgroundResource(R.drawable.bg_tab_selected);
            tvFilterAll.setTextColor(Color.parseColor("#f24405"));
        } else if (currentHistoryTypeFilter.equals("confirmed")) {
            tvFilterConfirmed.setBackgroundResource(R.drawable.bg_tab_selected);
            tvFilterConfirmed.setTextColor(Color.parseColor("#f24405"));
        } else {
            tvFilterCancelled.setBackgroundResource(R.drawable.bg_tab_selected);
            tvFilterCancelled.setTextColor(Color.parseColor("#f24405"));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String todayStr = sdf.format(new Date());

        for (SellerHistoryOrder order : allOrders) {
            boolean matchesType = false;
            if (currentHistoryTypeFilter.equals("all")) {
                matchesType = true;
            } else if (currentHistoryTypeFilter.equals("confirmed")) {
                if ("Đã giao".equalsIgnoreCase(order.getStatus()) || "Đang giao".equalsIgnoreCase(order.getStatus())) {
                    matchesType = true;
                }
            } else if (currentHistoryTypeFilter.equals("cancelled")) {
                if ("Đã hủy".equalsIgnoreCase(order.getStatus())) {
                    matchesType = true;
                }
            }

            boolean matchesDate = true;
            if (filterStartDate != null && filterEndDate != null) {
                try {
                    String dateStr = order.getOrderDate();
                    if ("Hôm nay".equalsIgnoreCase(dateStr)) dateStr = todayStr;
                    
                    Date orderDate = sdf.parse(dateStr);
                    if (orderDate != null) {
                        long utcOrderTime = orderDate.getTime();
                        if (utcOrderTime < filterStartDate || utcOrderTime > filterEndDate) {
                            matchesDate = false;
                        }
                    }
                } catch (ParseException e) {
                    matchesDate = false;
                }
            }

            if (matchesType && matchesDate) {
                filteredList.add(order);
            }
        }
        adapter.updateData(filteredList);
    }
}
