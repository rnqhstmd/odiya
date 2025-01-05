package org.example.odiya.security.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;


public class CookieUtil {

    public static final String TOKEN_NAME = "jwtToken";

    public static void addCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie = ResponseCookie.from(TOKEN_NAME, value)
                .path("/")
                .httpOnly(true)
                .secure(true) // HTTPS를 사용하는 경우 true
                .sameSite("None").secure(true)
                .maxAge(720000000)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String getCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TOKEN_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 쿠키가 없는 경우 null 반환
    }

    public static void clearCookies(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(TOKEN_NAME, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .sameSite("None").secure(true)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
