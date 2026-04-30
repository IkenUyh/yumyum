package com.example.uitpayapp.home.deposit_withdraw;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.money_transfer.TransferSuccessActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.Locale;

public class DepositWithdrawActivity extends AppCompatActivity {

    private String currentAmount = "0";
    private TextView tvAmount;
    private TextView tabDeposit, tabWithdraw;
    private View tabSlider;
    private Button btnContinue;
    private boolean isButtonVisible = false;
    private boolean isAmountHidden = false;
    private TextView tvBalance;
    private String currentPin = "";
    private View[] pinDots = new View[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_withdraw);

        initViews();
        setupTabs();
        setupKeypad();
        setupQuickAmounts();
    }

    private void initViews() {
        tvAmount = findViewById(R.id.tv_amount);
        tabDeposit = findViewById(R.id.tab_deposit);
        tabWithdraw = findViewById(R.id.tab_withdraw);
        tabSlider = findViewById(R.id.tab_slider);
        tvBalance = findViewById(R.id.tv_balance);

        btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setTranslationY(100f);

        btnContinue.setAlpha(0f);
        btnContinue.setVisibility(View.GONE);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        btnContinue.setOnClickListener(v -> {
            showBankSelectionBottomSheet();
        });

        ImageView btnToggleVisibility = findViewById(R.id.btn_toggle_visibility);
        btnToggleVisibility.setOnClickListener(v -> {
            isAmountHidden = !isAmountHidden;

            if (isAmountHidden) {
                btnToggleVisibility.setAlpha(0.5f);
            } else {
                btnToggleVisibility.setAlpha(1.0f);
            }

            updateDisplay();
        });

        updateDisplay();
    }

    private void setupTabs() {
        tabDeposit.setOnClickListener(v -> {
            tabSlider.animate().translationX(0f).setDuration(250).start();
            tabDeposit.setTextColor(Color.parseColor("#0A46A6"));
            tabWithdraw.setTextColor(Color.parseColor("#546E7A"));
        });

        tabWithdraw.setOnClickListener(v -> {
            tabSlider.animate().translationX(tabDeposit.getWidth()).setDuration(250).start();
            tabWithdraw.setTextColor(Color.parseColor("#0A46A6"));
            tabDeposit.setTextColor(Color.parseColor("#546E7A"));
        });
    }

    private void setupQuickAmounts() {
        findViewById(R.id.btn_quick_50k).setOnClickListener(v -> {
            currentAmount = "50000";
            updateDisplay();
        });
        findViewById(R.id.btn_quick_100k).setOnClickListener(v -> {
            currentAmount = "100000";
            updateDisplay();
        });
        findViewById(R.id.btn_quick_200k).setOnClickListener(v -> {
            currentAmount = "200000";
            updateDisplay();
        });
    }

    private void setupKeypad() {
        int[] numberIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_000
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(v -> {
                String number = ((TextView) v).getText().toString();
                handleNumberPress(number);
            });
        }

        findViewById(R.id.btn_delete).setOnClickListener(v -> handleDeletePress());
    }

    private void handleNumberPress(String number) {
        if (currentAmount.equals("0")) {
            if (number.equals("000")) return;
            currentAmount = number;
        } else {
            int maxLength = 12;
            int spaceLeft = maxLength - currentAmount.length();

            if (spaceLeft <= 0) return;

            if (number.length() > spaceLeft) {
                number = number.substring(0, spaceLeft);
            }
            currentAmount += number;
        }
        updateDisplay();
    }

    private void handleDeletePress() {
        if (currentAmount.length() > 1) {
            currentAmount = currentAmount.substring(0, currentAmount.length() - 1);
        } else {
            currentAmount = "0";
        }
        updateDisplay();
    }

    private void updateDisplay() {
        String displayString = currentAmount;
        try {
            long amount = Long.parseLong(currentAmount);
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            displayString = formatter.format(amount);
        } catch (NumberFormatException e) {
            displayString = currentAmount;
        }

        String finalString = displayString + "đ";
        SpannableString spannable = new SpannableString(finalString);

        int startIndex = displayString.length();
        int endIndex = finalString.length();

        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#BDBDBD")),
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(
                new RelativeSizeSpan(0.6f),
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvAmount.setText(spannable);

        int length = currentAmount.length();
        if (length <= 6) {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 56f);
        } else if (length <= 9) {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 44f);
        } else {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);
        }

        if (isAmountHidden) {
            tvBalance.setText("****đ");
        } else {
            tvBalance.setText("1.513đ");
        }

        checkAndToggleContinueButton();
    }

    private void checkAndToggleContinueButton() {
        boolean shouldShow = !currentAmount.equals("0");

        if (shouldShow && !isButtonVisible) {
            isButtonVisible = true;
            btnContinue.setVisibility(View.VISIBLE);
            btnContinue.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(250)
                    .start();

        } else if (!shouldShow && isButtonVisible) {
            isButtonVisible = false;
            btnContinue.animate()
                    .translationY(100f)
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> btnContinue.setVisibility(View.GONE))
                    .start();
        }
    }

    private void showBankSelectionBottomSheet() {
        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog);

        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_banks, null);
        bottomSheetDialog.setContentView(view);

        View bottomSheet = bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btn_close_sheet).setOnClickListener(v -> bottomSheetDialog.dismiss());

        androidx.recyclerview.widget.RecyclerView rvBanks = view.findViewById(R.id.rv_banks);
        rvBanks.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        java.util.List<BankItem> mockBanks = new java.util.ArrayList<>();
        mockBanks.add(new BankItem("MB Bank", "Ngân hàng TMCP Quân đội", R.mipmap.ic_launcher));
        mockBanks.add(new BankItem("VietinBank iPay", "Ngân hàng TMCP Công thương Việt Nam", R.mipmap.ic_launcher));
        mockBanks.add(new BankItem("Techcombank Mobile", "Ngân hàng TMCP Kỹ thương Việt Nam", R.mipmap.ic_launcher));
        mockBanks.add(new BankItem("BIDV SmartBanking", "Ngân hàng TMCP Đầu tư và Phát triển Việt Nam", R.mipmap.ic_launcher));
        mockBanks.add(new BankItem("Agribank Plus", "Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam", R.mipmap.ic_launcher));
        mockBanks.add(new BankItem("digimi", "Ngân hàng số digimi", R.mipmap.ic_launcher));

        BankAdapter adapter = new BankAdapter(mockBanks, bank -> {
            bottomSheetDialog.dismiss();
            showPasscodeBottomSheet(bank.getName(), bank.getIconResId());
        });
        rvBanks.setAdapter(adapter);

        bottomSheetDialog.show();
    }
    private void showPasscodeBottomSheet(String bankName, int bankIconId) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_passcode, null);
        bottomSheetDialog.setContentView(sheetView);

        pinDots[0] = sheetView.findViewById(R.id.dot_1);
        pinDots[1] = sheetView.findViewById(R.id.dot_2);
        pinDots[2] = sheetView.findViewById(R.id.dot_3);
        pinDots[3] = sheetView.findViewById(R.id.dot_4);
        pinDots[4] = sheetView.findViewById(R.id.dot_5);
        pinDots[5] = sheetView.findViewById(R.id.dot_6);

        sheetView.findViewById(R.id.btn_close_passcode).setOnClickListener(v -> bottomSheetDialog.dismiss());

        int[] numberButtonIds = {
                R.id.btn_pin_0, R.id.btn_pin_1, R.id.btn_pin_2, R.id.btn_pin_3, R.id.btn_pin_4,
                R.id.btn_pin_5, R.id.btn_pin_6, R.id.btn_pin_7, R.id.btn_pin_8, R.id.btn_pin_9
        };

        for (int id : numberButtonIds) {
            sheetView.findViewById(id).setOnClickListener(v -> {
                if (currentPin.length() < 6) {
                    String pressedNumber = v.getTag().toString();
                    currentPin += pressedNumber;

                    updatePinDots();

                    // Nếu đã nhập đủ 6 số
                    if (currentPin.length() == 6) {
                        new Handler().postDelayed(() -> {
                            bottomSheetDialog.dismiss();
                            navigateToSuccessScreen(bankName, bankIconId);
                        }, 300);
                    }
                }
            });
        }

        sheetView.findViewById(R.id.btn_pin_delete).setOnClickListener(v -> {
            if (currentPin.length() > 0) {
                currentPin = currentPin.substring(0, currentPin.length() - 1);
                updatePinDots();
            }
        });

        currentPin = "";
        updatePinDots();
        bottomSheetDialog.show();
    }

    private void updatePinDots() {
        int colorBlue = android.graphics.Color.parseColor("#0A46A6");
        int colorGray = android.graphics.Color.parseColor("#E0E0E0");

        for (int i = 0; i < 6; i++) {
            if (i < currentPin.length()) {
                pinDots[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorBlue));
            } else {
                pinDots[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorGray));
            }
        }
    }

    private void navigateToSuccessScreen(String bankName, int bankIconId) {
        Intent intent = new Intent(DepositWithdrawActivity.this, TransferSuccessActivity.class);

        intent.putExtra("KEY_AMOUNT", currentAmount);

        boolean isDeposit = tabDeposit.getCurrentTextColor() == Color.parseColor("#0A46A6");
        if (isDeposit) {
            intent.putExtra("KEY_NAME", "Nạp tiền từ\n" + bankName);
        } else {
            intent.putExtra("KEY_NAME", "Rút tiền về\n" + bankName);
        }

        intent.putExtra("KEY_AVATAR", bankIconId);

        startActivity(intent);
        finish();
    }
}