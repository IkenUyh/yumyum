package com.example.uitpayapp.history;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.chat.ChatRepository;
import com.example.uitpayapp.modules.chat.StompSockJsClient;
import com.example.uitpayapp.modules.chat.models.responses.ChatMessageResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChatContent;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private EditText etMessageInput;
    private ImageButton btnBackChat, btnSendMessage;
    private TextView tvChatSubtitle;
    private LinearLayout lnQuickRepliesContainer;

    // Các trường phục vụ kết nối API và WebSocket
    private ChatRepository chatRepository;
    private StompSockJsClient stompClient;
    private Long orderIdLong;
    private Long userId;
    private boolean isChatLocked = false;

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
        btnSendMessage = findViewById(R.id.btnSendMessage);

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);

        rvChatContent.setLayoutManager(new LinearLayoutManager(this));
        rvChatContent.setAdapter(chatAdapter);

        // Đọc thông tin mã đơn nhận từ trang chi tiết
        String orderId = getIntent().getStringExtra("ORDER_ID");
        String merchantName = getIntent().getStringExtra("MERCHANT_NAME");
        isChatLocked = getIntent().getBooleanExtra("IS_CHAT_LOCKED", false);

        if (orderId != null && merchantName != null) {
            tvChatSubtitle.setText(merchantName + " | Mã đơn: #" + orderId);
        }

        // Lấy thông tin người dùng hiện tại
        userId = SessionManager.getInstance(this).getUserId();

        // Kiểm tra xem phòng chat có bị khóa do đơn hàng đã hoàn tất/hủy hay không
        if (isChatLocked) {
            etMessageInput.setEnabled(false);
            etMessageInput.setHint("Cuộc trò chuyện đã kết thúc");
            btnSendMessage.setEnabled(false);
            lnQuickRepliesContainer.setVisibility(View.GONE);
        }

        // Cố gắng chuyển đổi ID đơn hàng thành số
        try {
            if (orderId != null) {
                orderIdLong = Long.parseLong(orderId);
            }
        } catch (NumberFormatException e) {
            orderIdLong = null; // Trả về null nếu ID là dạng chuỗi mock có dấu gạch ngang
        }

        // Khởi tạo Repository và bắt đầu tải lịch sử chat
        chatRepository = new ChatRepository();
        loadChatHistoryAndConnect();

        // Click vào nút gửi tin nhắn trên màn hình
        btnSendMessage.setOnClickListener(v -> performSendMessage());

        // Nhấn nút Enter / Gửi từ bàn phím ảo hệ thống
        etMessageInput.setOnEditorActionListener((v, actionId, event) -> {
            performSendMessage();
            return true;
        });

        // Bắt sự kiện click vào các hộp đáp nhanh (Quick Replies)
        setupQuickRepliesClickEvent();

        btnBackChat.setOnClickListener(v -> finish());
    }

    private void loadChatHistoryAndConnect() {
        if (orderIdLong != null) {
            // Tải lịch sử chat từ Backend REST API
            chatRepository.fetchChatHistory(orderIdLong, new ApiCallback<List<ChatMessageResponse>>() {
                @Override
                public void onSuccess(List<ChatMessageResponse> history) {
                    messages.clear();
                    for (ChatMessageResponse msg : history) {
                        int senderType = ChatMessage.TYPE_DRIVER;
                        if (msg.getSenderId() != null && msg.getSenderId().equals(userId)) {
                            senderType = ChatMessage.TYPE_USER;
                        }
                        String displayName = (senderType == ChatMessage.TYPE_USER) ? "Bạn" : (msg.getSenderName() != null ? msg.getSenderName() : "Tài xế");
                        messages.add(new ChatMessage(
                                String.valueOf(msg.getId()),
                                senderType,
                                msg.getContent(),
                                formatTimestamp(msg.getCreatedAt()),
                                displayName
                        ));
                    }
                    chatAdapter.notifyDataSetChanged();
                    if (!messages.isEmpty()) {
                        rvChatContent.scrollToPosition(messages.size() - 1);
                    }

                    // Khởi tạo kết nối WebSocket cho thời gian thực
                    initWebSocket();
                }

                @Override
                public void onError(String errorMessage) {
                    // Nếu lỗi kết nối, tự động chuyển sang chế độ giả lập (Simulation mode) để không làm crash UI
                    Toast.makeText(ChatActivity.this, "Không thể tải lịch sử từ máy chủ. Đang bật chế độ mô phỏng.", Toast.LENGTH_SHORT).show();
                    loadInitialMockChatHistory();
                }
            });
        } else {
            // Chạy chế độ mô phỏng nếu đơn hàng là đơn hàng mock tĩnh
            Toast.makeText(this, "Đang chạy chế độ mô phỏng (Đơn hàng thử nghiệm)", Toast.LENGTH_SHORT).show();
            loadInitialMockChatHistory();
        }
    }

    private void initWebSocket() {
        if (isChatLocked) return;

        String token = SessionManager.getInstance(this).getAuthToken();
        // Server URL được cấu hình trực tiếp từ endpoint hệ thống
        stompClient = new StompSockJsClient("https://kienhuy-dev.name.vn/", token, orderIdLong, new StompSockJsClient.StompListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    Log.d("ChatActivity", "WebSocket STOMP connected.");
                    Toast.makeText(ChatActivity.this, "Đã kết nối trực tiếp với tài xế", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onMessageReceived(ChatMessageResponse message) {
                runOnUiThread(() -> {
                    // Kiểm tra trùng lặp tin nhắn trước khi hiển thị
                    boolean isDuplicate = false;
                    for (ChatMessage existingMsg : messages) {
                        if (String.valueOf(message.getId()).equals(existingMsg.getMessageId())) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate) {
                        int senderType = ChatMessage.TYPE_DRIVER;
                        if (message.getSenderId() != null && message.getSenderId().equals(userId)) {
                            senderType = ChatMessage.TYPE_USER;
                        }
                        String displayName = (senderType == ChatMessage.TYPE_USER) ? "Bạn" : (message.getSenderName() != null ? message.getSenderName() : "Tài xế");
                        messages.add(new ChatMessage(
                                String.valueOf(message.getId()),
                                senderType,
                                message.getContent(),
                                formatTimestamp(message.getCreatedAt()),
                                displayName
                        ));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        rvChatContent.scrollToPosition(messages.size() - 1);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("ChatActivity", "WebSocket Error: " + error);
                    Toast.makeText(ChatActivity.this, "Lỗi kết nối chat: " + error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> Log.d("ChatActivity", "WebSocket disconnected."));
            }
        });
        stompClient.connect();
    }

    private void performSendMessage() {
        String text = etMessageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            if (stompClient != null) {
                stompClient.sendMessage(userId, text);
                etMessageInput.setText("");
            } else {
                // Chế độ mô phỏng cục bộ
                appendUserMessage(text);
                etMessageInput.setText("");
            }
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
        messages.add(new ChatMessage(uniqueId, ChatMessage.TYPE_USER, text, "Vừa xong", "Bạn"));

        chatAdapter.notifyItemInserted(messages.size() - 1);
        rvChatContent.scrollToPosition(messages.size() - 1);
    }

    private void setupQuickRepliesClickEvent() {
        for (int i = 0; i < lnQuickRepliesContainer.getChildCount(); i++) {
            View child = lnQuickRepliesContainer.getChildAt(i);
            if (child instanceof TextView) {
                TextView tvReply = (TextView) child;
                tvReply.setOnClickListener(v -> {
                    String text = tvReply.getText().toString();
                    if (stompClient != null) {
                        stompClient.sendMessage(userId, text);
                    } else {
                        appendUserMessage(text);
                    }
                });
            }
        }
    }

    private String formatTimestamp(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) return "Vừa xong";
        try {
            if (createdAt.contains("T")) {
                String timePart = createdAt.split("T")[1];
                String[] parts = timePart.split(":");
                return parts[0] + ":" + parts[1];
            }
        } catch (Exception e) {
            // ignore
        }
        return createdAt;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }
}