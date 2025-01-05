package org.example.odiya.common.exception.dto;

import org.example.odiya.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public record ApiExceptionResponse(String errorCode, String message, String detail) {

    public static ApiExceptionResponse res(final ApiException apiException) {
        return new ApiExceptionResponse(
                apiException.getErrorType().getErrorCode(),
                apiException.getErrorType().getMessage(),
                apiException.getDetail()
        );
    }

    public static ApiExceptionResponse res(final Exception e) {
        return new ApiExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "서버 내부 오류가 발생했습니다.",
                e.getMessage()
        );
    }

    @Override
    public String toString() {
        return "ApiExceptionResponse {" +
                "errorCode=" + errorCode +
                ", message='" + message +
                ", detail='" + detail +
                '}';
    }
}
