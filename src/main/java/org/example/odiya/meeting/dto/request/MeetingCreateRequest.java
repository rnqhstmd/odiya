package org.example.odiya.meeting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "약속 이름은 필수입니다")
    @Size(min = 1, max = 15, message = "약속 이름은 1글자 이상, 16자 미만으로 입력 가능합니다.")
    private String name;

    @Schema(description = "약속 날짜", example = "2023-12-25")
    @NotBlank(message = "약속 날짜는 필수입니다")
    private LocalDate date;

    @Schema(description = "약속 시간", example = "18:00")
    @NotBlank(message = "약속 시간은 필수입니다")
    private LocalTime time;

    @Schema(description = "장소 ID", example = "1234567890")
    private String placeId;

    @Schema(description = "장소 이름", example = "강남카페")
    private String placeName;

    @Schema(description = "주소", example = "서울특별시 강남구")
    private String address;

    @Schema(description = "경도", example = "126.9780")
    private String longitude;

    @Schema(description = "위도", example = "37.5665")
    private String latitude;
}
