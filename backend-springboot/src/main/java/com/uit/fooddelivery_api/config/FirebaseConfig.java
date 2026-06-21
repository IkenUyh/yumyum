package com.uit.fooddelivery_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${app.firebase.config-path:}")
    private String configPath;

    private boolean firebaseEnabled = false;

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount;
            if (configPath != null && !configPath.isEmpty()) {
                log.info("Loading Firebase credentials from custom path: {}", configPath);
                serviceAccount = new FileInputStream(configPath);
            } else {
                log.info("Loading Firebase credentials from default resource path: firebase-service-account.json");
                serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
            }

            if (serviceAccount == null) {
                log.warn("Firebase service account credentials file (firebase-service-account.json) NOT found in classpath or specified path. FCM will be disabled.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            
            firebaseEnabled = true;
            log.info("Firebase Application has been successfully initialized.");
        } catch (Exception e) {
            log.error("Failed to initialize Firebase Application: {}. FCM will be disabled.", e.getMessage(), e);
        }
    }

    public boolean isFirebaseEnabled() {
        return firebaseEnabled;
    }
}
