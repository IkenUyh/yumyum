package com.example.uitpayapp.merchant.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.ReviewModel;
import com.example.uitpayapp.network.SessionManager;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.modules.restaurant.RestaurantRepository;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;
import com.example.uitpayapp.modules.review.ReviewRepository;
import com.example.uitpayapp.modules.review.ReviewService;
import com.example.uitpayapp.network.RetrofitClient;
import com.example.uitpayapp.modules.review.models.responses.ReviewResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SellerReviewActivity extends AppCompatActivity {

    private RecyclerView rvReviews;
    private SellerReviewAdapter adapter;
    private TextView tvFilterTime, tvFilterEvaluation, tvFilterComment;
    private ImageView btnRemoveFilter;
    
    // Rating views
    private TextView tvOverallRating, tvOverallCount;
    private RatingBar rbOverall;
    private TextView tvFilteredRating, tvFilteredCount, tvFilterDesc;
    private RatingBar rbFiltered;

    private final List<ReviewModel> allReviews = new ArrayList<>();
    private final List<ReviewModel> displayReviews = new ArrayList<>();

    private String selectedTime = "Tất cả";
    private String selectedEvaluation = "Đánh giá";
    private String selectedComment = "Bình luận";

    private SessionManager sessionManager;
    private RestaurantRepository restaurantRepository;
    private ReviewRepository reviewRepository;
    private Long merchantId;
    private Long restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_review);

        // Khởi tạo session và repositories
        sessionManager = SessionManager.getInstance(this);
        restaurantRepository = new RestaurantRepository();
        ReviewService reviewService = RetrofitClient.getReviewService();
        reviewRepository = new ReviewRepository(reviewService);
        
        merchantId = sessionManager.getUserId();

        initViews();
        setupData();
        setupListeners();
        updateRatingSummaries();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tvFilterTime = findViewById(R.id.tv_filter_time);
        tvFilterEvaluation = findViewById(R.id.tv_filter_evaluation);
        tvFilterComment = findViewById(R.id.tv_filter_comment);
        btnRemoveFilter = findViewById(R.id.btn_remove_filter);
        rvReviews = findViewById(R.id.rv_reviews);

        tvOverallRating = findViewById(R.id.tv_overall_rating);
        tvOverallCount = findViewById(R.id.tv_overall_count);
        rbOverall = findViewById(R.id.rb_overall);
        
        tvFilteredRating = findViewById(R.id.tv_filtered_rating);
        tvFilteredCount = findViewById(R.id.tv_filtered_count);
        tvFilterDesc = findViewById(R.id.tv_filter_desc);
        rbFiltered = findViewById(R.id.rb_filtered);

        View mainContainer = findViewById(R.id.seller_review_container);
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top;
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), safeBottomPadding);
            return insets;
        });
        if (rvReviews != null) {
            rvReviews.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setupData() {
        adapter = new SellerReviewAdapter(displayReviews);
        if (rvReviews != null) {
            rvReviews.setAdapter(adapter);
        }
        loadReviewsFromApi();
    }

    private void loadReviewsFromApi() {
        if (merchantId == null || merchantId == -1L) {
            Toast.makeText(this, "Không tìm thấy phiên đăng nhập chủ quán!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Lấy tất cả nhà hàng để tìm nhà hàng thuộc về merchantId này
        restaurantRepository.getAllRestaurants(new ApiCallback<List<RestaurantResponseDTO>>() {
            @Override
            public void onSuccess(List<RestaurantResponseDTO> data) {
                if (data == null || data.isEmpty()) {
                    Toast.makeText(SellerReviewActivity.this, "Không tìm thấy quán ăn nào!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tìm quán ăn của merchant hiện tại
                for (RestaurantResponseDTO r : data) {
                    if (r.getMerchantId() != null && r.getMerchantId().equals(merchantId)) {
                        restaurantId = r.getId();
                        break;
                    }
                }

                if (restaurantId == null) {
                    Toast.makeText(SellerReviewActivity.this, "Tài khoản của bạn chưa đăng ký quán ăn!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Tải các đánh giá của quán ăn đó từ API
                fetchReviews(restaurantId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SellerReviewActivity.this, "Lỗi tải thông tin quán ăn: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviews(Long resId) {
        reviewRepository.getReviewsByRestaurant(resId, new ApiCallback<List<ReviewResponse>>() {
            @Override
            public void onSuccess(List<ReviewResponse> data) {
                allReviews.clear();
                displayReviews.clear();
                if (data != null) {
                    for (ReviewResponse res : data) {
                        String ratingText = "Bình thường";
                        if (res.getRating() != null) {
                            int r = res.getRating();
                            if (r == 5) ratingText = "Tuyệt vời";
                            else if (r == 4) ratingText = "Rất tốt";
                            else if (r == 3) ratingText = "Bình thường";
                            else if (r == 2) ratingText = "Kém";
                            else if (r == 1) ratingText = "Rất kém";
                        }

                        ReviewModel model = new ReviewModel(
                                res.getCustomerName() != null ? res.getCustomerName() : "Ẩn danh",
                                "",
                                res.getRating() != null ? res.getRating().floatValue() : 0.0f,
                                ratingText,
                                res.getOrderId() != null ? "#" + res.getOrderId() : "",
                                formatIsoDate(res.getCreatedAt()),
                                res.getComment() != null ? res.getComment() : "",
                                ""
                        );
                        allReviews.add(model);
                    }
                }
                displayReviews.addAll(allReviews);
                adapter.notifyDataSetChanged();
                updateRatingSummaries();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SellerReviewActivity.this, "Lỗi tải danh sách đánh giá: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatIsoDate(String isoDateStr) {
        if (isoDateStr == null) return "";
        if (isoDateStr.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")) {
            return isoDateStr;
        }

        String[] patterns = {
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss"
        };

        for (String pattern : patterns) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(pattern, Locale.getDefault());
                Date date = parser.parse(isoDateStr);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                return outputFormat.format(date);
            } catch (Exception ignored) {}
        }
        return isoDateStr;
    }

    private void setupListeners() {
        tvFilterTime.setOnClickListener(v -> showTimeFilterPopup());
        tvFilterEvaluation.setOnClickListener(v -> showEvaluationFilterPopup());
        tvFilterComment.setOnClickListener(v -> showCommentFilterPopup());
        btnRemoveFilter.setOnClickListener(v -> resetFilters());
    }

    private void showTimeFilterPopup() {
        PopupMenu popup = new PopupMenu(this, tvFilterTime);
        popup.getMenu().add("Tất cả");
        popup.getMenu().add("1 tháng qua");
        popup.getMenu().add("3 tháng qua");
        popup.getMenu().add("6 tháng qua");

        popup.setOnMenuItemClickListener(item -> {
            selectedTime = item.getTitle().toString();
            tvFilterTime.setText(selectedTime);
            applyFilters();
            return true;
        });
        popup.show();
    }

    private void showEvaluationFilterPopup() {
        PopupMenu popup = new PopupMenu(this, tvFilterEvaluation);
        String[] evaluations = {"Tất cả", "5 sao", "4 sao", "3 sao", "2 sao", "1 sao"};
        for (String s : evaluations) popup.getMenu().add(s);

        popup.setOnMenuItemClickListener(item -> {
            selectedEvaluation = item.getTitle().toString();
            tvFilterEvaluation.setText(selectedEvaluation);
            applyFilters();
            return true;
        });
        popup.show();
    }

    private void showCommentFilterPopup() {
        PopupMenu popup = new PopupMenu(this, tvFilterComment);
        popup.getMenu().add("Tất cả");
        popup.getMenu().add("Có bình luận");
        popup.getMenu().add("Không có bình luận");

        popup.setOnMenuItemClickListener(item -> {
            selectedComment = item.getTitle().toString();
            tvFilterComment.setText(selectedComment);
            applyFilters();
            return true;
        });
        popup.show();
    }

    private void resetFilters() {
        selectedTime = "Tất cả";
        selectedEvaluation = "Đánh giá";
        selectedComment = "Bình luận";
        tvFilterTime.setText("Tất cả");
        tvFilterEvaluation.setText("Đánh giá");
        tvFilterComment.setText("Bình luận");
        applyFilters();
    }

    private void applyFilters() {
        displayReviews.clear();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Calendar calThreshold = Calendar.getInstance();

        boolean checkTime = !selectedTime.equals("Tất cả");
        if (checkTime) {
            int months = 1;
            if (selectedTime.contains("3")) months = 3;
            else if (selectedTime.contains("6")) months = 6;
            calThreshold.add(Calendar.MONTH, -months);
        }
        
        for (ReviewModel review : allReviews) {
            if (checkTime) {
                try {
                    Date reviewDate = sdf.parse(review.getDate());
                    if (reviewDate == null || reviewDate.before(calThreshold.getTime())) continue;
                } catch (ParseException e) { continue; }
            }

            if (!selectedEvaluation.equals("Tất cả") && !selectedEvaluation.equals("Đánh giá")) {
                int ratingTarget = Integer.parseInt(selectedEvaluation.split(" ")[0]);
                if ((int) review.getRating() != ratingTarget) continue;
            }

            if (!selectedComment.equals("Tất cả") && !selectedComment.equals("Bình luận")) {
                boolean isContentEmpty = review.getContent() == null || review.getContent().trim().isEmpty();
                if (selectedComment.equals("Có bình luận") && isContentEmpty) continue;
                if (selectedComment.equals("Không có bình luận") && !isContentEmpty) continue;
            }
            displayReviews.add(review);
        }

        adapter.notifyDataSetChanged();
        updateRatingSummaries();
    }

    private void updateRatingSummaries() {
        // Overall
        float overallAvg = calculateAverage(allReviews);
        tvOverallRating.setText(String.format(Locale.getDefault(), "%.1f", overallAvg));
        rbOverall.setRating(overallAvg);
        tvOverallCount.setText(allReviews.size() + " Bình luận");

        // Filtered
        float filteredAvg = calculateAverage(displayReviews);
        tvFilteredRating.setText(String.format(Locale.getDefault(), "%.1f", filteredAvg));
        rbFiltered.setRating(filteredAvg);
        tvFilteredCount.setText(displayReviews.size() + " Bình luận");
        tvFilterDesc.setText(selectedTime);
    }

    private float calculateAverage(List<ReviewModel> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0.0f;
        float sum = 0;
        for (ReviewModel r : reviews) {
            sum += r.getRating();
        }
        return sum / reviews.size();
    }
}