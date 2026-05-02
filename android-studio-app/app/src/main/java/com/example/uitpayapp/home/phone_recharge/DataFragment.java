package com.example.uitpayapp.home.phone_recharge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.Arrays;
import java.util.List;

public class DataFragment extends Fragment {

    private boolean isDataRechargeMode = true;
    private String currentDataAmount = "10.000đ";
    private int currentDataCardPrice = 10000;
    private int quantity = 1;
    private TextView tvQuantity, btnMinus;

    // Adapters cho tab Nạp Data
    private DataPackageAdapter adapter1Day, adapter3Days, adapter7Days, adapter30Days;
    // Adapters cho tab Thẻ Data
    private DataPackageAdapter adapterCard1Day, adapterCard3Days, adapterCard7Days, adapterCard30Days;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        LinearLayout layoutNapData = view.findViewById(R.id.layout_nap_data);
        LinearLayout layoutTheData = view.findViewById(R.id.layout_the_data);
        TextView tvTabNapData = view.findViewById(R.id.tv_tab_nap_data);
        TextView tvTabTheData = view.findViewById(R.id.tv_tab_the_data);
        EditText etPhoneNumber = view.findViewById(R.id.et_phone_number_data);
        ImageView ivContact = view.findViewById(R.id.iv_contact_data);

        // Ánh xạ Headers Nạp Data
        TextView tvHeaderNap1 = view.findViewById(R.id.tv_header_nap_1_day);
        TextView tvHeaderNap3 = view.findViewById(R.id.tv_header_nap_3_days);
        TextView tvHeaderNap7 = view.findViewById(R.id.tv_header_nap_7_days);
        TextView tvHeaderNap30 = view.findViewById(R.id.tv_header_nap_30_days);

        // Ánh xạ Nạp Data
        RecyclerView rv1Day = view.findViewById(R.id.rv_data_1_day);
        RecyclerView rv3Days = view.findViewById(R.id.rv_data_3_days);
        RecyclerView rv7Days = view.findViewById(R.id.rv_data_7_days);
        RecyclerView rv30Days = view.findViewById(R.id.rv_data_30_days);

        // Ánh xạ Headers Thẻ Data
        TextView tvHeaderCard1 = view.findViewById(R.id.tv_header_card_1_day);
        TextView tvHeaderCard3 = view.findViewById(R.id.tv_header_card_3_days);
        TextView tvHeaderCard7 = view.findViewById(R.id.tv_header_card_7_days);
        TextView tvHeaderCard30 = view.findViewById(R.id.tv_header_card_30_days);

        // Ánh xạ Thẻ Data
        RecyclerView rvCarriers = view.findViewById(R.id.rv_carriers_data);
        RecyclerView rvCard1Day = view.findViewById(R.id.rv_card_data_1_day);
        RecyclerView rvCard3Days = view.findViewById(R.id.rv_card_data_3_days);
        RecyclerView rvCard7Days = view.findViewById(R.id.rv_card_data_7_days);
        RecyclerView rvCard30Days = view.findViewById(R.id.rv_card_data_30_days);

        // Ánh xạ 2 nút "Tất cả" của 2 bộ lọc
        TextView filterNapAll = view.findViewById(R.id.filter_nap_all);
        TextView filterTheAll = view.findViewById(R.id.filter_the_all);

        tvQuantity = view.findViewById(R.id.tv_quantity_data);
        btnMinus = view.findViewById(R.id.btn_minus_data);
        TextView btnPlus = view.findViewById(R.id.btn_plus_data);

        tvTabNapData.setOnClickListener(v -> {
            isDataRechargeMode = true;
            layoutNapData.setVisibility(View.VISIBLE);
            layoutTheData.setVisibility(View.GONE);
            tvTabNapData.setTextColor(Color.BLACK);
            tvTabNapData.setTypeface(null, Typeface.BOLD);
            tvTabTheData.setTextColor(Color.parseColor("#757575"));
            tvTabTheData.setTypeface(null, Typeface.NORMAL);
            updateButtonUI();
        });

        tvTabNapData.setOnClickListener(v -> {
            isDataRechargeMode = true;
            layoutNapData.setVisibility(View.VISIBLE);
            layoutTheData.setVisibility(View.GONE);
            tvTabNapData.setTextColor(Color.BLACK);
            tvTabNapData.setTypeface(null, Typeface.BOLD);
            tvTabTheData.setTextColor(Color.parseColor("#757575"));
            tvTabTheData.setTypeface(null, Typeface.NORMAL);
            updateButtonUI();

            if (filterNapAll != null) filterNapAll.performClick();
        });

