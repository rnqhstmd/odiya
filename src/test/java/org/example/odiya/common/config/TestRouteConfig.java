package org.example.odiya.common.config;

import org.example.odiya.route.service.GoogleRouteClient;
import org.example.odiya.route.service.TmapRouteClient;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestRouteConfig {

    @Bean
    @Order(1)
    @Qualifier("google")
    public GoogleRouteClient googleRouteClient() {
        return mock(GoogleRouteClient.class);
    }

    @Bean
    @Order(2)
    @Qualifier("tmap")
    public TmapRouteClient tmapRouteClient() {
        return mock(TmapRouteClient.class);
    }
}
