package org.example.odiya.eta.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EtaRequest {

    @Schema(description = "위치추적 불가 여부", example = "false")
    @NotNull
    private boolean isMissing;

    @Schema(description = "현재 위도", example = "39.123345")
    @NotNull
    private String currentLatitude;

    @Schema(description = "현재 경도", example = "126.234524")
    @NotNull
    private String currentLongitude;
}