        tvTabTheData.setOnClickListener(v -> {
            isDataRechargeMode = false;
            layoutNapData.setVisibility(View.GONE);
            layoutTheData.setVisibility(View.VISIBLE);
            tvTabTheData.setTextColor(Color.BLACK);
            tvTabTheData.setTypeface(null, Typeface.BOLD);
            tvTabNapData.setTextColor(Color.parseColor("#757575"));
            tvTabNapData.setTypeface(null, Typeface.NORMAL);
            updateButtonUI();

            if (filterTheAll != null) filterTheAll.performClick();
        });

        // DỮ LIỆU CHUNG CHO CÁC GÓI
        List<DataPackageAdapter.DataPack> list1Day = Arrays.asList(
                new DataPackageAdapter.DataPack("3GB", "6 giờ", "10.000đ", false),
                new DataPackageAdapter.DataPack("1,2GB", "1 ngày", "8.000đ", false),
                new DataPackageAdapter.DataPack("2,5GB", "1 ngày", "12.000đ", false),
                new DataPackageAdapter.DataPack("5GB", "1 ngày", "13.000đ", true)
        );
        List<DataPackageAdapter.DataPack> list3Days = Arrays.asList(
                new DataPackageAdapter.DataPack("4GB", "3 ngày", "20.000đ", false),
                new DataPackageAdapter.DataPack("25GB", "3 ngày", "36.000đ", true)
        );
        List<DataPackageAdapter.DataPack> list7Days = Arrays.asList(
                new DataPackageAdapter.DataPack("7GB", "7 ngày", "30.000đ", false),
                new DataPackageAdapter.DataPack("15GB", "7 ngày", "50.000đ", false)
        );
        List<DataPackageAdapter.DataPack> list30Days = Arrays.asList(
                new DataPackageAdapter.DataPack("30GB", "30 ngày", "90.000đ", false),
                new DataPackageAdapter.DataPack("60GB", "30 ngày", "120.000đ", true)
        );

        // ================= SETUP NẠP DATA =================
        adapter1Day = new DataPackageAdapter(list1Day, 0, price -> {
            adapter3Days.clearSelection(); adapter7Days.clearSelection(); adapter30Days.clearSelection();
            currentDataAmount = price;
            if (isDataRechargeMode) updateButtonUI();
        });
        rv1Day.setAdapter(adapter1Day); rv1Day.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter3Days = new DataPackageAdapter(list3Days, -1, price -> {
            adapter1Day.clearSelection(); adapter7Days.clearSelection(); adapter30Days.clearSelection();
            currentDataAmount = price;
            if (isDataRechargeMode) updateButtonUI();
        });
        rv3Days.setAdapter(adapter3Days); rv3Days.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter7Days = new DataPackageAdapter(list7Days, -1, price -> {
            adapter1Day.clearSelection(); adapter3Days.clearSelection(); adapter30Days.clearSelection();
            currentDataAmount = price;
            if (isDataRechargeMode) updateButtonUI();
        });
        rv7Days.setAdapter(adapter7Days); rv7Days.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter30Days = new DataPackageAdapter(list30Days, -1, price -> {
            adapter1Day.clearSelection(); adapter3Days.clearSelection(); adapter7Days.clearSelection();
            currentDataAmount = price;
            if (isDataRechargeMode) updateButtonUI();
        });
        rv30Days.setAdapter(adapter30Days); rv30Days.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // ================= SETUP THẺ DATA =================
        List<Integer> carrierList = Arrays.asList(
                R.drawable.img_viettel,
                R.drawable.img_mobifone,
                R.drawable.img_vinaphone
        );
        CarrierAdapter carrierAdapter = new CarrierAdapter(carrierList);
        rvCarriers.setAdapter(carrierAdapter);
        rvCarriers.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapterCard1Day = new DataPackageAdapter(list1Day, 0, price -> {
            adapterCard3Days.clearSelection(); adapterCard7Days.clearSelection(); adapterCard30Days.clearSelection();
            updateCardPrice(price);
        });
        rvCard1Day.setAdapter(adapterCard1Day); rvCard1Day.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapterCard3Days = new DataPackageAdapter(list3Days, -1, price -> {
            adapterCard1Day.clearSelection(); adapterCard7Days.clearSelection(); adapterCard30Days.clearSelection();
            updateCardPrice(price);
        });
        rvCard3Days.setAdapter(adapterCard3Days); rvCard3Days.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapterCard7Days = new DataPackageAdapter(list7Days, -1, price -> {
            adapterCard1Day.clearSelection(); adapterCard3Days.clearSelection(); adapterCard30Days.clearSelection();
            updateCardPrice(price);
        });
        rvCard7Days.setAdapter(adapterCard7Days); rvCard7Days.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapterCard30Days = new DataPackageAdapter(list30Days, -1, price -> {
            adapterCard1Day.clearSelection(); adapterCard3Days.clearSelection(); adapterCard7Days.clearSelection();
            updateCardPrice(price);
        });
        rvCard30Days.setAdapter(adapterCard30Days); rvCard30Days.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // ================= XỬ LÝ LỌC =================
        // Nạp Data
        TextView[] napFilters = {
                view.findViewById(R.id.filter_nap_all),
                view.findViewById(R.id.filter_nap_30d),
                view.findViewById(R.id.filter_nap_7d),
                view.findViewById(R.id.filter_nap_3d),
                view.findViewById(R.id.filter_nap_1d)
        };
        View[] napHeaders = {null, tvHeaderNap30, tvHeaderNap7, tvHeaderNap3, tvHeaderNap1};
        View[] napLists = {null, rv30Days, rv7Days, rv3Days, rv1Day};
        setupFilterLogic(napFilters, napHeaders, napLists);

