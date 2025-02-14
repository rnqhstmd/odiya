package org.example.odiya.common.config;

import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test")
public class TestFcmConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return mock(FirebaseMessaging.class);
    }
}
