package org.example.odiya.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.example.odiya.common.exception.type.ErrorType.FILE_PROCESS_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.FIREBASE_INIT_ERROR;

@Slf4j
@Configuration
public class FcmConfig {

    @Value("${fcm.config.admin-sdk}")
    private String adminSdk;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }

    @PostConstruct
    public void initialize() {
        try {
            FirebaseApp.initializeApp(buildOptions());
            log.info("Fcm 설정 성공");
        } catch (IOException exception) {
            log.error("Fcm IOException : {}", exception.getMessage());
            throw new InternalServerException(FILE_PROCESS_ERROR, exception.getMessage());
        } catch (Exception e) {
            log.error("Fcm Exception : {}", e.getMessage());
            throw new InternalServerException(FIREBASE_INIT_ERROR, e.getMessage());
        }
    }

    private FirebaseOptions buildOptions() throws IOException {
        return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(adminSdk.getBytes())))
                .build();
    }
}
