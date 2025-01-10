package org.example.odiya.security.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.example.odiya.common.exception.ApiException;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.security.jwt.provider.JwtProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static org.example.odiya.common.exception.type.ErrorType.INTERNAL_SERVER_ERROR;
import static org.example.odiya.common.util.CookieUtil.getCookieValue;
import static org.example.odiya.common.constant.Constants.WHITE_LIST;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthenticationFailureHandler failureHandler;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (isPublicUri(requestURI)) {
            // Public uri 일 경우 검증 안함
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwtToken = getCookieValue(request);
            jwtProvider.isValidToken(jwtToken);
            jwtProvider.getAuthenticationFromToken(jwtToken);
        } catch (ApiException e) {
            failureHandler.onAuthenticationFailure(request, response,
                    new InsufficientAuthenticationException(e.getMessage(), e));
            return;
        } catch (Exception e) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicUri(final String requestURI) {
        return Arrays.stream(WHITE_LIST)
                .anyMatch(pattern ->
                        new AntPathMatcher().match(pattern, requestURI));
    }
}

