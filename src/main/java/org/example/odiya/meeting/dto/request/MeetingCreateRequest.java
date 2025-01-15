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

    @Schema(description = "목적지 ID", example = "23444676")
    @NotNull(message = "목적지 ID는 필수입니다")
    private String placeId;

    @Schema(description = "목적지 이름", example = "롯데월드타워")
    @NotNull(message = "목적지 이름은 필수입니다")
    private String placeName;

    @Schema(description = "목적지 주소", example = "서울 송파구 신천동 29")
    @NotNull(message = "목적지 주소는 필수입니다")
    private String targetAddress;

    @Schema(description = "경도", example = "127.10255558658325")
    @NotNull(message = "목적지 경도는 필수입니다")
    private String targetLongitude;

    @Schema(description = "위도", example = "37.51260447840551")
    @NotNull(message = "목적지 위도는 필수입니다")
    private String targetLatitude;

    @Schema(description = "출발지 주소", example = "서울 강남구 테헤란로 411")
    @NotNull(message = "출발지 주소는 필수입니다")
    private String originAddress;

    @Schema(description = "출발지 위도", example = "37.505713")
    @NotNull(message = "출발지 경도는 필수입니다")
    private String originLatitude;

    @Schema(description = "출발지 경도", example = "127.050691")
    @NotNull(message = "출발지 경도는 필수입니다")
    private String originLongitude;
}
