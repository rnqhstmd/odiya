package org.example.odiya.route.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "google.api")
public class RouteClientProperty {

    private final String key;
    private final String url;

    public RouteClientProperty(String key, String url) {
        this.key = key;
        this.url = url;
    }
}
