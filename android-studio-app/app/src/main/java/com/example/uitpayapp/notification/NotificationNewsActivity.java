package com.example.uitpayapp.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class NotificationNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_news);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvNewsNotifications);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        com.example.uitpayapp.modules.news.NewsRepository newsRepository = new com.example.uitpayapp.modules.news.NewsRepository();
        newsRepository.getActiveNews(new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.news.models.NewsDTO>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.modules.news.models.NewsDTO> data) {
                List<NewsNotification> list = new ArrayList<>();
                for (com.example.uitpayapp.modules.news.models.NewsDTO dto : data) {
                    list.add(new NewsNotification(
                            String.valueOf(dto.getId()),
                            dto.getTitle(),
                            dto.getContent(),
                            formatDateTime(dto.getCreatedAt())
                    ));
                }
                rv.setAdapter(new NewsAdapter(list));
            }

            @Override
            public void onError(String errorMessage) {
                android.widget.Toast.makeText(NotificationNewsActivity.this, "Lỗi tải tin tức: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDateTime(String isoString) {
        if (isoString == null) return "";
        try {
            String cleanStr = isoString;
            if (cleanStr.contains(".")) {
                int dotIdx = cleanStr.indexOf(".");
                int tIdx = cleanStr.indexOf("+");
                if (tIdx == -1) tIdx = cleanStr.indexOf("-", dotIdx);
                if (tIdx == -1) tIdx = cleanStr.indexOf("Z", dotIdx);
                if (tIdx != -1) {
                    cleanStr = cleanStr.substring(0, dotIdx) + cleanStr.substring(tIdx);
                } else {
                    cleanStr = cleanStr.substring(0, dotIdx);
                }
            }
            java.text.SimpleDateFormat inputFormat;
            if (cleanStr.endsWith("Z")) {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            } else if (cleanStr.contains("+") || (cleanStr.lastIndexOf("-") > 10)) {
                try {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.getDefault());
                    java.util.Date date = inputFormat.parse(cleanStr);
                    java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                    return outputFormat.format(date);
                } catch (Exception ex) {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                }
            } else {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            }
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(cleanStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoString;
        }
    }

    private static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        private List<NewsNotification> mList;
        public NewsAdapter(List<NewsNotification> list) { this.mList = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.activity_notification_news_item, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            NewsNotification m = mList.get(pos);
            h.t1.setText(m.getTitle());
            h.t2.setText(m.getSummary());
            h.t3.setText(m.getTimestamp());
        }

        @Override
        public int getItemCount() { return mList.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2, t3;
            public ViewHolder(@NonNull View v) {
                super(v);
                t1 = v.findViewById(R.id.tvNewsTitle);
                t2 = v.findViewById(R.id.tvNewsSummary);
                t3 = v.findViewById(R.id.tvNewsTime);
            }
        }
    }
}