        // Thẻ Data
        TextView[] theFilters = {
                view.findViewById(R.id.filter_the_all),
                view.findViewById(R.id.filter_the_30d),
                view.findViewById(R.id.filter_the_7d),
                view.findViewById(R.id.filter_the_3d),
                view.findViewById(R.id.filter_the_1d)
        };
        View[] theHeaders = {null, tvHeaderCard30, tvHeaderCard7, tvHeaderCard3, tvHeaderCard1};
        View[] theLists = {null, rvCard30Days, rvCard7Days, rvCard3Days, rvCard1Day};
        setupFilterLogic(theFilters, theHeaders, theLists);

        // SETUP SỐ LƯỢNG MUA THẺ
        btnPlus.setOnClickListener(v -> {
            quantity++; tvQuantity.setText(String.valueOf(quantity));
            updateMinusButtonState();
            if (!isDataRechargeMode) updateButtonUI();
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--; tvQuantity.setText(String.valueOf(quantity));
                updateMinusButtonState();
                if (!isDataRechargeMode) updateButtonUI();
            }
        });

        return view;
    }

    private void setupFilterLogic(TextView[] filters, View[] headers, View[] lists) {
        for (int i = 0; i < filters.length; i++) {
            final int index = i;
            filters[i].setOnClickListener(v -> {
                for (int j = 0; j < filters.length; j++) {
                    if (j == index) {
                        filters[j].setBackground(getSelectedFilterBg());
                        filters[j].setTextColor(Color.parseColor("#0A46A6"));
                    } else {
                        filters[j].setBackground(getUnselectedFilterBg());
                        filters[j].setTextColor(Color.parseColor("#42526E"));
                    }
                }

                if (index == 0) {
                    for (int j = 1; j < headers.length; j++) {
                        headers[j].setVisibility(View.VISIBLE);
                        lists[j].setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int j = 1; j < headers.length; j++) {
                        headers[j].setVisibility(j == index ? View.VISIBLE : View.GONE);
                        lists[j].setVisibility(j == index ? View.VISIBLE : View.GONE);
                    }
                }
            });
        }
        filters[0].performClick();
    }

    private GradientDrawable getSelectedFilterBg() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(100f);
        drawable.setColor(Color.WHITE);
        drawable.setStroke(3, Color.parseColor("#0A46A6"));
        return drawable;
    }

    private GradientDrawable getUnselectedFilterBg() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(100f);
        drawable.setColor(Color.parseColor("#F4F7FD"));
        return drawable;
    }

    private void updateCardPrice(String price) {
        String rawAmount = price.replace(".", "").replace("đ", "").trim();
        currentDataCardPrice = Integer.parseInt(rawAmount);
        if (!isDataRechargeMode) updateButtonUI();
    }

    private void updateMinusButtonState() {
        if (quantity > 1) {
            btnMinus.setTextColor(Color.parseColor("#0A46A6"));
            btnMinus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F0FE")));
        } else {
            btnMinus.setTextColor(Color.parseColor("#BDBDBD"));
            btnMinus.setBackgroundTintList(null);
        }
    }

    private void updateButtonUI() {
        if (getActivity() instanceof PhoneRechargeActivity) {
            String buttonText;
            if (isDataRechargeMode) {
                buttonText = "Nạp ngay • " + currentDataAmount;
            } else {
                int total = currentDataCardPrice * quantity;
                buttonText = "Mua ngay • " + String.format("%,d", total).replace(",", ".") + "đ";
            }
            ((PhoneRechargeActivity) getActivity()).updateRechargeButton(buttonText);
        }
    }
}