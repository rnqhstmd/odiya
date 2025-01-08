package org.example.odiya.meeting.dto.request;

import jakarta.validation.constraints.Future;
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

    @NotBlank(message = "약속 이름은 필수입니다")
    @Size(min = 1, max = 15, message = "약속 이름은 1글자 이상, 16자 미만으로 입력 가능합니다.")
    private String name;

    @NotBlank(message = "약속 날짜는 필수입니다")
    private LocalDate date;

    @NotBlank(message = "약속 시간은 필수입니다")
    private LocalTime time;

    private String placeId;  // 카카오 장소 ID
    private String placeName;
    private String address;
    private String longitude;
    private String latitude;
}
