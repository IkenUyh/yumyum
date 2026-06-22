package com.uit.fooddelivery_api.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Kích hoạt máy chủ trung gian xử lý tin nhắn
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Frontend Android/Web sẽ mở kết nối tới đường dẫn này để bắt đầu Chat
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Fallback nếu mạng yếu rớt WebSocket thì chuyển qua HTTP Streaming
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Hậu tố "/topic" dùng để Server phát sóng (Broadcast) tin nhắn ra cho các client đang lắng nghe
        registry.enableSimpleBroker("/topic");

        // Hậu tố "/app" dùng để Client gửi tin nhắn lên Server (gọi vào các hàm @MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");
    }
}