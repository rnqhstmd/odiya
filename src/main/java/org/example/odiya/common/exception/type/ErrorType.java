package org.example.odiya.common.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // Token
    TOKEN_EXPIRED_ERROR("TOKEN_40100", "토큰의 유효 기간이 만료되었습니다."),
    TOKEN_NOT_INCLUDED_ERROR("TOKEN_40101", "요청에 토큰이 포함되지 않았습니다."),
    TOKEN_MALFORMED_ERROR("TOKEN_40102", "토큰 형식이 올바르지 않습니다."),
    TOKEN_TYPE_ERROR("TOKEN_40103", "토큰 타입이 일치하지 않거나 비어있습니다."),
    TOKEN_UNSUPPORTED_ERROR("TOKEN_40104", "지원하지 않는 토큰입니다."),
    TOKEN_UNKNOWN_ERROR("TOKEN_40105", "알 수 없는 토큰입니다."),

    // Cookie
    COOKIE_NOT_FOUND_ERROR("COOKIE_40100", "쿠키가 존재하지 않습니다."),

    // Auth
    NO_AUTHORIZATION_ERROR("AUTH_40100", "인증이 없는 사용자입니다."),
    PASSWORD_NOT_MATCH_ERROR("AUTH_40101", "이메일 또는 비밀번호가 일치하지 않습니다."),

    // Resource
    NO_RESOURCE_ERROR("RESOURCE_40000", "해당 리소스를 찾을 수 없습니다."),

    // HTTP
    METHOD_NOT_ALLOWED_ERROR("HTTP_40500", "잘못된 HTTP 메서드입니다."),
    INVALID_REQUEST_PARAMETER_ERROR("HTTP_40000", "요청 파라미터가 잘못되었습니다."),
    MISSING_REQUEST_PARAMETER_ERROR("HTTP_40001", "필수 요청 파라미터가 누락되었습니다."),
    INVALID_REQUEST_FORMAT_ERROR("HTTP_40002", "요청에 필요한 데이터가 잘못되었습니다."),

    // Member
    MEMBER_NOT_FOUND_ERROR("MEMBER_40400", "해당 회원을 찾을 수 없습니다."),
    DUPLICATION_EMAIL_ERROR("MEMBER_40901", "이미 사용중인 이메일입니다."),

    // Map
    SEARCH_RESULT_NOT_FOUND_ERROR("MAP_40400", "검색 결과가 없습니다."),
    PLACE_NOT_FOUND_ERROR("MAP_40401", "해당 장소를 찾을 수 없습니다."),

    // Meeting
    MEETING_NOT_FOUND_ERROR("MEETING_40400", "해당 약속을 찾을 수 없습니다."),
    MEETING_OVERDUE_ERROR("MEETING_40401", "해당 약속은 이미 종료되었습니다."),
    NO_DATE_AND_TIME_ERROR("MEETING_40402", "설정된 약속 시간이 없습니다."),
    NOT_ONE_HOUR_BEFORE_MEETING_ERROR("MEETING_40000", "현재 시간이 설정된 약속 시간 1시간 전보다 이전입니다."),
    NOT_SAME_MEETING_ERROR("MEETING_40001", "재촉한 참여자와 재촉 당한 참여자가 같은 약속에 속해있지 않습니다."),

    // Mate
    DUPLICATION_MATE_ERROR("MATE_40900", "해당 약속에 이미 참여한 멤버입니다."),
    NOT_PARTICIPATED_MATE_ERROR("MATE_40300", "해당 약속에 참여하지 않았습니다."),
    MATE_NOT_FOUND_ERROR("MATE_40400", "해당 약속 참여자를 찾을 수 없습니다."),
    NOT_LATE_MATE_ERROR("MATE_40000", "해당 약속 참여자는 지각상태가 아닙니다."),
    REQUEST_MATE_MATCH_ERROR("MATE_40001", "재촉한 참여자와 로그인된 사용자가 일치하지 않습니다."),

    // Eta
    MATE_ETA_NOT_FOUND_ERROR("ETA_40400", "약속 참여자의 ETA 상태를 찾을 수 없습니다."),

    // Notification
    NOTIFICATION_NOT_FOUND_ERROR("NOTIFICATION_40400", "해당 알림을 찾을 수 없습니다."),

    // Client
    SEARCH_ROUTE_NOT_FOUND_ERROR("CLIENT_40400", "경로를 찾을 수 없습니다."),
    INVALID_ROUTE_REQUEST_ERROR("CLIENT_40000", "경로 요청이 잘못되었습니다."),
    REQUEST_DENIED_ERROR("CLIENT_40003", "요청이 거부되었습니다."),
    CLIENT_TYPE_NOT_FOUND_ERROR("CLIENT_40401", "해당 클라이언트 타입을 찾을 수 없습니다."),

    // Internal Server
    INTERNAL_SERVER_ERROR("INTERNAL_50000", "서버 내부 에러입니다."),
    EXTERNAL_API_ERROR("INTERNAL_50001", "외부 API 호출 에러입니다."),
    REST_TEMPLATE_ERROR("INTERNAL_50002", "RestTemplate 에러입니다."),
    TOO_MANY_REQUEST_ERROR("INTERNAL_50003", "외부 API 일일 호출 한도를 초과했습니다."),
    FIREBASE_INIT_ERROR("INTERNAL_50004", "Firebase 초기화 에러입니다."),
    FILE_PROCESS_ERROR("INTERNAL_50005", "파일 처리 중 에러가 발생했습니다."),
    FIREBASE_SUBSCRIBE_ERROR("INTERNAL_50006", "Firebase Subscribe 중 에러가 발생했습니다."),
    FIREBASE_UNSUBSCRIBE_ERROR("INTERNAL_50007", "Firebase Unsubscribe 중 에러가 발생했습니다."),
    FIREBASE_SEND_ERROR("INTERNAL_50008", "Firebase Send 중 에러가 발생했습니다."),

    // Validation
    NOT_NULL_VALID_ERROR("VALID_90000", "필수값이 누락되었습니다."),
    NOT_BLANK_VALID_ERROR("VALID_90001", "필수값이 빈 값이거나 공백으로 되어있습니다."),
    REGEX_VALID_ERROR("VALID_90002", "형식에 맞지 않습니다."),
    LENGTH_VALID_ERROR("VALID_90003", "길이가 유효하지 않습니다.");

    private final String errorCode;
    private final String message;

    public static ErrorType resolveValidationErrorCode(String code) {
        return switch (code) {
            case "NotNull" -> NOT_NULL_VALID_ERROR;
            case "NotBlank" -> NOT_BLANK_VALID_ERROR;
            case "Pattern" -> REGEX_VALID_ERROR;
            case "Size" -> LENGTH_VALID_ERROR;
            default -> throw new IllegalArgumentException("Unexpected value: " + code);
        };
    }

    public static ErrorType resolveClientApiStatus(String status) {
        return switch (status) {
            case "ZERO_RESULTS" -> SEARCH_ROUTE_NOT_FOUND_ERROR;
            case "INVALID_REQUEST" -> INVALID_ROUTE_REQUEST_ERROR;
            case "REQUEST_DENIED" -> REQUEST_DENIED_ERROR;
            case "OVER_QUERY_LIMIT" -> TOO_MANY_REQUEST_ERROR;
            case "UNKNOWN_ERROR" -> EXTERNAL_API_ERROR;
            default -> throw new IllegalArgumentException("Unexpected Client API status: " + status);
        };
    }
}
