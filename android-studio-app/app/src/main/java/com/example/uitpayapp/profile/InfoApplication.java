package com.example.uitpayapp.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.List;

public class InfoApplication extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_application);
        View pagetitle = findViewById(R.id.top_bar_info_application);
        ViewCompat.setOnApplyWindowInsetsListener(pagetitle, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //hong can thanh duoi
            return insets;
        });
        TextView topbarTitle = pagetitle.findViewById(R.id.top_bar_title);
        topbarTitle.setText("Thông tin ứng dụng");
        pagetitle.findViewById(R.id.top_bar_back_btn).setOnClickListener(v->finish());
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems = new ArrayList<>();
        ListItems.add(new MenuItemData("Bình chọn YumYum","",R.drawable.ic_info_app_evaluation,false));
        ListItems.add(new MenuItemData("Điều khoản dịch vụ","",R.drawable.ic_info_app_term,false));
        ListItems.add(new MenuItemData("Quy chế hoạt động","",R.drawable.ic_info_app_law,false));
        ListGroupItem.add(new GroupItemData("",ListItems));
        ProfileMenuAdapter adapter=new ProfileMenuAdapter(this,ListGroupItem,itemMenu->HanleItemClick(itemMenu));
        RecyclerView recyclerView = findViewById(R.id.rv_info_application);
        recyclerView.setAdapter(adapter);
    }
    private void HanleItemClick(MenuItemData item) {
        if (item.getTitle().equals("Bình chọn YumYum")) {
            String packetName = "com.deliverynow";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packetName)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packetName)));
            }
            return;
        }

        Intent intent = new Intent(this, ProfileWebView.class);
        if (item.getTitle().equals("Điều khoản dịch vụ")) {
            intent.putExtra("URL_KEY", "https://help.cs.shopeefood.vn/portal/103/article/74116?previousPage=other%20articles");
        } else if (item.getTitle().equals("Quy chế hoạt động")) {
            intent.putExtra("URL_KEY", "https://help.cs.shopeefood.vn/portal/103/article/73880-Quy-ch%E1%BA%BF-ho%E1%BA%A1t-%C4%91%E1%BB%99ng");
        }
        startActivity(intent);
    }
}

