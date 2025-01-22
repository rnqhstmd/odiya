package org.example.odiya.route.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "route")
public class RouteClientProperties {

    private final RouteClientProperty google;
    private final RouteClientProperty tmap;

    public RouteClientProperties(RouteClientProperty google, RouteClientProperty tmap) {
        this.google = google;
        this.tmap = tmap;
    }
}
