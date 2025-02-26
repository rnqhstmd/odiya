package org.example.odiya.common.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.odiya.common.exception.ApiException;
import org.example.odiya.common.exception.ForbiddenException;
import org.example.odiya.common.exception.type.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.example.odiya.common.exception.type.ErrorType.NO_AUTHORIZATION_ERROR;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiException apiException = new ForbiddenException(NO_AUTHORIZATION_ERROR);
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
