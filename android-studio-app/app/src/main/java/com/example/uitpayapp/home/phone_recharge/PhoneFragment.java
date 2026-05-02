package com.example.uitpayapp.home.phone_recharge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class PhoneFragment extends Fragment {

    private boolean isRechargeMode = true;
    private String currentRechargeAmount = "5.000đ";
    private int currentBuyCardPrice = 50000;
    private int quantity = 1;
    private TextView tvQuantity, btnMinus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone, container, false);

        LinearLayout layoutNapDt = view.findViewById(R.id.layout_nap_dt);
        LinearLayout layoutMuaThe = view.findViewById(R.id.layout_mua_the);
        TextView tvTabNapDt = view.findViewById(R.id.tv_tab_nap_dt);
        TextView tvTabMuaThe = view.findViewById(R.id.tv_tab_mua_the);

        EditText etPhoneNumber = view.findViewById(R.id.et_phone_number);
        ImageView ivContact = view.findViewById(R.id.iv_contact);
        RecyclerView rvAmounts = view.findViewById(R.id.rv_amounts);

        RecyclerView rvCarriers = view.findViewById(R.id.rv_carriers);
        RecyclerView rvBuyCardAmounts = view.findViewById(R.id.rv_buy_card_amounts);
        tvQuantity = view.findViewById(R.id.tv_quantity);
        btnMinus = view.findViewById(R.id.btn_minus);
        TextView btnPlus = view.findViewById(R.id.btn_plus);

        tvTabNapDt.setOnClickListener(v -> {
            isRechargeMode = true;
            layoutNapDt.setVisibility(View.VISIBLE);
            layoutMuaThe.setVisibility(View.GONE);
            tvTabNapDt.setTextColor(Color.BLACK);
            tvTabNapDt.setTypeface(null, Typeface.BOLD);
            tvTabMuaThe.setTextColor(Color.parseColor("#757575"));
            tvTabMuaThe.setTypeface(null, Typeface.NORMAL);
            updateButtonUI();
        });

        tvTabMuaThe.setOnClickListener(v -> {
            isRechargeMode = false;
            layoutNapDt.setVisibility(View.GONE);
            layoutMuaThe.setVisibility(View.VISIBLE);
            tvTabMuaThe.setTextColor(Color.BLACK);
            tvTabMuaThe.setTypeface(null, Typeface.BOLD);
            tvTabNapDt.setTextColor(Color.parseColor("#757575"));
            tvTabNapDt.setTypeface(null, Typeface.NORMAL);
            updateButtonUI();
        });

        ivContact.setOnClickListener(v -> {
            etPhoneNumber.requestFocus();
            etPhoneNumber.setSelection(etPhoneNumber.getText().length());
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etPhoneNumber, InputMethodManager.SHOW_IMPLICIT);
        });

        List<String> rechargeList = Arrays.asList("5.000đ", "10.000đ", "20.000đ", "30.000đ", "50.000đ", "100.000đ", "200.000đ", "300.000đ", "500.000đ");
        AmountAdapter rechargeAdapter = new AmountAdapter(rechargeList, amount -> {
            currentRechargeAmount = amount;
            if (isRechargeMode) updateButtonUI();
        });
        rvAmounts.setAdapter(rechargeAdapter);
        rvAmounts.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<Integer> carrierList = Arrays.asList(R.drawable.img_viettel, R.drawable.img_mobifone, R.drawable.img_vinaphone);
        CarrierAdapter carrierAdapter = new CarrierAdapter(carrierList);
        rvCarriers.setAdapter(carrierAdapter);
        rvCarriers.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<String> buyCardList = Arrays.asList("10.000đ", "20.000đ", "50.000đ", "100.000đ", "200.000đ", "300.000đ", "500.000đ", "1.000.000đ");
        AmountAdapter buyCardAdapter = new AmountAdapter(buyCardList, amount -> {
            String rawAmount = amount.replace(".", "").replace("đ", "").trim();
            currentBuyCardPrice = Integer.parseInt(rawAmount);
            if (!isRechargeMode) updateButtonUI();
        });
        rvBuyCardAmounts.setAdapter(buyCardAdapter);
        rvBuyCardAmounts.setLayoutManager(new GridLayoutManager(getContext(), 3));

        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateMinusButtonState();
            if (!isRechargeMode) updateButtonUI();
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateMinusButtonState();
                if (!isRechargeMode) updateButtonUI();
            }
        });

        return view;
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
            if (isRechargeMode) {
                buttonText = "Nạp ngay • " + currentRechargeAmount;
            } else {
                int total = currentBuyCardPrice * quantity;
                buttonText = "Mua ngay • " + String.format("%,d", total).replace(",", ".") + "đ";
            }
            ((PhoneRechargeActivity) getActivity()).updateRechargeButton(buttonText);
        }
    }
}