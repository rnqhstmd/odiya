package org.example.odiya.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.odiya.meeting.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class MeetingCreateResponse {

    @Schema(description = "약속 ID", example = "1")
    private Long id;

    @Schema(description = "약속 이름", example = "동창 모임")
    private String name;

    @Schema(description = "모임 날짜", type = "string", example = "2024-07-15")
    private LocalDate date;

    @Schema(description = "모임 시간", type = "string", example = "14:00")
    private LocalTime time;

    @Schema(description = "초대코드", example = "123456")
    private String inviteCode;

    @Schema(description = "약속 장소 정보")
    private LocationDto location;

    @Getter
    @AllArgsConstructor
    public static class LocationDto {

        @Schema(description = "도착지 주소", example = "서울 송파구 올림픽로35다길 42")
        private String address;

        @Schema(description = "도착지 위도", example = "37.515298")
        private String latitude;

        @Schema(description = "도착지 경도", example = "127.103113")
        private String longitude;
    }

    public static MeetingCreateResponse from(Meeting meeting) {
        return new MeetingCreateResponse(
                meeting.getId(),
                meeting.getName(),
                meeting.getDate(),
                meeting.getTime(),
                meeting.getInviteCode(),
                new LocationDto(
                        meeting.getTarget().getAddress(),
                        meeting.getTarget().getCoordinates().getLatitude(),
                        meeting.getTarget().getCoordinates().getLongitude()
                )
        );
    }
}
