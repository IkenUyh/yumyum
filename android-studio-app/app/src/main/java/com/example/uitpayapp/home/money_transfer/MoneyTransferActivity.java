package com.example.uitpayapp.home.money_transfer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoneyTransferActivity extends AppCompatActivity {

    private TextView tvAmount;
    private Button btnContinue;
    private String currentAmount = "0";
    private boolean isButtonVisible = false;
    private boolean isRecipientSelected = false;

    private int currentAvatarId = R.drawable.img_usagi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer);

        initViews();
        setupKeypad();
    }

    private void initViews() {
        tvAmount = findViewById(R.id.tv_amount);
        btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setTranslationY(100f);

        View layoutSelected = findViewById(R.id.layout_selected_recipient);
        ImageView ivSelectedAvatar = findViewById(R.id.iv_selected_avatar);
        TextView tvSelectedName = findViewById(R.id.tv_selected_name);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        RecyclerView rvQuickSelect = findViewById(R.id.rv_quick_select);
        rvQuickSelect.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<RecipientItem> mockList = new ArrayList<>();
        mockList.add(new RecipientItem("U nà", R.drawable.img_usagi));
        mockList.add(new RecipientItem("Ya hà", R.drawable.img_miku));
        mockList.add(new RecipientItem("Bri", R.drawable.ic_dog_kid));

        RecipientAdapter adapter = new RecipientAdapter(mockList, item -> {
            tvSelectedName.setText(item.getName());
            ivSelectedAvatar.setImageResource(item.getAvatarResId());
            currentAvatarId = item.getAvatarResId();

            isRecipientSelected = true;
            checkAndToggleContinueButton();

            layoutSelected.setVisibility(View.VISIBLE);
            layoutSelected.setAlpha(0f);
            layoutSelected.animate().alpha(1f).setDuration(300).start();
        });
        rvQuickSelect.setAdapter(adapter);

        findViewById(R.id.btn_change_recipient).setOnClickListener(v -> {
            isRecipientSelected = false;
            checkAndToggleContinueButton();

            layoutSelected.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> layoutSelected.setVisibility(View.GONE))
                    .start();
        });

        btnContinue.setOnClickListener(v -> {
            String recipientName = tvSelectedName.getText().toString();

            android.content.Intent intent = new android.content.Intent(MoneyTransferActivity.this, TransferConfirmationActivity.class);

            intent.putExtra("KEY_AMOUNT", currentAmount);
            intent.putExtra("KEY_NAME", recipientName);
            intent.putExtra("KEY_AVATAR", currentAvatarId);

            startActivity(intent);
        });

        updateUI();
    }

    private void setupKeypad() {
        int[] numberButtonIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_000
        };

        for (int id : numberButtonIds) {
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
        updateUI();
    }

    private void handleDeletePress() {
        if (currentAmount.length() > 1) {
            currentAmount = currentAmount.substring(0, currentAmount.length() - 1);
        } else {
            currentAmount = "0";
        }
        updateUI();
    }

    private void updateUI() {
        String displayString = currentAmount;
        try {
            long amount = Long.parseLong(currentAmount);
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            displayString = formatter.format(amount);
        } catch (NumberFormatException e) {
            displayString = currentAmount;
        }

        String finalString = displayString + " đ";
        SpannableString spannable = new SpannableString(finalString);

        int startIndex = displayString.length();
        int endIndex = finalString.length();

        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#BDBDBD")),
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(
                new RelativeSizeSpan(0.5f),
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvAmount.setText(spannable);

        checkAndToggleContinueButton();
    }
    private void checkAndToggleContinueButton() {
        boolean isAmountValid = !currentAmount.equals("0");
        boolean shouldShow = isAmountValid && isRecipientSelected;

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
}