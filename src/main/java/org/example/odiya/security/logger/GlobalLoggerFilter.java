package org.example.odiya.security.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class GlobalLoggerFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-FORWARDED-FOR";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("[Global] HTTP Request Received! ({} {} {})",
                request.getHeader(HEADER_NAME) != null ? request.getHeader(HEADER_NAME) : request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI());

        request.setAttribute("INTERCEPTOR_PRE_HANDLE_TIME",  System.currentTimeMillis());

        filterChain.doFilter(request, response);

        Long preHandleTime = (Long) request.getAttribute("INTERCEPTOR_PRE_HANDLE_TIME");
        Long postHandleTime = System.currentTimeMillis();

        log.info("[Global] HTTP Request Has Been Processed! It Tokes {}ms. ({} {} {})",
                postHandleTime - preHandleTime,
                request.getHeader(HEADER_NAME) != null ? request.getHeader(HEADER_NAME) : request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI());
    }
}
