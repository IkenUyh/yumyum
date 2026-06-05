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
        List<NewsNotification> list = new ArrayList<>();

        list.add(new NewsNotification("1",
                "[HCMC, HN, CT] 📢 Cập nhật chính sách ShopeeFood",
                "Ra mắt tính năng dịch vụ \"Lấy tại quán\" &amp; cập nhật các chính sách liên quan từ ngày 19.05.2026",
                "26/05/2026 18:00"));

        rv.setAdapter(new NewsAdapter(list));
        rv.setLayoutManager(new LinearLayoutManager(this));
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