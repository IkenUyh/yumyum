package com.example.uitpayapp.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

            if(phone.isEmpty()){
                Toast.makeText(SignInActivity.this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            loading.setVisibility(View.VISIBLE);
            handler.post(runnable);

            // Giả lập loading nhẹ 0.5s cho mượt rồi chuyển màn hình
            new Handler().postDelayed(() -> {
                loading.setVisibility(View.GONE);
                handler.removeCallbacks(runnable);
                sliderHandler.removeCallbacks(sliderRunnable);
                android.content.Intent intent = new android.content.Intent(SignInActivity.this, PasscodeActivity.class);
                intent.putExtra("PHONE_NUMBER", phone); // Gói dữ liệu
                startActivity(intent);
            }, 500);
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
