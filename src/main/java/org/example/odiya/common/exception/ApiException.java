package org.example.odiya.common.exception;

import lombok.Getter;
import org.example.odiya.common.exception.type.ErrorType;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final ErrorType errorType;
    private final String detail;

    protected ApiException(final ErrorType errorType, final HttpStatus httpStatus) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.detail = null;
        this.httpStatus = httpStatus;
    }

    protected ApiException(
            final ErrorType errorType,
            final String detail,
            final HttpStatus httpStatus) {

        super(errorType.getMessage());
        this.errorType = errorType;
        this.detail = detail;
        this.httpStatus = httpStatus;
    }
}
