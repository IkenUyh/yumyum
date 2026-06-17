package com.example.uitpayapp.admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.profile.ProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminApprovalActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvApprovalList;
    private TextView tvFilterPending, tvFilterApproved, tvFilterRejected;

    private PendingStoreAdapter storeAdapter;
    private PendingDishAdapter dishAdapter;

    private List<PendingStore> allStores = new ArrayList<>();
    private List<PendingDish> allDishes = new ArrayList<>();

    private String currentFilter = "pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        androidx.core.view.WindowInsetsControllerCompat windowInsetsController = androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
        setContentView(R.layout.activity_admin_approval);

        initViews();
        setupData();
        setupFilters();
        setupTabLayout();

        // Default to store and pending
        filterData("pending");
    }

    private void initViews() {
        View topBar = findViewById(R.id.top_bar_admin_approval);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Quản lý duyệt");

        tabLayout = findViewById(R.id.tab_layout);
        rvApprovalList = findViewById(R.id.rv_approval_list);
        rvApprovalList.setLayoutManager(new LinearLayoutManager(this));

        tvFilterPending = findViewById(R.id.tv_filter_pending);
        tvFilterApproved = findViewById(R.id.tv_filter_approved);
        tvFilterRejected = findViewById(R.id.tv_filter_rejected);

        View mainContainer = findViewById(R.id.admin_approval_container);
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int dp16 = (int)(16 * getResources().getDisplayMetrics().density);
            topBar.setPadding(dp16, systemBars.top + dp16, dp16, dp16);
            v.setPadding(v.getPaddingLeft(), 0, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });
    }

    private void setupData() {
        allStores.clear();
        allDishes.clear();

        String[] statuses = {"pending", "approved", "rejected"};

        List<com.example.uitpayapp.home.home_models.Restaurant> homeRestaurants = com.example.uitpayapp.home.HomeActivity.HomeRepository.getInstance().getRestaurants();
        int storeId = 1;
        if (homeRestaurants != null) {
            for (com.example.uitpayapp.home.home_models.Restaurant r : homeRestaurants) {
                String status = statuses[(storeId - 1) % 3];
                allStores.add(new PendingStore("S" + storeId, r.getName(), "Chủ cửa hàng " + storeId, r.getAddress(), r.getCategory(), r.getImageResId(), status, "20/05/2024"));
                storeId++;
            }
        }

        List<com.example.uitpayapp.home.home_models.FoodMenuItem> homeFoods = com.example.uitpayapp.home.HomeActivity.HomeRepository.getInstance().getPopularFoods();
        int dishId = 1;
        if (homeFoods != null) {
            for (com.example.uitpayapp.home.home_models.FoodMenuItem f : homeFoods) {
                String status = statuses[(dishId - 1) % 3];
                allDishes.add(new PendingDish("D" + dishId, f.getName(), "Cửa hàng " + dishId, "Đồ ăn", f.getPrice(), f.getImageResId(), status, "21/05/2024"));
                dishId++;
            }
        }
    }

    private void setupFilters() {
        tvFilterPending.setOnClickListener(v -> filterData("pending"));
        tvFilterApproved.setOnClickListener(v -> filterData("approved"));
        tvFilterRejected.setOnClickListener(v -> filterData("rejected"));
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterData(currentFilter);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterData(String status) {
        currentFilter = status;

        tvFilterPending.setBackgroundResource(R.drawable.bg_tab_unselected_gray);
        tvFilterPending.setTextColor(Color.parseColor("#757575"));
        tvFilterApproved.setBackgroundResource(R.drawable.bg_tab_unselected_gray);
        tvFilterApproved.setTextColor(Color.parseColor("#757575"));
        tvFilterRejected.setBackgroundResource(R.drawable.bg_tab_unselected_gray);
        tvFilterRejected.setTextColor(Color.parseColor("#757575"));

        if (status.equals("pending")) {
            tvFilterPending.setBackgroundResource(R.drawable.bg_tab_selected);
            tvFilterPending.setTextColor(Color.parseColor("#f24405"));
        } else if (status.equals("approved")) {
            tvFilterApproved.setBackgroundResource(R.drawable.bg_tab_selected);
            tvFilterApproved.setTextColor(Color.parseColor("#f24405"));
        } else if (status.equals("rejected")) {
            tvFilterRejected.setBackgroundResource(R.drawable.bg_tab_selected);
            tvFilterRejected.setTextColor(Color.parseColor("#f24405"));
        }

        if (tabLayout.getSelectedTabPosition() == 0) {
            // Stores
            List<PendingStore> filteredStores = new ArrayList<>();
            for (PendingStore store : allStores) {
                if (store.getStatus().equals(status)) {
                    filteredStores.add(store);
                }
            }
            storeAdapter = new PendingStoreAdapter(this, filteredStores, this::showStoreDetailBottomSheet);
            rvApprovalList.setAdapter(storeAdapter);
        } else {
            // Dishes
            List<PendingDish> filteredDishes = new ArrayList<>();
            for (PendingDish dish : allDishes) {
                if (dish.getStatus().equals(status)) {
                    filteredDishes.add(dish);
                }
            }
            dishAdapter = new PendingDishAdapter(this, filteredDishes, this::showDishDetailBottomSheet);
            rvApprovalList.setAdapter(dishAdapter);
        }
    }

    private void showStoreDetailBottomSheet(PendingStore store) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_store_approval, null);
        dialog.setContentView(view);

        ImageView ivImage = view.findViewById(R.id.iv_detail_store_image);
        TextView tvName = view.findViewById(R.id.tv_detail_store_name);
        TextView tvBadge = view.findViewById(R.id.tv_detail_status_badge);
        TextView tvRejectReason = view.findViewById(R.id.tv_reject_reason_display);
        
        setDetailRow(view.findViewById(R.id.row_detail_owner), "Chủ cửa hàng", store.getOwnerName(), R.drawable.ic_security_user);
        setDetailRow(view.findViewById(R.id.row_detail_address), "Địa chỉ", store.getAddress(), R.drawable.ic_location);
        setDetailRow(view.findViewById(R.id.row_detail_type), "Loại hình kinh doanh", store.getStoreType(), R.drawable.ic_my_store);
        setDetailRow(view.findViewById(R.id.row_detail_date), "Ngày gửi", store.getSubmittedDate(), R.drawable.icon_transactionhistory_calendar_month_24px);

        if (store.getImageRes() != 0) ivImage.setImageResource(store.getImageRes());
        tvName.setText(store.getStoreName());

        updateBadgeUI(tvBadge, store.getStatus());

        if (store.getStatus().equals("rejected") && store.getRejectReason() != null) {
            tvRejectReason.setVisibility(View.VISIBLE);
            tvRejectReason.setText("Lý do từ chối: " + store.getRejectReason());
        }

        LinearLayout llAction = view.findViewById(R.id.ll_action_buttons);
        if (!store.getStatus().equals("pending")) {
            llAction.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btn_close_sheet).setOnClickListener(v -> dialog.dismiss());
        
        view.findViewById(R.id.btn_approve).setOnClickListener(v -> {
            showApproveDialog(store, null, dialog);
        });

        view.findViewById(R.id.btn_reject).setOnClickListener(v -> {
            showRejectDialog(store, null, dialog);
        });

        dialog.show();
    }

    private void showDishDetailBottomSheet(PendingDish dish) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_dish_approval, null);
        dialog.setContentView(view);

        ImageView ivImage = view.findViewById(R.id.iv_detail_dish_image);
        TextView tvName = view.findViewById(R.id.tv_detail_dish_name);
        TextView tvBadge = view.findViewById(R.id.tv_detail_status_badge);
        TextView tvRejectReason = view.findViewById(R.id.tv_reject_reason_display);
        
        setDetailRow(view.findViewById(R.id.row_detail_store_name), "Cửa hàng", dish.getStoreName(), R.drawable.ic_my_store);
        setDetailRow(view.findViewById(R.id.row_detail_category), "Danh mục", dish.getCategory(), R.drawable.list_alt_24px);
        setDetailRow(view.findViewById(R.id.row_detail_price), "Giá bán", String.format("%,.0fđ", dish.getPrice()), R.drawable.ic_dollar_sign);
        setDetailRow(view.findViewById(R.id.row_detail_date), "Ngày gửi", dish.getSubmittedDate(), R.drawable.icon_transactionhistory_calendar_month_24px);

        if (dish.getImageRes() != 0) ivImage.setImageResource(dish.getImageRes());
        tvName.setText(dish.getDishName());

        updateBadgeUI(tvBadge, dish.getStatus());

        if (dish.getStatus().equals("rejected") && dish.getRejectReason() != null) {
            tvRejectReason.setVisibility(View.VISIBLE);
            tvRejectReason.setText("Lý do từ chối: " + dish.getRejectReason());
        }

        LinearLayout llAction = view.findViewById(R.id.ll_action_buttons);
        if (!dish.getStatus().equals("pending")) {
            llAction.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btn_close_sheet).setOnClickListener(v -> dialog.dismiss());
        
        view.findViewById(R.id.btn_approve).setOnClickListener(v -> {
            showApproveDialog(null, dish, dialog);
        });

        view.findViewById(R.id.btn_reject).setOnClickListener(v -> {
            showRejectDialog(null, dish, dialog);
        });

        dialog.show();
    }

    private void updateBadgeUI(TextView tvBadge, String status) {
        if (status.equals("pending")) {
            tvBadge.setText("Chờ duyệt");
            tvBadge.setBackgroundResource(R.drawable.bg_badge_pending);
            tvBadge.setTextColor(Color.parseColor("#F57C00"));
        } else if (status.equals("approved")) {
            tvBadge.setText("Đã duyệt");
            tvBadge.setBackgroundResource(R.drawable.bg_badge_approved);
            tvBadge.setTextColor(Color.parseColor("#4CAF50"));
        } else if (status.equals("rejected")) {
            tvBadge.setText("Từ chối");
            tvBadge.setBackgroundResource(R.drawable.bg_badge_rejected);
            tvBadge.setTextColor(Color.parseColor("#E53935"));
        }
    }

    private void showApproveDialog(PendingStore store, PendingDish dish, BottomSheetDialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_confirm_approve, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView ivItem = view.findViewById(R.id.img_dialog_item);
        TextView tvName = view.findViewById(R.id.tv_dialog_item_name);
        TextView tvPrompt = view.findViewById(R.id.tv_dialog_prompt);

        if (store != null) {
            if (store.getImageRes() != 0) ivItem.setImageResource(store.getImageRes());
            tvName.setText(store.getStoreName());
            tvPrompt.setText("Bạn chắc chắn muốn duyệt cửa hàng này?");
        } else if (dish != null) {
            if (dish.getImageRes() != 0) ivItem.setImageResource(dish.getImageRes());
            tvName.setText(dish.getDishName());
            tvPrompt.setText("Bạn chắc chắn muốn duyệt món ăn này?");
        }

        view.findViewById(R.id.btn_dialog_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_dialog_approve).setOnClickListener(v -> {
            if (store != null) {
                store.setStatus("approved");
                Toast.makeText(this, "Đã duyệt cửa hàng!", Toast.LENGTH_SHORT).show();
            } else if (dish != null) {
                dish.setStatus("approved");
                Toast.makeText(this, "Đã duyệt món ăn!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            if (parentDialog != null) parentDialog.dismiss();
            filterData(currentFilter);
        });

        dialog.show();
    }

    private void showRejectDialog(PendingStore store, PendingDish dish, BottomSheetDialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_reject_reason, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etReason = view.findViewById(R.id.et_reject_reason);
        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_confirm_reject).setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập lý do từ chối", Toast.LENGTH_SHORT).show();
                return;
            }

            if (store != null) {
                store.setStatus("rejected");
                store.setRejectReason(reason);
                Toast.makeText(this, "Đã từ chối cửa hàng!", Toast.LENGTH_SHORT).show();
            } else if (dish != null) {
                dish.setStatus("rejected");
                dish.setRejectReason(reason);
                Toast.makeText(this, "Đã từ chối món ăn!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            if (parentDialog != null) parentDialog.dismiss();
            filterData(currentFilter);
        });

        dialog.show();
    }

    private void setDetailRow(View rowView, String label, String value, int iconRes) {
        if (rowView == null) return;
        com.example.uitpayapp.profile.ProfileActivity.SetDetailMenuItem(rowView, label, value, iconRes);
        View menuLessThan = rowView.findViewById(R.id.menu_less_than);
        if (menuLessThan != null) {
            menuLessThan.setVisibility(View.GONE);
        }
    }
}
