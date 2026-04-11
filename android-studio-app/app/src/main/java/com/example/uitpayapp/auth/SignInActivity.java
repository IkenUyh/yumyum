package com.example.uitpayapp.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.home.ImageSliderAdapter;
import com.example.uitpayapp.R;

import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private Button btnLogin;
    private Handler sliderHandler;
    private Runnable sliderRunnable;
    FrameLayout loading;
    TextView txtDots;
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);
        btnLogin= findViewById(R.id.btnLogin);
        loading = findViewById(R.id.loadingOverlay);
        txtDots = findViewById(R.id.txtDots);
        handler = new Handler(Looper.getMainLooper());
        edtPhoneNumber = findViewById(R.id.PhoneNumber);
        runnable = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                count = (count + 1) % 4;

                String dots = "";
                for (int i = 0; i < count; i++) {
                    dots += "●";
                }

                txtDots.setText(dots);

                handler.postDelayed(this, 500);
            }
        };
        btnLogin.setOnClickListener(v -> {
            String phone = edtPhoneNumber.getText().toString();
            loading.setVisibility(View.VISIBLE);
            handler.post(runnable);

            // giả lập loading 2 giây
            new Handler().postDelayed(() -> {
                loading.setVisibility(View.GONE);
                handler.removeCallbacks(runnable);
                sliderHandler.removeCallbacks(sliderRunnable);

                android.content.Intent intent = new android.content.Intent(SignInActivity.this, PasscodeActivity.class);
                startActivity(intent);
            }, 2000);
        });
        List <Integer> imageList = List.of(
                R.drawable.img_advertisment1,
                R.drawable.img_advertisment2,
                R.drawable.img_advertisment3);
        ViewPager2 viewPager2 = findViewById(R.id.imgAdvertisement);
        //adapter se la trung gian dua anh len viewpaper
        ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
        viewPager2.setAdapter(adapter);
        sliderHandler=new Handler(Looper.getMainLooper());
        sliderRunnable=new Runnable() {
            @Override
            public void run() {
                int currentitem=viewPager2.getCurrentItem();
                currentitem=(currentitem+1)%imageList.size();
                viewPager2.setCurrentItem(currentitem,true);
                sliderHandler.postDelayed(this,3000);
            }
        };
        sliderHandler.post(sliderRunnable);
    }
}
