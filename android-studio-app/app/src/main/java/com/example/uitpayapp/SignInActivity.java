package com.example.uitpayapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private Button btnLogin;
    FrameLayout loading;
    TextView txtDots;
    Handler handler;
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }



}
