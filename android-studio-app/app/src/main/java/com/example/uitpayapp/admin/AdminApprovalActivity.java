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
import com.example.uitpayapp.modules.merchant.MerchantRepository;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.modules.merchant.models.responses.MerchantRequestResponseDTO;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdminApprovalActivity extends AppCompatActivity {

    private RecyclerView rvApprovalList;
    private TextView tvFilterPending, tvFilterApproved, tvFilterRejected;
    private PendingStoreAdapter storeAdapter;
    private List<PendingStore> allStores = new ArrayList<>();

    private String currentFilter = "pending";
    private MerchantRepository merchantRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        androidx.core.view.WindowInsetsControllerCompat windowInsetsController = androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
        setContentView(R.layout.activity_admin_approval);

        merchantRepository = new MerchantRepository();

        initViews();
        setupFilters();

        // Default to store and pending
        filterData("pending");
    }

    private void initViews() {
        View topBar = findViewById(R.id.top_bar_admin_approval);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Quản lý duyệt");

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

        String[] statuses = {"pending", "approved", "rejected"};

        int storeId = 1;
        for (int i = 0; i < 5; i++) {
            String status = statuses[i % 3];
            allStores.add(new PendingStore("S" + storeId, "Cửa hàng " + storeId, "Chủ cửa hàng " + storeId, "Địa chỉ " + storeId, "Danh mục", 0, status, "20/05/2024"));
            storeId++;
        }
    }

    private void setupFilters() {
        tvFilterPending.setOnClickListener(v -> filterData("pending"));
        tvFilterApproved.setOnClickListener(v -> filterData("approved"));
        tvFilterRejected.setOnClickListener(v -> filterData("rejected"));
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

        String apiStatus = status.toUpperCase(); // "PENDING", "APPROVED", "REJECTED"
        merchantRepository.getRequestsByStatus(apiStatus, new ApiCallback<List<MerchantRequestResponseDTO>>() {
            @Override
            public void onSuccess(List<MerchantRequestResponseDTO> response) {
                List<PendingStore> pendingStores = new ArrayList<>();
                for (MerchantRequestResponseDTO dto : response) {
                    String id = dto.getId().toString();
                    String storeName = dto.getStoreName();
                    String ownerName = dto.getOwnerName() != null ? dto.getOwnerName() : (dto.getStorePhone() != null ? dto.getStorePhone() : "Chưa rõ");
                    String address = dto.getStoreAddress();
                    String storeType = "Cửa hàng ăn uống";
                    int imageRes = 0;
                    String displayStatus = dto.getStatus().toLowerCase();
                    String submittedDate = dto.getCreatedAt() != null ? dto.getCreatedAt().replace("T", " ") : "Chưa rõ";

                    PendingStore pendingStore = new PendingStore(id, storeName, ownerName, address, storeType, imageRes, displayStatus, submittedDate);
                    pendingStores.add(pendingStore);
                }
                storeAdapter = new PendingStoreAdapter(AdminApprovalActivity.this, pendingStores, AdminApprovalActivity.this::showStoreDetailBottomSheet);
                rvApprovalList.setAdapter(storeAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AdminApprovalActivity.this, "Lỗi tải danh sách: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
            showApproveDialog(store, dialog);
        });

        view.findViewById(R.id.btn_reject).setOnClickListener(v -> {
            showRejectDialog(store, dialog);
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

    private void showApproveDialog(PendingStore store, BottomSheetDialog parentDialog) {
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
        }

        view.findViewById(R.id.btn_dialog_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_dialog_approve).setOnClickListener(v -> {
            if (store != null) {
                Long requestId = Long.parseLong(store.getId());
                merchantRepository.approveRequest(requestId, new ApiCallback<MerchantRequestResponseDTO>() {
                    @Override
                    public void onSuccess(MerchantRequestResponseDTO response) {
                        Toast.makeText(AdminApprovalActivity.this, "Đã duyệt cửa hàng thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if (parentDialog != null) parentDialog.dismiss();
                        filterData(currentFilter);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminApprovalActivity.this, "Lỗi duyệt: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }

    private void showRejectDialog(PendingStore store, BottomSheetDialog parentDialog) {
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
                Long requestId = Long.parseLong(store.getId());
                merchantRepository.rejectRequest(requestId, new ApiCallback<MerchantRequestResponseDTO>() {
                    @Override
                    public void onSuccess(MerchantRequestResponseDTO response) {
                        Toast.makeText(AdminApprovalActivity.this, "Đã từ chối cửa hàng thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        if (parentDialog != null) parentDialog.dismiss();
                        filterData(currentFilter);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(AdminApprovalActivity.this, "Lỗi từ chối: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
