package org.example.odiya.meeting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.odiya.meeting.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class MeetingResponse {

    private Long id;
    private String name;
    private LocalDate date;
    private LocalTime time;
    private String inviteCode;
    private LocationDto location;

    @Getter
    @AllArgsConstructor
    public static class LocationDto {
        private String address;
        private String latitude;
        private String longitude;
    }

    public static MeetingResponse from(Meeting meeting) {
        return new MeetingResponse(
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
