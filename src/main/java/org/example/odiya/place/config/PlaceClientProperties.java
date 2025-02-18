package org.example.odiya.place.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "place")
public class PlaceClientProperties {

    private final PlaceClientProperty kakao;

    public PlaceClientProperties(PlaceClientProperty kakao) {
        this.kakao = kakao;
    }
}
