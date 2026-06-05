package com.example.uitpayapp.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class NotificationOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_order);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvOrderNotifications);
        List<OrderNotification> list = new ArrayList<>();

        list.add(new OrderNotification("1",
                "Đơn hàng tại Hồng Trà Sữa Ba Cô Gái Tam Hảo - 100 Ba Tháng Hai, Cần Thơ đã hoàn tất",
                "Cảm ơn bạn đã sử dụng dịch vụ ShopeeFood. Hãy chia sẻ cảm nhận của bạn về đơn hàng để giúp những Khách hàng khác có thể tham khảo nhé!",
                "21/05/2026 17:47", android.R.drawable.ic_menu_gallery));

        list.add(new OrderNotification("2",
                "Đơn hàng tại TIỆM BÚN A NHỬU - MẠC THIÊN TÍCH đã hoàn tất",
                "Cảm ơn bạn đã sử dụng dịch vụ ShopeeFood. Hãy chia sẻ cảm nhận của bạn về đơn hàng để giúp những Khách hàng khác có thể tham khảo nhé!",
                "21/03/2026 19:42", android.R.drawable.ic_menu_gallery));

        rv.setAdapter(new OrderAdapter(list));
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private static class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
        private List<OrderNotification> mList;
        public OrderAdapter(List<OrderNotification> list) { this.mList = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.activity_notification_order_item, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            OrderNotification m = mList.get(pos);
            h.t1.setText(m.getTitle());
            h.t2.setText(m.getContent());
            h.t3.setText(m.getTimestamp());
            h.iv.setImageResource(m.getShopImageResId());
        }

        @Override
        public int getItemCount() { return mList.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2, t3; ImageView iv;
            public ViewHolder(@NonNull View v) {
                super(v);
                t1 = v.findViewById(R.id.tvOrderTitle);
                t2 = v.findViewById(R.id.tvOrderContent);
                t3 = v.findViewById(R.id.tvOrderTime);
                iv = v.findViewById(R.id.ivShopThumb);
            }
        }
    }
}