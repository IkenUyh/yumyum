package com.example.uitpayapp.modules.chat;

import android.util.Log;
import com.example.uitpayapp.modules.chat.models.responses.ChatMessageResponse;
import com.google.gson.Gson;
import java.util.*;
import okhttp3.*;

public class StompSockJsClient {
    private static final String TAG = "StompSockJsClient";
    
    public interface StompListener {
        void onConnected();
        void onMessageReceived(ChatMessageResponse message);
        void onError(String error);
        void onDisconnected();
    }

    private final String baseUrl;
    private final String authToken;
    private final Long orderId;
    private final StompListener listener;
    private final Gson gson = new Gson();
    private final OkHttpClient client;
    
    private WebSocket webSocket;
    private boolean isConnected = false;

    public StompSockJsClient(String baseUrl, String authToken, Long orderId, StompListener listener) {
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.orderId = orderId;
        this.listener = listener;
        this.client = new OkHttpClient();
    }

    public void connect() {
        String wsUrl = buildSockJsUrl(baseUrl);
        Log.d(TAG, "Connecting to WebSocket URL: " + wsUrl);

        Request.Builder builder = new Request.Builder().url(wsUrl);
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }
        Request request = builder.build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket handshake opened");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                handleIncomingMessage(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closed: " + reason);
                isConnected = false;
                listener.onDisconnected();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket failure: " + t.getMessage(), t);
                isConnected = false;
                listener.onError(t.getMessage());
            }
        });
    }

    public void disconnect() {
        if (webSocket != null) {
            if (isConnected) {
                sendStompFrame("DISCONNECT", null, null);
            }
            webSocket.close(1000, "User disconnected");
            webSocket = null;
        }
        isConnected = false;
    }

    public void sendMessage(Long senderId, String content) {
        if (!isConnected || webSocket == null) {
            listener.onError("Chưa kết nối đến máy chủ.");
            return;
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("senderId", String.valueOf(senderId));
        payload.put("content", content);
        String bodyJson = gson.toJson(payload);

        Map<String, String> headers = new HashMap<>();
        headers.put("destination", "/app/chat.sendMessage/" + orderId);
        headers.put("content-type", "application/json");

        sendStompFrame("SEND", headers, bodyJson);
    }

    private void sendStompFrame(String command, Map<String, String> headers, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append(command).append("\n");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                sb.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
            }
        }
        sb.append("\n");
        if (body != null) {
            sb.append(body);
        }
        sb.append("\u0000");

        String rawFrame = sb.toString();
        String sockJsFrame = gson.toJson(Collections.singletonList(rawFrame));
        
        Log.d(TAG, "Sending frame: " + sockJsFrame);
        if (webSocket != null) {
            webSocket.send(sockJsFrame);
        }
    }

    private void handleIncomingMessage(String text) {
        if (text.equals("o")) {
            Map<String, String> headers = new HashMap<>();
            headers.put("accept-version", "1.1,1.0");
            headers.put("heart-beat", "10000,10000");
            sendStompFrame("CONNECT", headers, null);
        } else if (text.startsWith("a")) {
            try {
                String jsonArray = text.substring(1);
                String[] rawStompFrames = gson.fromJson(jsonArray, String[].class);
                for (String rawFrame : rawStompFrames) {
                    parseAndProcessStompFrame(rawFrame);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing SockJS array frame", e);
            }
        } else if (text.equals("h")) {
            // Heartbeat
        } else if (text.startsWith("c")) {
            isConnected = false;
            listener.onDisconnected();
        }
    }

    private void parseAndProcessStompFrame(String rawFrame) {
        if (rawFrame == null || rawFrame.isEmpty()) return;

        if (rawFrame.endsWith("\u0000")) {
            rawFrame = rawFrame.substring(0, rawFrame.length() - 1);
        }

        int doubleNewlineIndex = rawFrame.indexOf("\n\n");
        if (doubleNewlineIndex == -1) {
            doubleNewlineIndex = rawFrame.indexOf("\r\n\r\n");
        }

        String headersSection;
        String body = "";
        if (doubleNewlineIndex != -1) {
            headersSection = rawFrame.substring(0, doubleNewlineIndex);
            int skip = rawFrame.startsWith("\n\n", doubleNewlineIndex) ? 2 : 4;
            body = rawFrame.substring(doubleNewlineIndex + skip);
        } else {
            headersSection = rawFrame;
        }

        String[] headerLines = headersSection.split("\\r?\\n");
        if (headerLines.length == 0) return;

        String command = headerLines[0].trim();
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < headerLines.length; i++) {
            String line = headerLines[i];
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                headers.put(line.substring(0, colonIndex).trim(), line.substring(colonIndex + 1).trim());
            }
        }

        if ("CONNECTED".equals(command)) {
            isConnected = true;
            Map<String, String> subHeaders = new HashMap<>();
            subHeaders.put("id", "sub-0");
            subHeaders.put("destination", "/topic/order/" + orderId);
            sendStompFrame("SUBSCRIBE", subHeaders, null);
            listener.onConnected();
        } else if ("MESSAGE".equals(command)) {
            try {
                ChatMessageResponse chatMessage = gson.fromJson(body, ChatMessageResponse.class);
                listener.onMessageReceived(chatMessage);
            } catch (Exception e) {
                Log.e(TAG, "Error deserializing message body", e);
            }
        } else if ("ERROR".equals(command)) {
            String errorMsg = headers.get("message");
            if (errorMsg == null) errorMsg = body;
            listener.onError(errorMsg);
        }
    }

    private String buildSockJsUrl(String baseUrl) {
        String wsUrl = baseUrl;
        if (wsUrl.startsWith("http://")) {
            wsUrl = "ws://" + wsUrl.substring(7);
        } else if (wsUrl.startsWith("https://")) {
            wsUrl = "wss://" + wsUrl.substring(8);
        }
        if (!wsUrl.endsWith("/")) {
            wsUrl += "/";
        }
        wsUrl += "ws/chat";

        int serverId = (int) (Math.random() * 1000);
        String serverIdStr = String.format(Locale.US, "%03d", serverId);

        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        String sessionId = sb.toString();

        return wsUrl + "/" + serverIdStr + "/" + sessionId + "/websocket";
    }
}
