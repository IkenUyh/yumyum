package com.example.uitpayapp.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.home.home_adapters.ImageSliderAdapter;
import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.user.UserRepository;
import com.example.uitpayapp.modules.user.models.responses.AuthResponseDTO;
import com.example.uitpayapp.network.ApiCallback;

import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private TextView tvErrorPhone;
    private Button btnLogin;
    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private android.app.Dialog loadingDialog;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);
        LinearLayout navBottom = findViewById(R.id.navBottom);
        ViewCompat.setOnApplyWindowInsetsListener(navBottom, (v, insets) -> {
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            //ban phim
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            int safeBottomPadding = Math.max(navInsets.bottom,imeInsets.bottom)+10;
            if (v != null) {
                v.setPadding(v.getPaddingLeft(),v.getPaddingTop(),v.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        btnLogin= findViewById(R.id.btnLogin);
        tvErrorPhone = findViewById(R.id.tv_error_phone);
        edtPhoneNumber = findViewById(R.id.PhoneNumber);
        
        userRepository = new UserRepository();
        
        loadingDialog = new android.app.Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        edtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_rounded);
                tvErrorPhone.setVisibility(View.GONE);
            }
        });

        btnLogin.setOnClickListener(v -> {
            String phone = edtPhoneNumber.getText().toString();

            if(phone.isEmpty()){
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_error);
                tvErrorPhone.setText("Vui lòng nhập số điện thoại");
                tvErrorPhone.setVisibility(View.VISIBLE);
                return;
            }

            if (loadingDialog != null && !isFinishing()) loadingDialog.show();

            new android.os.Handler().postDelayed(() -> {
                proceedToPasscode(phone);
            }, 500);
        });

        // Nút quay lại
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Nút đăng ký
        View btnRegister = findViewById(R.id.btnRegister);
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        // Setup Banner Slider
        ViewPager2 viewPager2 = findViewById(R.id.imgAdvertisement);
        View bannerLoading = findViewById(R.id.layout_banner_loading);
        View bannerError = findViewById(R.id.layout_banner_error);
        
        if (viewPager2 != null) {
            ImageSliderAdapter adapter = new ImageSliderAdapter(new java.util.ArrayList<>());
            viewPager2.setAdapter(adapter);
            sliderHandler = new Handler(Looper.getMainLooper());
            sliderRunnable = new Runnable() {
                @Override
                public void run() {
                    if (adapter.getItemCount() > 1) {
                        int currentitem = viewPager2.getCurrentItem();
                        currentitem = (currentitem + 1) % adapter.getItemCount();
                        viewPager2.setCurrentItem(currentitem, true);
                    }
                    sliderHandler.postDelayed(this, 3000);
                }
            };
            sliderHandler.post(sliderRunnable);
            
            if (bannerLoading != null) bannerLoading.setVisibility(View.VISIBLE);
            if (bannerError != null) bannerError.setVisibility(View.GONE);
            viewPager2.setVisibility(View.GONE);

            com.example.uitpayapp.network.RetrofitClient.getHomeApiService().getHomeCore("").enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>> response) {
                    if (bannerLoading != null) bannerLoading.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<com.example.uitpayapp.home.network.Banner> banners = response.body().getData().getBanners();
                        if (banners != null && !banners.isEmpty()) {
                            viewPager2.setVisibility(View.VISIBLE);
                            List<String> urls = new java.util.ArrayList<>();
                            for (com.example.uitpayapp.home.network.Banner b : banners) urls.add(b.getImageUrl());
                            adapter.updateData(urls);
                        } else {
                            if (bannerError != null) bannerError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (bannerError != null) bannerError.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>> call, Throwable t) {
                    if (bannerLoading != null) bannerLoading.setVisibility(View.GONE);
                    if (bannerError != null) {
                        bannerError.setVisibility(View.VISIBLE);
                        TextView tvError = findViewById(R.id.tv_banner_error);
                        if (tvError != null) tvError.setText("Không kết nối được server");
                    }
                }
            });
        }
    }
    
    private void proceedToPasscode(String phone) {
        if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
        android.content.Intent intent = new android.content.Intent(SignInActivity.this, PasscodeActivity.class);
        intent.putExtra("PHONE_NUMBER", phone); // Gói dữ liệu
        startActivity(intent);
    }
}
