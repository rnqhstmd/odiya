package org.example.odiya.route.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "google.api")
public class RouteClientProperty {

    private String key;
    private String url;
}
