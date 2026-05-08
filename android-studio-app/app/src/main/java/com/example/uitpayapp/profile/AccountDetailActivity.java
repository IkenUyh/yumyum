package com.example.uitpayapp.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.uitpayapp.R;
import com.example.uitpayapp.ScanQRCode.QRScanActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AccountDetailActivity extends AppCompatActivity {
    TextView tvVerifyStatus;
    View infoVerifyMainStatus, infoNoVerifyMainStatus;
    List<String> MainInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        initView();
        setNoVerifyData();
        infoNoVerifyMainStatus.setOnClickListener(v -> HanleVerifyAccount());
        findViewById(R.id.info_verify_showmore).setOnClickListener(v -> showInfoBottomSheet());
        findViewById(R.id.update_secondary_info).setOnClickListener(v -> HanleUpdateSecondaryInfo());
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_account_detail);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Thông tin chi tiết");
        View mainContainer = findViewById(R.id.account_detail_container);
        tvVerifyStatus = findViewById(R.id.info_tv_verify_status);
        infoVerifyMainStatus = findViewById(R.id.info_verify_main_status);
        infoNoVerifyMainStatus = findViewById(R.id.info_no_verify_main_status);

        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
    }

    private void setNoVerifyData() {
        setRowData(R.id.inforow_name, "Tên", "Chưa cập nhật");
        setRowData(R.id.inforow_gender, "Giới tính", "Chưa cập nhật");
        setRowData(R.id.inforow_birthday, "Ngày sinh", "Chưa cập nhật");
        setRowData(R.id.inforow_id_number, "Căn cước công dân", "Chưa cập nhật");
        setRowData(R.id.inforow_issue_date, "Ngày cấp giấy tờ", "Chưa cập nhật");
        setRowData(R.id.inforow_issue_place, "Nơi cấp giấy tờ", "Chưa cập nhật");
        
        setRowData(R.id.inforow_email, "Email", "Chưa cập nhật");
        setRowData(R.id.inforow_address, "Địa chỉ", "Chưa cập nhật");
        setRowData(R.id.inforow_job, "Nghề nghiệp", "Chưa cập nhật");

        infoVerifyMainStatus.setVisibility(View.GONE);
        infoNoVerifyMainStatus.setVisibility(View.VISIBLE);
        tvVerifyStatus.setText("Chưa xác thực");
        tvVerifyStatus.setBackgroundResource(R.drawable.bg_button_login_rounded);
        tvVerifyStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#fbebeb")));
        tvVerifyStatus.setTextColor(Color.parseColor("#e02449"));
    }

    private void setRowData(int viewId, String label, String value) {
        View row = findViewById(viewId);
        if (row != null) {
            TextView tvLabel = row.findViewById(R.id.tv_label);
            TextView tvValue = row.findViewById(R.id.tv_value);
            if (tvLabel != null) tvLabel.setText(label);
            if (tvValue != null) tvValue.setText(value);
        }
    }

    private final ActivityResultLauncher<Intent> qrScanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            String resultData = result.getData().getStringExtra("QR_DATA");
            if (resultData != null) {
                HandleCCCDData(resultData);
            }
        }
    });

    private void HanleVerifyAccount() {
        Intent intent = new Intent(this, QRScanActivity.class);
        intent.putExtra("SCAN_TYPE", "CCCD");
        qrScanLauncher.launch(intent);
    }
    private void HandleCCCDData(String resultData) {
        String[] items = resultData.split("\\|");
        items[3]=SplitDate(items[3]);
        items[6]=SplitDate(items[6]);
        MainInfo=new ArrayList<>(Arrays.asList(items));
        MainInfo.remove(1);
        if (MainInfo.size() == 6) {
            setRowData(R.id.inforow_id_number, "Căn cước công dân", HideMainInfo(MainInfo.get(0), 3));
            setRowData(R.id.inforow_name, "Tên", MainInfo.get(1));
            setRowData(R.id.inforow_birthday, "Ngày sinh", HideMainInfo(MainInfo.get(2), 5));
            setRowData(R.id.inforow_gender, "Giới tính", MainInfo.get(3));
            setRowData(R.id.inforow_address, "Địa chỉ", MainInfo.get(4));
            setRowData(R.id.inforow_issue_date, "Ngày cấp giấy tờ", HideMainInfo(MainInfo.get(5), 5));
            setRowData(R.id.inforow_issue_place, "Nơi cấp giấy tờ", "CỤC CẢNH SÁT QUẢY LÝ HÀNH CHÍNH VỀ TRẬT TỰ XÃ HỘI");
            setVerifiedStatus();
        }
    }
    private String HideMainInfo(String text, int lengthShow)
    {
        int startsub=text.length()-lengthShow;
        String result="";
        for (int i=0;i<startsub;i++) {
            result += "*";
        };
        result += text.substring(startsub);
        return result;
    }
    private String SplitDate(String input)
    {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMyyyy");

        try {
            Date date=inputFormat.parse(input);
            return outputFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private void setVerifiedStatus() {
        infoVerifyMainStatus.setVisibility(View.VISIBLE);
        infoNoVerifyMainStatus.setVisibility(View.GONE);
        tvVerifyStatus.setText("Đã xác thực");
        tvVerifyStatus.setBackgroundResource(R.drawable.bg_button_login_rounded);
        tvVerifyStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
        tvVerifyStatus.setTextColor(Color.parseColor("#2E7D32"));
    }
    private void showInfoBottomSheet()
    {
        if (infoNoVerifyMainStatus.getVisibility()==View.VISIBLE)
        {
            Toast.makeText(this,"Vui lòng xác thực tài khoản trước!",Toast.LENGTH_SHORT).show();
            return;
        }
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view=getLayoutInflater().inflate(R.layout.layout_dynamic_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);
        ((TextView)view.findViewById(R.id.sheet_title)).setText("Thông tin chi tiết");
        view.findViewById(R.id.btn_close).setOnClickListener(v->bottomSheetDialog.dismiss());
        LinearLayout container=view.findViewById(R.id.sheet_container);
        //
        if (MainInfo!=null||MainInfo.size()==6)
        {
            addInfoRowToSheet(container, "Tên", MainInfo.get(1));
            addInfoRowToSheet(container, "Số CCCD", MainInfo.get(0));
            addInfoRowToSheet(container, "Giới tính", MainInfo.get(3));
            addInfoRowToSheet(container, "Ngày sinh", MainInfo.get(2));
            addInfoRowToSheet(container, "Căn cước công dân", MainInfo.get(0));
            addInfoRowToSheet(container, "Ngày cấp giấy tờ", MainInfo.get(5));
            addInfoRowToSheet(container, "Nơi cấp giấy tờ", "CỤC CẢNH SÁT QUẢN LÝ HÀNH CHÍNH VỀ TRẬT TỰ XÃ HỘI");
        } else return;

        bottomSheetDialog.show();
    }
    private void addInfoRowToSheet(android.widget.LinearLayout container, String label, String value) {
        View row = getLayoutInflater().inflate(R.layout.item_info_row, null);
        ((TextView) row.findViewById(R.id.tv_label)).setText(label);
        ((TextView) row.findViewById(R.id.tv_value)).setText(value);
        container.addView(row);
    }
    private void HanleUpdateSecondaryInfo() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_update_info, null);
        bottomSheetDialog.setContentView(view);

        EditText etEmail = view.findViewById(R.id.et_update_email);
        EditText etAddress = view.findViewById(R.id.et_update_address);
        TextView tvJob = view.findViewById(R.id.tv_update_job);

        String currentEmail = ((TextView) findViewById(R.id.inforow_email).findViewById(R.id.tv_value)).getText().toString();
        String currentAddress = ((TextView) findViewById(R.id.inforow_address).findViewById(R.id.tv_value)).getText().toString();
        String currentJob = ((TextView) findViewById(R.id.inforow_job).findViewById(R.id.tv_value)).getText().toString();

        if (!currentEmail.equals("Chưa cập nhật")) etEmail.setText(currentEmail);
        if (!currentAddress.equals("Chưa cập nhật")) etAddress.setText(currentAddress);
        if (!currentJob.equals("Chưa cập nhật")) tvJob.setText(currentJob);

        view.findViewById(R.id.btn_close_update_secondary_info).setOnClickListener(v -> bottomSheetDialog.dismiss());
        view.findViewById(R.id.btn_popup_job).setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            String[] jobs = {"Nhân viên văn phòng", "Kinh doanh tự do", "Học sinh/Sinh viên", "Công nhân","Nông dân", "Nghỉ hưu", "Khác"};
            for (String job : jobs) {
                popupMenu.getMenu().add(job);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                tvJob.setText(item.getTitle());
                return true;
            });
            popupMenu.show();
        });

        view.findViewById(R.id.btn_save_update).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String job = tvJob.getText().toString().trim();

            if (!email.isEmpty()) setRowData(R.id.inforow_email, "Email", email);
            if (!address.isEmpty()) setRowData(R.id.inforow_address, "Địa chỉ", address);
            if (!job.isEmpty() && !job.equals("Chọn nghề nghiệp")) setRowData(R.id.inforow_job, "Nghề nghiệp", job);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
}
