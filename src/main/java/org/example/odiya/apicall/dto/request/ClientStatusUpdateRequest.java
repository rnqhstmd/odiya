package org.example.odiya.apicall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ClientStatusUpdateRequest(

        @Schema(description = "클라이언트 사용 가능 여부 업데이트", example = "false")
        @NotNull
        boolean enabled
) {
}
