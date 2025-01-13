package org.example.odiya.mate.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.meeting.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MateJoinResponse {

    @Schema(description = "약속 ID", example = "1")
    private Long id;

    @Schema(description = "약속 날짜", type = "string", example = "2024-09-15")
    private LocalDate date;

    @Schema(description = "약속 시간", type = "string", example = "14:00")
    private LocalTime time;

    public static MateJoinResponse from(Meeting meeting) {
        return new MateJoinResponse(meeting.getId(), meeting.getDate(), meeting.getTime());
    }
}
