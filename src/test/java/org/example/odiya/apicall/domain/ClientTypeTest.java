package org.example.odiya.apicall.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTypeTest {

    @Test
    @DisplayName("모든 ClientType은 해당 월의 1일을 리셋 날짜로 반환한다")
    void determineResetDate() {
        // given
        LocalDate date = LocalDate.of(2025, 2, 15);
        LocalDate expectedResetDate = LocalDate.of(2025, 2, 1);

        // when & then
        Arrays.stream(ClientType.values())
                .forEach(clientType -> {
                    LocalDate resetDate = clientType.determineResetDate(date);
                    assertThat(resetDate)
                            .as("ClientType %s의 리셋 날짜는 월의 첫날이어야 합니다", clientType)
                            .isEqualTo(expectedResetDate);
                });
    }
}