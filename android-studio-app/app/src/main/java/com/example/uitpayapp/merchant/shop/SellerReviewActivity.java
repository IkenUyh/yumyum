package com.example.uitpayapp.merchant.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_review);

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
        allReviews.add(new ReviewModel("Huỳnh Thị Hồng Vân", "", 5.0f, "Tuyệt vời", "", "16/11/2024 19:19", "Cảm ơn shop nhé!", ""));
        allReviews.add(new ReviewModel("Trần Văn An", "", 4.0f, "Rất tốt", "#16105-393706528", "17/10/2024 14:30", "Món ăn ngon, giao hàng nhanh.", ""));
        allReviews.add(new ReviewModel("Nguyễn Minh Tâm", "", 5.0f, "Tuyệt vời", "#16105-393706530", "15/10/2024 12:00", "Chất lượng tuyệt vời.", ""));
        allReviews.add(new ReviewModel("Lê Văn B", "", 3.0f, "Bình thường", "#16105-393706531", "10/09/2024 10:00", "", ""));
        allReviews.add(new ReviewModel("Phạm Thị C", "", 2.0f, "Kém", "#16105-393706532", "05/08/2024 09:00", "Đồ ăn hơi nguội", ""));

        displayReviews.addAll(allReviews);
        adapter = new SellerReviewAdapter(displayReviews);
        rvReviews.setAdapter(adapter);
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