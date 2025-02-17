package org.example.odiya.place.config;

import lombok.RequiredArgsConstructor;
import org.example.odiya.place.service.KakaoPlaceSearchClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PlaceClientProperties.class)
public class PlaceConfig {

    @Bean
    public KakaoPlaceSearchClient kakaoPlaceSearchClient(
            PlaceClientProperties properties,
            RestTemplate restTemplate
    ) {
        return new KakaoPlaceSearchClient(properties, restTemplate);
    }
}
