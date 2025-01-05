package org.example.odiya.security.jwt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.ApiException;
import org.example.odiya.common.exception.type.ErrorType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecurityException e) {
            log.error("FilterException throw SecurityException Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorType.NO_AUTHORIZATION_ERROR);
        } catch (MalformedJwtException e) {
            log.error("FilterException throw MalformedJwtException Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorType.TOKEN_MALFORMED_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("FilterException throw IllegalArgumentException Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorType.TOKEN_TYPE_ERROR);
        } catch (ExpiredJwtException e) {
            log.error("FilterException throw ExpiredJwtException Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorType.TOKEN_EXPIRED_ERROR);
        } catch (UnsupportedJwtException e) {
            log.error("FilterException throw UnsupportedJwtException Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorType.TOKEN_UNSUPPORTED_ERROR);
        } catch (JwtException e) {
            log.error("FilterException throw JwtException Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorType.TOKEN_UNKNOWN_ERROR);
        } catch (ApiException e) {
            log.error("FilterException throw Exception Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorType.TOKEN_UNKNOWN_ERROR);
        } catch (Exception e) {
            log.error("FilterException throw Exception Exception : {}", e.getMessage());
            setErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, int status, ErrorType errorType) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":\"" + status + "\",\"errorCode\":\"" + errorType.getErrorCode() + "\",\"message\":\"" + errorType.getMessage() + "\"}");
    }
}