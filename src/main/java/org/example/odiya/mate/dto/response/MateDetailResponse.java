package org.example.odiya.mate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.mate.domain.Mate;

@Getter
@NoArgsConstructor
public class MateDetailResponse {

    @Schema(description = "참여자 ID", example = "1")
    private Long id;

    @Schema(description = "참여자 출발지주소", example = "서울 강남구 테헤란로 411")
    private String originAddress;

    public MateDetailResponse(Mate mate) {
        this.id = mate.getId();
        this.originAddress = mate.getOrigin().getAddress();
    }
}
