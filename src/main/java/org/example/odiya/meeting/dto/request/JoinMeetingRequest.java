package org.example.odiya.meeting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinMeetingRequest {

    @Schema(description = "초대 코드", example = "123456")
    @NotNull(message = "초대 코드는 필수입니다")
    @Size(min = 6, max = 6, message = "초대 코드는 6자리여야 합니다")
    private String inviteCode;
}
