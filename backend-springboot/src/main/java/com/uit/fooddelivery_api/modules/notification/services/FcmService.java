package com.uit.fooddelivery_api.modules.notification.services;

import com.google.firebase.messaging.*;
import com.uit.fooddelivery_api.config.FirebaseConfig;
import com.uit.fooddelivery_api.modules.notification.entities.UserFcmToken;
import com.uit.fooddelivery_api.modules.notification.repositories.UserFcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final FirebaseConfig firebaseConfig;
    private final UserFcmTokenRepository userFcmTokenRepository;

    @org.springframework.scheduling.annotation.Async
    @Transactional
    public void sendPushNotification(Long userId, String title, String message, String type) {
        if (!firebaseConfig.isFirebaseEnabled()) {
            log.warn("FCM is disabled. Skipping push notification for userId={}", userId);
            return;
        }

        List<UserFcmToken> userTokens = userFcmTokenRepository.findByUserId(userId);
        if (userTokens.isEmpty()) {
            log.debug("No registered FCM tokens found for userId={}", userId);
            return;
        }

        List<String> tokens = userTokens.stream()
                .map(UserFcmToken::getFcmToken)
                .toList();

        try {
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .putData("type", type)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
            
            for (int i = 0; i < response.getResponses().size(); i++) {
                SendResponse sendResponse = response.getResponses().get(i);
                if (!sendResponse.isSuccessful()) {
                    String token = tokens.get(i);
                    FirebaseMessagingException exception = sendResponse.getException();
                    log.warn("FCM sending failed for token={}. Error: {}", token, exception != null ? exception.getMessage() : "Unknown error");
                    
                    if (exception != null && (exception.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED 
                            || exception.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT)) {
                        log.info("Removing inactive/invalid FCM token: {}", token);
                        userFcmTokenRepository.deleteByFcmToken(token);
                    }
                }
            }
            log.info("FCM push notification sent. Sent count: {}, Failed count: {}", response.getSuccessCount(), response.getFailureCount());
        } catch (Exception e) {
            log.error("Error occurred while sending FCM multicast notification for userId={}: {}", userId, e.getMessage(), e);
        }
    }
}
