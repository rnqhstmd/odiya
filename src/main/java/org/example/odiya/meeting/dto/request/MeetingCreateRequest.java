package org.example.odiya.meeting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.odiya.common.annotation.FutureOrPresentDateTime;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@FutureOrPresentDateTime(dateFieldName = "date", timeFieldName = "time")
public class MeetingCreateRequest {

    @Schema(description = "약속 이름", example = "친구 모임")
    @NotNull(message = "약속 이름은 필수입니다")
    @Size(min = 1, max = 15, message = "약속 이름은 1글자 이상, 16자 미만으로 입력 가능합니다.")
    private String name;

    @Schema(description = "약속 날짜", example = "2025-01-30")
    @NotNull(message = "약속 날짜는 필수입니다")
    private LocalDate date;

    @Schema(description = "약속 시간", example = "18:00")
    @NotNull(message = "약속 시간은 필수입니다")
    private LocalTime time;

    @Schema(description = "장소 ID", example = "23444676")
    private String placeId;

    @Schema(description = "장소 이름", example = "롯데월드타워")
    private String placeName;

    @Schema(description = "주소", example = "서울 송파구 신천동 29")
    private String address;

    @Schema(description = "경도", example = "127.10255558658325")
    private String longitude;

    @Schema(description = "위도", example = "37.51260447840551")
    private String latitude;
}
