package com.example.uitpayapp.history;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChatContent;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private EditText etMessageInput;
    private ImageButton btnBackChat, btnGridMenu, btnSendMessage; // Đổi tên nút gửi ảnh thành nút gửi tin nhắn
    private TextView tvChatSubtitle;
    private LinearLayout lnQuickRepliesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Ánh xạ View
        rvChatContent = findViewById(R.id.rvChatContent);
        etMessageInput = findViewById(R.id.etChatMessageInput);
        btnBackChat = findViewById(R.id.btnBackChat);
        tvChatSubtitle = findViewById(R.id.tvChatSubtitle);
        lnQuickRepliesContainer = findViewById(R.id.lnQuickRepliesContainer);
        btnSendMessage = findViewById(R.id.btnSendMessage); // Ánh xạ nút gửi tin nhắn mới

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);

        rvChatContent.setLayoutManager(new LinearLayoutManager(this));
        rvChatContent.setAdapter(chatAdapter);

        // Đọc thông tin mã đơn nhận từ trang chi tiết
        String orderId = getIntent().getStringExtra("ORDER_ID");
        String merchantName = getIntent().getStringExtra("MERCHANT_NAME");
        if (orderId != null && merchantName != null) {
            tvChatSubtitle.setText(merchantName + " | Mã đơn: #" + orderId);
        }

        // Tải lịch sử chat mock ban đầu
        loadInitialMockChatHistory();

        // CÁCH 1: Click vào chính nút Mũi tên gửi trên màn hình (Sửa lỗi của ông)
        btnSendMessage.setOnClickListener(v -> {
            performSendMessage();
        });

        // CÁCH 2: Nhấn nút Enter / Gửi từ bàn phím ảo hệ thống
        etMessageInput.setOnEditorActionListener((v, actionId, event) -> {
            performSendMessage();
            return true;
        });

        // Bắt sự kiện click vào các hộp đáp nhanh (Quick Replies)
        setupQuickRepliesClickEvent();

        btnBackChat.setOnClickListener(v -> finish());
    }

    // Khối hàm xử lý gửi tin nhắn dùng chung (Dễ bảo trì và gán API)
    private void performSendMessage() {
        String text = etMessageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            appendUserMessage(text);
            etMessageInput.setText(""); // Xóa sạch khung nhập sau khi gửi thành công
        }
    }

    private void loadInitialMockChatHistory() {
        messages.add(new ChatMessage("sys_1", ChatMessage.TYPE_SYSTEM,
                "Chào bạn foodee_gl6s3niq, Tài xế sẽ nhận được tin nhắn của bạn ngay khi nhận đơn hàng. Nếu cần hỗ trợ, bạn vui lòng tham khảo Trung tâm Trợ giúp nhé!",
                "12:38", "Hỗ Trợ"));

        messages.add(new ChatMessage("user_1", ChatMessage.TYPE_USER,
                "Halo skibibi",
                "12:38", "foodee_gl6s3niq"));


        chatAdapter.notifyDataSetChanged();
        rvChatContent.scrollToPosition(messages.size() - 1);
    }

    private void appendUserMessage(String text) {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        messages.add(new ChatMessage(uniqueId, ChatMessage.TYPE_USER, text, "Vừa xong", "foodee_gl6s3niq"));

        chatAdapter.notifyItemInserted(messages.size() - 1);
        rvChatContent.scrollToPosition(messages.size() - 1);

        /* 💡 NƠI GẮN API SAU NÀY:
          if (webSocket != null) {
              String json = new com.google.gson.Gson().toJson(myMsg);
              webSocket.send(json);
          }
        */
    }

    private void setupQuickRepliesClickEvent() {
        for (int i = 0; i < lnQuickRepliesContainer.getChildCount(); i++) {
            View child = lnQuickRepliesContainer.getChildAt(i);
            if (child instanceof TextView) {
                TextView tvReply = (TextView) child;
                tvReply.setOnClickListener(v -> {
                    String text = tvReply.getText().toString();
                    appendUserMessage(text);
                });
            }
        }
    }
}