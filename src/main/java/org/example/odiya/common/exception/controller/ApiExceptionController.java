package org.example.odiya.common.exception.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.ApiException;
import org.example.odiya.common.exception.dto.ApiExceptionResponse;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.common.exception.type.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionController {

    // ApiException 예외를 처리하는 핸들러(커스텀 예외)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionResponse> customExceptionHandler(final ApiException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(ApiExceptionResponse.res(e));
    }

    // HandlerMethodValidationException 예외를 처리하는 핸들러(요청 시 검증을 통과하지 못한 경우)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if (fieldError != null) {
            ErrorType errorType = ErrorType.resolveValidationErrorCode(fieldError.getCode());
            String detail = fieldError.getDefaultMessage();
            ApiExceptionResponse response = new ApiExceptionResponse(
                    errorType.getErrorCode(),
                    errorType.getMessage(),
                    detail
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ObjectError globalError = exception.getBindingResult().getGlobalError();
        if (globalError != null) {
            ApiExceptionResponse response = new ApiExceptionResponse(
                    "VALIDATION_ERROR",
                    "날짜/시간 검증 실패",
                    globalError.getDefaultMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ApiExceptionResponse response = new ApiExceptionResponse(
                HttpStatus.BAD_REQUEST.name(),
                "Validation Error",
                "Request validation failed without specific field errors."
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // HttpRequestMethodNotSupportedException 예외를 처리하는 핸들러(요청의 메소드가 잘못된 경우)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiExceptionResponse> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {

        BadRequestException badRequestException = new BadRequestException(
                ErrorType.METHOD_NOT_ALLOWED_ERROR, httpRequestMethodNotSupportedException.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiExceptionResponse.res(badRequestException));
    }

    // NoResourceFoundException 예외를 처리하는 핸들러(리소스를 찾을 수 없는 경우(URI가 잘못된 경우))
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleNoResourceFoundException(
            final NoResourceFoundException noResourceFoundException) {

        BadRequestException badRequestException = new BadRequestException(
                ErrorType.NO_RESOURCE_ERROR, noResourceFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiExceptionResponse.res(badRequestException));
    }

    // HandlerMethodValidationException 예외를 처리하는 핸들러 (요청 시 검증을 통과하지 못한 경우)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiExceptionResponse> handlerMethodValidationExceptionHandler(
            final HandlerMethodValidationException e) {

        String failedParameter = e.getValueResults().get(0).getMethodParameter().getParameterName()
                + " : "
                + e.getDetailMessageArguments()[0].toString();

        BadRequestException badRequestException = new BadRequestException(
                ErrorType.INVALID_REQUEST_PARAMETER_ERROR, failedParameter);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiExceptionResponse.res(badRequestException));
    }

    // MethodArgumentTypeMismatchException 예외를 처리하는 핸들러 (요청 파라메터의 타입이 잘못된 경우)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiExceptionResponse> methodArgumentTypeMismatchExceptionHandler(
            final MethodArgumentTypeMismatchException e) {

        BadRequestException badRequestException = new BadRequestException(
                ErrorType.INVALID_REQUEST_PARAMETER_ERROR, e.getName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiExceptionResponse.res(badRequestException));
    }

    // MissingServletRequestParameterException 예외를 처리하는 핸들러(필수 요청 파라미터가 누락된 경우)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiExceptionResponse> missingServletRequestParameterExceptionHandler(
            final MissingServletRequestParameterException e) {

        BadRequestException badRequestException = new BadRequestException(
                ErrorType.MISSING_REQUEST_PARAMETER_ERROR, e.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiExceptionResponse.res(badRequestException));
    }

    // HttpMessageNotReadableException 예외를 처리하는 핸들러(Body가 잘못된 경우(json 형식이 잘못된 경우))
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionResponse> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e) {

        BadRequestException badRequestException = new BadRequestException(
                ErrorType.INVALID_REQUEST_FORMAT_ERROR, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiExceptionResponse.res(badRequestException));
    }

    // JsonProcessingException 예외를 처리하는 핸들러
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiExceptionResponse> handleJsonProcessingException(JsonProcessingException e) {
        log.error("Json processing error: {}", e.getMessage());

        BadRequestException badRequestException = new BadRequestException(
                ErrorType.INVALID_REQUEST_FORMAT_ERROR, e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiExceptionResponse.res(badRequestException));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(final Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiExceptionResponse.res(e));
    }
}
