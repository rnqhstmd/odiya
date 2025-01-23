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

    @Schema(description = "참가자 이름", example = "구본승")
    private String name;

    @Schema(description = "참여자 출발지주소", example = "서울 송파구 송파대로 345")
    private String originAddress;

    @Schema(description = "예상 도착시간", example = "30")
    private Long remainingMinutes;

    public MateDetailResponse(Mate mate) {
        this.id = mate.getId();
        this.name = mate.getMember().getName();
        this.originAddress = mate.getOrigin().getAddress();
        this.remainingMinutes = mate.getEstimatedTime();
    }
}
