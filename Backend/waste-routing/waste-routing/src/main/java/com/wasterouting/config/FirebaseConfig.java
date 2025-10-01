package com.wasterouting.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    // Database URL is optional for Firestore
    @Value("${firebase.database.url:}")
    private String databaseUrl;

    @PostConstruct
    public void initialize() {
        try (InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream()) {
            logger.info("Initializing Firebase with config from: {}", firebaseConfigPath);

            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount));

            // Only set database URL if it's provided (for Realtime Database)
            if (databaseUrl != null && !databaseUrl.trim().isEmpty()) {
                optionsBuilder.setDatabaseUrl(databaseUrl);
                logger.info("Using Firebase Realtime Database URL: {}", databaseUrl);
            } else {
                logger.info("No database URL provided, using Firestore only");
            }

            FirebaseOptions options = optionsBuilder.build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application has been initialized");
            } else {
                logger.info("Firebase app already initialized");
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase: " + e.getMessage(), e);
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
