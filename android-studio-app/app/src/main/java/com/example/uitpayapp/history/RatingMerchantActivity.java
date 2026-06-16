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
import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.review.ReviewRepository;
import com.example.uitpayapp.modules.review.ReviewService;
import com.example.uitpayapp.modules.review.models.requests.CreateReviewRequest;
import com.example.uitpayapp.modules.review.models.responses.ReviewResponse;
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

    // KHAI BÁO THÊM: Biến quản lý API và dữ liệu phụ thuộc
    private ReviewRepository reviewRepository;
    private Long orderId;
    private String userToken;

    private final List<String> merchantCompliments = Arrays.asList("Ngon xỉu!", "Đóng gói tốt", "No căng bụng", "Giá phải chăng");
    private final List<String> orderDishes = Arrays.asList("Mì Trộn Thập Cẩm", "Cơm Chiên Dương Châu");

    private final Map<String, Integer> dishSatisfactionMap = new HashMap<>();
    private final List<String> mSelectedCompliments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_merchant);

        // 1. Lấy dữ liệu Token và Order ID chuyển giao từ màn hình trước
        // (Thay thế giá trị mặc định bằng logic SharedPreferences/Intent thực tế của bạn)
        orderId = getIntent().getLongExtra("ORDER_ID", 1L);
        userToken = "YOUR_STORED_JWT_TOKEN";

        // 2. Khởi tạo tầng Repository kết nối API
        ReviewService reviewService = RetrofitClient.getReviewService();
        reviewRepository = new ReviewRepository(reviewService);

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
            dishSatisfactionMap.put(dishName, 0);

            View dishView = inflater.inflate(R.layout.activity_history_item_dish_review, layoutDishesContainer, false);
            TextView tvName = dishView.findViewById(R.id.txtDishName);
            ImageView btnLike = dishView.findViewById(R.id.btnLike);
            ImageView btnDislike = dishView.findViewById(R.id.btnDislike);

            tvName.setText(dishName);

            btnLike.setOnClickListener(v -> {
                if (dishSatisfactionMap.get(dishName) == 1) {
                    dishSatisfactionMap.put(dishName, 0);
                    btnLike.setColorFilter(Color.parseColor("#CCCCCC"));
                } else {
                    dishSatisfactionMap.put(dishName, 1);
                    btnLike.setColorFilter(Color.parseColor("#EE4D2D"));
                    btnDislike.setColorFilter(Color.parseColor("#CCCCCC"));
                }
            });

            btnDislike.setOnClickListener(v -> {
                if (dishSatisfactionMap.get(dishName) == -1) {
                    dishSatisfactionMap.put(dishName, 0);
                    btnDislike.setColorFilter(Color.parseColor("#CCCCCC"));
                } else {
                    dishSatisfactionMap.put(dishName, -1);
                    btnDislike.setColorFilter(Color.parseColor("#EE4D2D"));
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
                btnMerchantSubmit.setEnabled(true);

                if (currentRating == 5) txtMerchantRatingStatus.setText("Tuyệt vời");
                else if (currentRating == 4) txtMerchantRatingStatus.setText("Hài lòng");
                else txtMerchantRatingStatus.setText("Bình thường");
            } else {
                layoutMerchantCompliments.setVisibility(View.GONE);
                txtMerchantRatingStatus.setVisibility(View.GONE);
                btnMerchantSubmit.setEnabled(false);
            }
        });

        // TÍCH HỢP GỌI API TẠI ĐÂY
        btnMerchantSubmit.setOnClickListener(v -> {
            btnMerchantSubmit.setEnabled(false); // Vô hiệu hóa tạm thời để tránh người dùng click lặp (Double Submit)

            // 1. Xử lý gom và định dạng nội dung text bình luận nâng cao từ UI công phu của bạn
            String baseComment = edtMerchantComment.getText().toString().trim();
            StringBuilder finalCommentBuilder = new StringBuilder(baseComment);

            // Đính kèm các Tag khen ngợi đã chọn
            if (!mSelectedCompliments.isEmpty()) {
                finalCommentBuilder.append("\n[Tiêu chí thích]: ").append(String.join(", ", mSelectedCompliments));
            }

            // Đính kèm danh sách đánh giá trạng thái món ăn
            StringBuilder dishReviewBuilder = new StringBuilder();
            for (Map.Entry<String, Integer> entry : dishSatisfactionMap.entrySet()) {
                if (entry.getValue() == 1) {
                    dishReviewBuilder.append("👍 ").append(entry.getKey()).append("; ");
                } else if (entry.getValue() == -1) {
                    dishReviewBuilder.append("👎 ").append(entry.getKey()).append("; ");
                }
            }
            if (dishReviewBuilder.length() > 0) {
                finalCommentBuilder.append("\n[Đánh giá món]: ").append(dishReviewBuilder.toString());
            }

            if (switchAnonymous.isChecked()) {
                finalCommentBuilder.append("\n(Đánh giá ẩn danh)");
            }

            int finalStars = (int) merchantRatingBar.getRating();
            String processedComment = finalCommentBuilder.toString().trim();

            // 2. Tạo đối tượng Request DTO tương thích Backend cấu trúc sẵn
            CreateReviewRequest request = new CreateReviewRequest(orderId, finalStars, processedComment);

            // 3. THAY ĐỔI TẠI ĐÂY: Gọi trực tiếp qua RetrofitClient và Service tương ứng
            // (Token bảo mật đã được OkHttpClient Interceptor tự động thêm vào Header)
            // 3. Thực thi gọi hàm thông qua RetrofitClient
            RetrofitClient.getReviewService().submitReview(request).enqueue(new retrofit2.Callback<ApiResponse<ReviewResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<ApiResponse<ReviewResponse>> call, retrofit2.Response<ApiResponse<ReviewResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Lấy object phản hồi chung từ Server
                        ApiResponse<ReviewResponse> apiResponse = response.body();

                        // Bạn có thể check thêm các logic riêng của dự án như apiResponse.isSuccess() nếu cần
                        Toast.makeText(RatingMerchantActivity.this, "Đánh giá đơn hàng hoàn tất!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Trả tín hiệu về cho màn hình lịch sử cập nhật lại trạng thái nút
                        finish();
                    } else {
                        btnMerchantSubmit.setEnabled(true); // Kích hoạt lại nút nếu lỗi xảy ra để người dùng thử lại
                        Toast.makeText(RatingMerchantActivity.this, "Lỗi khi gửi đánh giá: Mã lỗi " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ApiResponse<ReviewResponse>> call, Throwable t) {
                    btnMerchantSubmit.setEnabled(true); // Kích hoạt lại nút để người dùng có thể bấm gửi lại
                    Toast.makeText(RatingMerchantActivity.this, "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}