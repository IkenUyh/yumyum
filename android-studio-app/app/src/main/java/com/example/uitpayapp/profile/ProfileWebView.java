package com.example.uitpayapp.profile;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;

public class ProfileWebView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilescreen_web_view);
        View topBar = findViewById(R.id.top_bar_webview);
        WebView webView = findViewById(R.id.profilescreen_webview);
        View webContainer = findViewById(R.id.webview_page_container);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom+10;
            if (webContainer != null) {
                webContainer.setPadding(webContainer.getPaddingLeft(), webContainer.getPaddingTop(), webContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("UITpay");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        String url = getIntent().getStringExtra("URL_KEY");
        if (url != null && !url.isEmpty()) {
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);
        }
    }
}