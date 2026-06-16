package com.example.uitpayapp.history;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RatingDriverActivity extends AppCompatActivity implements View.OnClickListener {

    // Trạng thái Data Model chuẩn bị gửi API
    private String mDriverId;
    private int mSelectedRating = 0;
    private int mSelectedTipAmount = 0;
    private final List<String> mSelectedCompliments = new ArrayList<>();

    // UI Components
    private RatingBar ratingBar;
    private TextView txtRatingStatus;
    private LinearLayout layoutConditionalFeedback;
    private Button btnTip5k, btnTip10k, btnTip15k, btnTipOther, btnSubmit;
    private ChipGroup chipGroupCompliments;
    private EditText edtComment;
    private ImageView imgDriverAvatar;
    private TextView txtDriverName;

    // Danh sách data mẫu cho tag lời khen ngợi (Dễ chỉnh sửa/nhận từ API sau này)
    private final List<String> complimentTags = Arrays.asList(
            "Thân thiện", "Cẩn thận", "Đúng giờ", "Thái độ tốt", "Đồng phục gọn gàng", "Sạch sẽ"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_driver);

        initViews();
        getDataFromIntent();
        setupComplimentChips();
        setupListeners();
    }

    private void initViews() {
        ratingBar = findViewById(R.id.ratingBar);
        txtRatingStatus = findViewById(R.id.txtRatingStatus);
        layoutConditionalFeedback = findViewById(R.id.layoutConditionalFeedback);
        btnTip5k = findViewById(R.id.btnTip5k);
        btnTip10k = findViewById(R.id.btnTip10k);
        btnTip15k = findViewById(R.id.btnTip15k);
        btnTipOther = findViewById(R.id.btnTipOther);
        btnSubmit = findViewById(R.id.btnSubmit);
        chipGroupCompliments = findViewById(R.id.chipGroupCompliments);
        edtComment = findViewById(R.id.edtComment);
        imgDriverAvatar = findViewById(R.id.imgDriverAvatar);
        txtDriverName = findViewById(R.id.txtDriverName);
    }

    private void getDataFromIntent() {
        // Nhận thông tin động từ luồng Intent truyền sang
        if (getIntent() != null) {
            mDriverId = getIntent().getStringExtra("DRIVER_ID");
            String driverName = getIntent().getStringExtra("DRIVER_NAME");
            if (driverName != null) {
                txtDriverName.setText(driverName.toUpperCase());
            }
            // Load ảnh sử dụng Glide/Picasso qua ID/URL nếu cần:
            // Glide.with(this).load(driverAvatarUrl).into(imgDriverAvatar);
        }
    }

    private void setupComplimentChips() {
        for (String tag : complimentTags) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setClickable(true);

            // 1. Ẩn dấu check v mặc định của Material Chip để giống thiết kế mẫu 100%
            chip.setCheckedIconVisible(false);

            // 2. Ép các tập hợp màu Selector đã tạo ở trên vào Chip bằng code Java
            chip.setChipBackgroundColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.selector_chip_bg));
            chip.setChipStrokeColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.selector_chip_stroke));
            chip.setTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.selector_chip_text));

            // 3. Thiết lập độ dày viền và bo góc cho cân đối
            chip.setChipStrokeWidth(3f);
            chip.setChipCornerRadius(20f);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mSelectedCompliments.add(tag);
                } else {
                    mSelectedCompliments.remove(tag);
                }
            });
            chipGroupCompliments.addView(chip);
        }
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnTip5k.setOnClickListener(this);
        btnTip10k.setOnClickListener(this);
        btnTip15k.setOnClickListener(this);
        btnTipOther.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        // Theo dõi số sao đánh giá để cập nhật UI hợp lệ
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            mSelectedRating = (int) rating;
            if (mSelectedRating > 0) {
                layoutConditionalFeedback.setVisibility(View.VISIBLE);
                txtRatingStatus.setVisibility(View.VISIBLE);
                btnSubmit.setEnabled(true);

                // Set trạng thái text theo số sao giống Shopee App
                if (mSelectedRating == 5) txtRatingStatus.setText("Tuyệt vời");
                else if (mSelectedRating == 4) txtRatingStatus.setText("Hài lòng");
                else txtRatingStatus.setText("Cần cải thiện");
            } else {
                layoutConditionalFeedback.setVisibility(View.GONE);
                txtRatingStatus.setVisibility(View.GONE);
                btnSubmit.setEnabled(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnTip5k) {
            handleTipSelection(5000, btnTip5k);
        } else if (id == R.id.btnTip10k) {
            handleTipSelection(10000, btnTip10k);
        } else if (id == R.id.btnTip15k) {
            handleTipSelection(15000, btnTip15k);
        } else if (id == R.id.btnTipOther) {
            showCustomTipDialog();
        } else if (id == R.id.btnSubmit) {
            submitRatingData();
        }
    }

    private void handleTipSelection(int amount, Button selectedButton) {
        // Reset trạng thái chọn của tất cả nút tip
        btnTip5k.setSelected(false);
        btnTip10k.setSelected(false);
        btnTip15k.setSelected(false);
        btnTipOther.setSelected(false);

        if (mSelectedTipAmount == amount) {
            // Click lần 2 vào nút đang chọn -> Hủy chọn tip
            mSelectedTipAmount = 0;
            btnSubmit.setText("Gửi đi");
        } else {
            mSelectedTipAmount = amount;
            selectedButton.setSelected(true);
            btnSubmit.setText("Gửi đi (kèm tiền thưởng)");
        }
    }

    private void showCustomTipDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_history_rating_tipping);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView dialogClose = dialog.findViewById(R.id.dialogClose);
        EditText edtCustomTipAmount = dialog.findViewById(R.id.edtCustomTipAmount);
        androidx.appcompat.widget.AppCompatButton btnDialogDone = dialog.findViewById(R.id.btnDialogDone);

        dialogClose.setOnClickListener(v -> dialog.dismiss());

        // Lắng nghe và xử lý logic nhập tiền realtime
        edtCustomTipAmount.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (!input.isEmpty()) {
                    try {
                        int amount = Integer.parseInt(input);
                        // Kiểm tra hạn mức tiền tip hợp lệ từ 1k đến 200k
                        if (amount >= 1000 && amount <= 200000) {
                            btnDialogDone.setEnabled(true); // Mở khóa nút
                            btnDialogDone.setBackgroundResource(R.drawable.bg_btn_submit); // Nền Cam Shopee
                            btnDialogDone.setTextColor(Color.WHITE); // Chữ trắng
                            return;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                // Nếu không hợp lệ hoặc trống -> Khóa nút lại, trả về màu xám mặc định
                btnDialogDone.setEnabled(false);
                btnDialogDone.setBackgroundResource(R.drawable.bg_btn_submit);
                btnDialogDone.setTextColor(Color.parseColor("#9E9E9E"));
            }
        });

        // Xử lý khi nhấn nút Xác nhận số tiền vừa nhập
        btnDialogDone.setOnClickListener(v -> {
            String input = edtCustomTipAmount.getText().toString().trim();
            if (!input.isEmpty()) {
                int customAmount = Integer.parseInt(input);

                // Định dạng hiển thị trên nút "Khác" (Ví dụ: 15000đ -> 15K)
                if (customAmount % 1000 == 0) {
                    btnTipOther.setText((customAmount / 1000) + "K");
                } else {
                    btnTipOther.setText(customAmount + "đ");
                }

                // Gọi hàm xử lý chọn để nút "Khác" chuyển sang màu viền cam nền cam nhạt
                handleTipSelection(customAmount, btnTipOther);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void submitRatingData() {
        String comment = edtComment.getText().toString().trim();

        // Logic xử lý đóng gói API cũ giữ nguyên...
        Toast.makeText(this, "Gửi đánh giá tài xế thành công!", Toast.LENGTH_SHORT).show();

        // BỔ SUNG LUỒNG INTENT: Chuyển tiếp mượt mà sang màn hình Đánh giá quán và món
        Intent merchantIntent = new Intent(RatingDriverActivity.this, RatingMerchantActivity.class);
        // Truyền tiếp các dữ liệu cần thiết (OrderId, MerchantName, List món ăn) phục vụ API sau này
        merchantIntent.putExtra("ORDER_ID", mDriverId);
        startActivity(merchantIntent);

        finish(); // Đóng màn hình tài xế lại để tránh người dùng back ngược lại
    }
}