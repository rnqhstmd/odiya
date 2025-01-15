package org.example.odiya.meeting.dto.response;

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

    private Long id;
    private String name;
    private LocalDate date;
    private LocalTime time;
    private String address;
    private List<MateDetailResponse> mates;
    private String inviteCode;
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
