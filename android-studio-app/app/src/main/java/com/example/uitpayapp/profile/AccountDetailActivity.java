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

import com.example.uitpayapp.modules.user.UserRepository;
import com.example.uitpayapp.modules.user.models.responses.UserResponseDTO;
import com.example.uitpayapp.network.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccountDetailActivity extends AppCompatActivity {
    ImageView ivAvatar;
    TextView tvFullName,tvPhone;
    List<String> MainInfo;
    private java.io.File getFileFromUri(android.net.Uri uri) {
        try {
            java.io.File tempFile = new java.io.File(getCacheDir(), "temp_avatar.jpg");
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    java.io.File file = getFileFromUri(uri);
                    if (file != null) {
                        new UserRepository().uploadAvatar(file, new com.example.uitpayapp.network.ApiCallback<String>() {
                            @Override
                            public void onSuccess(String avatarUrl) {
                                updateAvatar(avatarUrl);
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                sharedPreferences.edit().putString("AVATAR_URL", avatarUrl).apply();

                                SessionManager sessionManager = SessionManager.getInstance(AccountDetailActivity.this);
                                sessionManager.updateProfileSession(
                                        sessionManager.getUserName(),
                                        avatarUrl,
                                        sessionManager.getUserEmail()
                                );
                                Toast.makeText(AccountDetailActivity.this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(AccountDetailActivity.this, "Cập nhật ảnh đại diện thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Không thể mở tệp ảnh đại diện", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_account_detail);
        initView();
        fetchUserProfile();
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
        tvFullName = findViewById(R.id.tv_full_name);
        tvPhone = findViewById(R.id.tv_phone_number);

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

    private void fetchUserProfile() {
        new UserRepository().getProfile(new com.example.uitpayapp.network.ApiCallback<UserResponseDTO>() {
            @Override
            public void onSuccess(UserResponseDTO data) {
                if (data != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("FULL_NAME", data.getFullName());
                    editor.putString("PHONE_NUMBER", data.getPhoneNumber());
                    editor.putString("AVATAR_URL", data.getAvatarUrl() != null ? data.getAvatarUrl() : "");
                    if (data.getEmail() != null) {
                        editor.putString("EMAIL", data.getEmail());
                    }
                    
                    String birthday = data.getBirthday() != null && !data.getBirthday().isEmpty() ? data.getBirthday() : "Chưa cập nhật";
                    String gender = data.getGender() != null && !data.getGender().isEmpty() ? data.getGender() : "Chưa cập nhật";
                    String job = data.getJob() != null && !data.getJob().isEmpty() ? data.getJob() : "Chưa cập nhật";
                    
                    editor.putString("BIRTHDAY", birthday);
                    editor.putString("GENDER", gender);
                    editor.putString("JOB", job);
                    editor.apply();

                    SessionManager sessionManager = SessionManager.getInstance(AccountDetailActivity.this);
                    sessionManager.updateProfileSession(
                            data.getFullName(),
                            data.getAvatarUrl(),
                            data.getEmail()
                    );

                    if (tvFullName != null) tvFullName.setText(data.getFullName());
                    if (tvPhone != null) tvPhone.setText(data.getPhoneNumber());
                    if (data.getAvatarUrl() != null && !data.getAvatarUrl().isEmpty()) {
                        updateAvatar(data.getAvatarUrl());
                    }

                    setRowData(R.id.inforow_username, "Tên", data.getFullName());
                    setRowData(R.id.inforow_email, "Email", data.getEmail() != null && !data.getEmail().isEmpty() ? data.getEmail() : "Chưa cập nhật");
                    setRowData(R.id.inforow_birthday, "Ngày sinh", birthday);
                    setRowData(R.id.inforow_gender, "Giới tính", gender);
                    setRowData(R.id.inforow_job, "Nghề nghiệp", job);
                }
            }

            @Override
            public void onError(String errorMessage) {
                loadUserDataLocal();
            }
        });
    }

    private void loadUserDataLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("FULL_NAME", "Người dùng ZaloPay");
        String savedPhone = sharedPreferences.getString("PHONE_NUMBER", "Chưa cập nhật");
        String savedAvatar = sharedPreferences.getString("AVATAR_URL", "");
        String email = sharedPreferences.getString("EMAIL", "Chưa cập nhật");
        String birthday = sharedPreferences.getString("BIRTHDAY", "Chưa cập nhật");
        String gender = sharedPreferences.getString("GENDER", "Chưa cập nhật");
        String job = sharedPreferences.getString("JOB", "Chưa cập nhật");

        tvFullName = findViewById(R.id.tv_full_name);
        tvPhone = findViewById(R.id.tv_phone_number);

        if (tvFullName != null) tvFullName.setText(savedName);
        if (tvPhone != null) tvPhone.setText(savedPhone);
        if (!savedAvatar.isEmpty()) {
            updateAvatar(savedAvatar);
        }

        setRowData(R.id.inforow_username, "Tên", savedName);
        setRowData(R.id.inforow_email, "Email", email);
        setRowData(R.id.inforow_birthday, "Ngày sinh", birthday);
        setRowData(R.id.inforow_gender, "Giới tính", gender);
        setRowData(R.id.inforow_job, "Nghề nghiệp", job);
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

            if (!email.isEmpty() && !email.equalsIgnoreCase(currentEmail)) {
                // Gửi mã OTP xác nhận về email mới
                android.app.ProgressDialog sendProgress = new android.app.ProgressDialog(AccountDetailActivity.this);
                sendProgress.setMessage("Đang gửi mã xác thực đến email mới...");
                sendProgress.setCancelable(false);
                sendProgress.show();

                new UserRepository().sendEmailOtp(email, new com.example.uitpayapp.network.ApiCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        runOnUiThread(() -> {
                            sendProgress.dismiss();
                            
                            // Hiển thị Dialog nhập mã OTP
                            android.app.AlertDialog.Builder otpBuilder = new android.app.AlertDialog.Builder(AccountDetailActivity.this);
                            otpBuilder.setTitle("Xác thực Email mới");
                            otpBuilder.setMessage("Vui lòng nhập mã xác thực (6 số) đã gửi đến email " + email + ":");

                            final android.widget.EditText otpInput = new android.widget.EditText(AccountDetailActivity.this);
                            otpInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                            otpInput.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            otpInput.setGravity(android.view.Gravity.CENTER);
                            otpBuilder.setView(otpInput);

                            otpBuilder.setPositiveButton("Xác nhận", (dialog, which) -> {
                                String otp = otpInput.getText().toString().trim();
                                if (otp.length() != 6) {
                                    Toast.makeText(AccountDetailActivity.this, "Mã OTP phải gồm 6 chữ số", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                performProfileUpdate(fullName, email, gender, birthday, job, otp, bottomSheetDialog);
                            });
                            otpBuilder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
                            otpBuilder.show();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            sendProgress.dismiss();
                            Toast.makeText(AccountDetailActivity.this, "Gửi OTP thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                // Không đổi email, cập nhật trực tiếp không cần OTP
                performProfileUpdate(fullName, email, gender, birthday, job, null, bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }

    private void performProfileUpdate(String fullName, String email, String gender, String birthday, String job, String emailOtp, BottomSheetDialog bottomSheetDialog) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật thông tin...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new UserRepository().updateProfile(fullName, email, gender, birthday, job, emailOtp, new com.example.uitpayapp.network.ApiCallback<UserResponseDTO>() {
            @Override
            public void onSuccess(UserResponseDTO userResponse) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    if (userResponse != null) {
                        if (email != null && !email.isEmpty()) setRowData(R.id.inforow_email, "Email", email);
                        if (fullName != null && !fullName.isEmpty()) {
                            setRowData(R.id.inforow_username, "Tên", fullName);
                            tvFullName.setText(fullName);
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("FULL_NAME", fullName);
                        editor.putString("EMAIL", email);
                        editor.putString("BIRTHDAY", birthday);
                        editor.putString("GENDER", gender);
                        editor.putString("JOB", job);
                        editor.apply();

                        if (birthday != null && !birthday.isEmpty()) setRowData(R.id.inforow_birthday, "Ngày sinh", birthday);
                        if (gender != null) setRowData(R.id.inforow_gender, "Giới tính", gender);
                        if (job != null) setRowData(R.id.inforow_job, "Nghề nghiệp", job);

                        SessionManager sessionManager = SessionManager.getInstance(AccountDetailActivity.this);
                        sessionManager.updateProfileSession(fullName, userResponse.getAvatarUrl(), email);

                        Toast.makeText(AccountDetailActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(AccountDetailActivity.this, "Cập nhật thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
