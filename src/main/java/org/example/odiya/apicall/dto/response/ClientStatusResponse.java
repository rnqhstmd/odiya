package org.example.odiya.apicall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.odiya.apicall.domain.ApiCall;

public record ClientStatusResponse(

        @Schema(description = "클라이언트 이름", type = "string", example = "google")
        String clientName,

        @Schema(description = "클라이언트 API 호출 횟수", type = "string", example = "20")
        int apiCallCount,

        @Schema(description = "클라이언트 API 사용 가능 여부", type = "string", example = "true")
        boolean isAvailable
) {
    public static ClientStatusResponse from(ApiCall apiCall) {
        return new ClientStatusResponse(
                apiCall.getClientType().name(),
                apiCall.getCount(),
                apiCall.getEnabled()
        );
    }
}
