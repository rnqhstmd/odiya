package org.example.odiya.common.constant;

public class Constants {

    private Constants() {
    }

    public static final String[] WHITE_LIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**",
            "/favicon.ico",
            "/h2-console/**",
            "/api/auth/**"
    };

    public static final int WALKING_THRESHOLD = 1000;
}
