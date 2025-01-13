package org.example.odiya.mate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MateJoinRequest {

    @Schema(description = "초대 코드", example = "123456")
    @NotNull(message = "초대 코드는 필수입니다")
    @Size(min = 6, max = 6, message = "초대 코드는 6자리여야 합니다")
    private String inviteCode;

    @Schema(description = "출발지 주소", example = "서울 강남구 테헤란로 411")
    @NotNull(message = "출발지 주소값은 필수입니다.")
    private String originAddress;

    @Schema(description = "출발지 위도", example = "37.505713")
    @NotNull(message = "출발지 위도값은 필수입니다.")
    private String originLatitude;

    @Schema(description = "출발지 경도", example = "127.050691")
    @NotNull(message = "출발지 경도값은 필수입니다.")
    private String originLongitude;

    public Coordinates toCoordinates() {
        return new Coordinates(originLatitude, originLongitude);
    }

    public Mate toMate(Meeting meeting, Member member, long estimatedMinutes) {
        Location origin = new Location(originAddress, originLatitude, originLongitude);
        return new Mate(member, meeting, origin, estimatedMinutes);
    }
}
