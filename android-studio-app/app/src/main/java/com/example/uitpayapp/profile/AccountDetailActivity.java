package com.example.uitpayapp.profile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccountDetailActivity extends AppCompatActivity {
    ImageView ivAvatar;
    TextView tvFullName,tvPhone;
    List<String> MainInfo;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    updateAvatar(uri.toString());
                    // Lưu vào SharedPreferences để đồng bộ với ProfileActivity
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    sharedPreferences.edit().putString("AVATAR_URL", uri.toString()).apply();
                    Toast.makeText(this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_account_detail);
        initView();
        loadUserData();
        setNoVerifyData();
        findViewById(R.id.update_secondary_info).setOnClickListener(v -> HanleUpdateSecondaryInfo());

        findViewById(R.id.btn_change_avatar).setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_account_detail);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Thông tin chi tiết");
        View mainContainer = findViewById(R.id.account_detail_container);
        ivAvatar = findViewById(R.id.iv_avatar);

        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            int safeBottomPadding = Math.max(navInsets.bottom,imeInsets.bottom)+10;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("FULL_NAME", "Người dùng ZaloPay");
        String savedPhone = sharedPreferences.getString("PHONE_NUMBER", "Chưa cập nhật");
        String savedAvatar = sharedPreferences.getString("AVATAR_URL", "");

        tvFullName = findViewById(R.id.tv_full_name);
        tvPhone = findViewById(R.id.tv_phone_number);

        if (tvFullName != null) tvFullName.setText(savedName);
        if (tvPhone != null) tvPhone.setText(savedPhone);
        
        if (!savedAvatar.isEmpty()) {
            updateAvatar(savedAvatar);
        }
    }

    private void updateAvatar(String url) {
        if (ivAvatar != null) {
            Glide.with(this)
                    .load(url)
                    .circleCrop()
                    .placeholder(R.drawable.yumyum_demo_logo)
                    .into(ivAvatar);
        }
    }

    private void setNoVerifyData() {
        setRowData(R.id.inforow_username, "Tên", "Chưa cập nhật");
        setRowData(R.id.inforow_gender, "Giới tính", "Chưa cập nhật");
        setRowData(R.id.inforow_birthday, "Ngày sinh", "Chưa cập nhật");
        setRowData(R.id.inforow_email, "Email", "Chưa cập nhật");
        setRowData(R.id.inforow_job, "Nghề nghiệp", "Chưa cập nhật");
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
    
    private void HanleUpdateSecondaryInfo() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_update_info, null);
        bottomSheetDialog.setContentView(view);

        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etFullName= view.findViewById(R.id.et_fullname);
        EditText etBirthday= view.findViewById(R.id.et_birthday);
        Spinner spGender= view.findViewById(R.id.sp_gender);
        Spinner spJob= view.findViewById(R.id.sp_job);

        String currentEmail = ((TextView) findViewById(R.id.inforow_email).findViewById(R.id.tv_value)).getText().toString();
        String currentJob = ((TextView) findViewById(R.id.inforow_job).findViewById(R.id.tv_value)).getText().toString();
        String currentFullName =((TextView) findViewById(R.id.inforow_username).findViewById(R.id.tv_value)).getText().toString();
        String currentBirthday =((TextView) findViewById(R.id.inforow_birthday).findViewById(R.id.tv_value)).getText().toString();
        String currentGender =((TextView) findViewById(R.id.inforow_gender).findViewById(R.id.tv_value)).getText().toString();

        if (!currentEmail.equals("Chưa cập nhật")) etEmail.setText(currentEmail);
        if (!currentFullName.equals("Chưa cập nhật")) etFullName.setText(currentFullName);
        if (!currentBirthday.equals("Chưa cập nhật")) etBirthday.setText(currentBirthday);

        // Setup Gender Spinner
        String[] genders = {"Nam", "Nữ", "Không công khai"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        spGender.setAdapter(genderAdapter);
        if (!currentGender.equals("Chưa cập nhật")) {
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equalsIgnoreCase(currentGender)) {
                    spGender.setSelection(i);
                    break;
                }
            }
        }

        // Setup Job Spinner
        String[] jobs={"Nhân viên văn phòng","Freelancer","Sinh viên/Học sinh","Ở nhà","Nghề nghiệp khác"};
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, jobs);
        spJob.setAdapter(jobAdapter);
        if (!currentJob.equals("Chưa cập nhật")) {
            for (int i = 0; i < jobs.length; i++) {
                if (jobs[i].equalsIgnoreCase(currentJob)) {
                    spJob.setSelection(i);
                    break;
                }
            }
        }

        etBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            String birthStr = etBirthday.getText().toString();
            if (!birthStr.isEmpty()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date = sdf.parse(birthStr);
                    if (date != null) calendar.setTime(date);
                } catch (ParseException ignored) {}
            }
            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (dview, year, month, dayOfMonth) -> {
                        @SuppressLint("DefaultLocale") String dateStr = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etBirthday.setText(dateStr);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        view.findViewById(R.id.btn_close_update_secondary_info).setOnClickListener(v -> bottomSheetDialog.dismiss());

        view.findViewById(R.id.btn_save_update).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String birthday = etBirthday.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString();
            String job = spJob.getSelectedItem().toString();

            if (!email.isEmpty()) setRowData(R.id.inforow_email, "Email", email);
            if (!fullName.isEmpty()) {
                setRowData(R.id.inforow_username, "Tên", fullName);
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                sharedPreferences.edit().putString("FULL_NAME", fullName).apply();
                tvFullName.setText(fullName);
            }
            if (!birthday.isEmpty()) setRowData(R.id.inforow_birthday, "Ngày sinh", birthday);
            setRowData(R.id.inforow_gender, "Giới tính", gender);
            setRowData(R.id.inforow_job, "Nghề nghiệp", job);

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }
}
