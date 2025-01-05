package org.example.odiya.security.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.odiya.common.exception.ApiException;
import org.example.odiya.common.exception.type.ErrorType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ApiException apiException = (ApiException) exception.getCause();
        setResponse(response, apiException);
    }

    private void setResponse(HttpServletResponse response, ApiException apiException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        int status = apiException.getHttpStatus().value();
        ErrorType errorType = apiException.getErrorType();
        response.setStatus(status);
        response.getWriter().println(
                "{\"status\" : \"" + status + "\"," +
                        "\"errorCode\" : \"" + errorType.getErrorCode() + "\"," +
                        "\"message\" : \"" + errorType.getMessage() + "\"}"
        );
    }
}
