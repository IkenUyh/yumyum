package com.example.uitpayapp.history;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import com.example.uitpayapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingMerchantActivity extends AppCompatActivity {

    private RatingBar merchantRatingBar;
    private TextView txtMerchantRatingStatus;
    private LinearLayout layoutMerchantCompliments, layoutDishesContainer;
    private ChipGroup chipGroupMerchant;
    private AppCompatButton btnMerchantSubmit;
    private EditText edtMerchantComment;
    private SwitchCompat switchAnonymous;

    // Dữ liệu mẫu nhận diện từ Đơn hàng (Dễ dàng đổ bằng API)
    private final List<String> merchantCompliments = Arrays.asList("Ngon xỉu!", "Đóng gói tốt", "No căng bụng", "Giá phải chăng");
    private final List<String> orderDishes = Arrays.asList("Mì Trộn Thập Cẩm", "Cơm Chiên Dương Châu");

    // Quản lý lưu trữ trạng thái Like/Dislike của từng món ăn để đẩy lên API
    // Key: Tên món (hoặc ID món), Value: 1 (Like), -1 (Dislike), 0 (Chưa chọn)
    private final Map<String, Integer> dishSatisfactionMap = new HashMap<>();
    private final List<String> mSelectedCompliments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_merchant);

        initViews();
        setupMerchantChips();
        populateOrderDishes();
        setupListeners();
    }

    private void initViews() {
        merchantRatingBar = findViewById(R.id.merchantRatingBar);
        txtMerchantRatingStatus = findViewById(R.id.txtMerchantRatingStatus);
        layoutMerchantCompliments = findViewById(R.id.layoutMerchantCompliments);
        chipGroupMerchant = findViewById(R.id.chipGroupMerchant);
        layoutDishesContainer = findViewById(R.id.layoutDishesContainer);
        btnMerchantSubmit = findViewById(R.id.btnMerchantSubmit);
        edtMerchantComment = findViewById(R.id.edtMerchantComment);
        switchAnonymous = findViewById(R.id.switchAnonymous);
    }

    private void setupMerchantChips() {
        for (String tag : merchantCompliments) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);

            // Tái sử dụng Selector màu cam viền trắng đã cấu tạo từ trước
            chip.setChipBackgroundColorResource(android.R.color.white);
            chip.setChipStrokeColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.selector_chip_stroke));
            chip.setTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.selector_chip_text));
            chip.setChipStrokeWidth(3f);

            chip.setOnCheckedChangeListener((bv, isChecked) -> {
                if (isChecked) mSelectedCompliments.add(tag);
                else mSelectedCompliments.remove(tag);
            });
            chipGroupMerchant.addView(chip);
        }
    }

    private void populateOrderDishes() {
        layoutDishesContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String dishName : orderDishes) {
            dishSatisfactionMap.put(dishName, 0); // Trạng thái mặc định ban đầu là chưa chọn gì

            View dishView = inflater.inflate(R.layout.activity_history_item_dish_review, layoutDishesContainer, false);
            TextView tvName = dishView.findViewById(R.id.txtDishName);
            ImageView btnLike = dishView.findViewById(R.id.btnLike);
            ImageView btnDislike = dishView.findViewById(R.id.btnDislike);

            tvName.setText(dishName);

            // Xử lý logic click tương tác loại trừ lẫn nhau giữa Like và Dislike
            btnLike.setOnClickListener(v -> {
                if (dishSatisfactionMap.get(dishName) == 1) {
                    dishSatisfactionMap.put(dishName, 0);
                    btnLike.setColorFilter(Color.parseColor("#CCCCCC")); // Tắt màu về xám
                } else {
                    dishSatisfactionMap.put(dishName, 1);
                    btnLike.setColorFilter(Color.parseColor("#EE4D2D")); // Sáng cam đậm Shopee
                    btnDislike.setColorFilter(Color.parseColor("#CCCCCC")); // Tắt nút còn lại
                }
            });

            btnDislike.setOnClickListener(v -> {
                if (dishSatisfactionMap.get(dishName) == -1) {
                    dishSatisfactionMap.put(dishName, 0);
                    btnDislike.setColorFilter(Color.parseColor("#CCCCCC"));
                } else {
                    dishSatisfactionMap.put(dishName, -1);
                    btnDislike.setColorFilter(Color.parseColor("#EE4D2D")); // Sáng cam đậm Shopee
                    btnLike.setColorFilter(Color.parseColor("#CCCCCC"));
                }
            });

            layoutDishesContainer.addView(dishView);
        }
    }

    private void setupListeners() {
        findViewById(R.id.btnMerchantBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddImage).setOnClickListener(v -> {
            Toast.makeText(this, "Mở trình chọn ảnh hệ thống...", Toast.LENGTH_SHORT).show();
        });

        merchantRatingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            int currentRating = (int) rating;
            if (currentRating > 0) {
                layoutMerchantCompliments.setVisibility(View.VISIBLE);
                txtMerchantRatingStatus.setVisibility(View.VISIBLE);
                btnMerchantSubmit.setEnabled(true); // Kích hoạt nút Gửi đi sáng màu Cam

                if (currentRating == 5) txtMerchantRatingStatus.setText("Tuyệt vời");
                else if (currentRating == 4) txtMerchantRatingStatus.setText("Hài lòng");
                else txtMerchantRatingStatus.setText("Bình thường");
            } else {
                layoutMerchantCompliments.setVisibility(View.GONE);
                txtMerchantRatingStatus.setVisibility(View.GONE);
                btnMerchantSubmit.setEnabled(false);
            }
        });

        btnMerchantSubmit.setOnClickListener(v -> {
            // Gom dữ liệu hoàn chỉnh chuẩn bị tích hợp đẩy lên API
            String userComment = edtMerchantComment.getText().toString().trim();
            boolean isAnonymous = switchAnonymous.isChecked();
            int finalStars = (int) merchantRatingBar.getRating();

            Toast.makeText(this, "Đánh giá đơn hàng hoàn tất! Đang gửi API...", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}