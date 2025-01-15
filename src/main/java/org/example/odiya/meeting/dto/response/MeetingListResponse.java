package org.example.odiya.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.meeting.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingListResponse {

    @Schema(description = "약속 리스트")
    List<MeetingList> meetings;

    public static MeetingListResponse of(List<Meeting> meetings, int mateCount) {
        List<MeetingList> responseList = meetings.stream()
                .map(meeting -> new MeetingList(
                        meeting,
                        mateCount))
                .toList();

        return new MeetingListResponse(responseList);
    }

    @Getter
    @NoArgsConstructor
    public static class MeetingList {

        @Schema(description = "약속 아이디", example = "1")
        private Long id;

        @Schema(description = "약속 이름", example = "동창 모임")
        private String name;

        @Schema(description = "약속 날짜", type = "string", example = "2024-09-10")
        private LocalDate date;

        @Schema(description = "약속 시간", type = "string", example = "14:00")
        private LocalTime time;

        @Schema(description = "참가자 수", example = "3")
        private int mateCount;

        public MeetingList(Meeting meeting, int mateCount) {
            this.id = meeting.getId();
            this.name = meeting.getName();
            this.date = meeting.getDate();
            this.time = meeting.getTime();
            this.mateCount = mateCount;
        }
    }
}
