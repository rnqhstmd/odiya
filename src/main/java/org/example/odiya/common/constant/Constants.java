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

    public static final String MODE_TRANSIT = "transit";
    public static final String STATUS_OK = "OK";

    public static final String VERSION = "1";
    public static final String COORD_TYPE = "WGS84GEO";
    public static final String SEARCH_OPTION = "0";
    public static final String SORT = "index";
}
