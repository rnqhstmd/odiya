package org.example.odiya.mate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HurryUpRequest {

    @Schema(description = "재촉한 사람", example = "1")
    private long senderId;

    @Schema(description = "재촉 당한 사람", example = "2")
    private long receiverId;
}
