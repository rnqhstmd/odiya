package org.example.odiya.common.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.odiya.common.exception.ApiException;
import org.example.odiya.common.exception.UnauthorizedException;
import org.example.odiya.common.exception.type.ErrorType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.example.odiya.common.exception.type.ErrorType.TOKEN_NOT_INCLUDED_ERROR;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if (authException.getCause() instanceof ApiException apiException) {
            setResponse(response, apiException);
            return;
        }

        ApiException apiException = new UnauthorizedException(TOKEN_NOT_INCLUDED_ERROR);
        setResponse(response, apiException);
    }

    private void setResponse(HttpServletResponse response, ApiException apiException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ErrorType errorType = apiException.getErrorType();
        int status = apiException.getHttpStatus().value();
        response.setStatus(status);

        response.getWriter().println(
                "{\"status\" : \"" + status + "\"," +
                        "\"errorCode\" : \"" + errorType.getErrorCode() + "\"," +
                        "\"message\" : \"" + errorType.getMessage() + "\"}");
    }
}
