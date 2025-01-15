package org.example.odiya.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.mate.dto.response.MateDetailResponse;
import org.example.odiya.meeting.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDetailResponse {

    @Schema(description = "약속 아이디", example = "1")
    private Long id;

    @Schema(description = "약속 이름", example = "동창 모임")
    private String name;

    @Schema(description = "약속 날짜", type = "string", example = "2024-09-10")
    private LocalDate date;

    @Schema(description = "약속 시간", type = "string", example = "14:00")
    private LocalTime time;

    @Schema(description = "도착지 주소", example = "서울 테헤란로 411")
    private String address;

    @Schema(description = "참가자 목록")
    private List<MateDetailResponse> mates;

    @Schema(description = "초대코드", example = "123456")
    private String inviteCode;

    @Schema(description = "만료 여부", example = "false")
    private boolean overdue;

    public static MeetingDetailResponse from(Meeting meeting) {
        return new MeetingDetailResponse(
            meeting.getId(),
            meeting.getName(),
            meeting.getDate(),
            meeting.getTime(),
            meeting.getTarget().getAddress(),
            meeting.getMates().stream()
                .map(MateDetailResponse::new)
                .toList(),
            meeting.getInviteCode(),
            meeting.isOverdue()
        );
    }
}
