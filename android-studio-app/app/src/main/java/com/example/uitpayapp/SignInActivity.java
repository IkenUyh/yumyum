package com.example.uitpayapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);
        edtPhoneNumber = findViewById(R.id.PhoneNumber);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            String phone = edtPhoneNumber.getText().toString();
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
