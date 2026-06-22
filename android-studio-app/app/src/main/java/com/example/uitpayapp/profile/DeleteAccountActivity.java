package com.example.uitpayapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.auth.SignInActivity;
import com.example.uitpayapp.modules.user.UserRepository;
import com.example.uitpayapp.network.SessionManager;

public class DeleteAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_delete_account);

        initView();

        findViewById(R.id.btn_continue_delete).setOnClickListener(v -> deleteAccount());
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_delete_account);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Xoá tài khoản");

        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        View bottomContainer = findViewById(R.id.bottom_container);
        ViewCompat.setOnApplyWindowInsetsListener(bottomContainer, (v, insets) -> {
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            int safeBottomPadding = Math.max(navInsets.bottom, imeInsets.bottom) + 10;
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), safeBottomPadding);
            return insets;
        });
    }

    private void deleteAccount() {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Đang xoá tài khoản...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new UserRepository().deleteAccount(new com.example.uitpayapp.network.ApiCallback<String>() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(DeleteAccountActivity.this, "Đã xoá tài khoản thành công", Toast.LENGTH_LONG).show();
                    
                    // Logout and go to Login
                    SessionManager.getInstance(DeleteAccountActivity.this).clearSession();
                    Intent intent = new Intent(DeleteAccountActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(DeleteAccountActivity.this, "Xoá tài khoản thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
