package org.example.odiya.meeting.dto.response;

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
        private Long id;
        private String name;
        private LocalDate date;
        private LocalTime time;
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
