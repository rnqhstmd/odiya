package org.example.odiya.route.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RouteClientProperties.class)
public class RouteConfig {
}
