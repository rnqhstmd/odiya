package org.example.odiya.apicall.repository;

import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.common.BaseTest.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ApiCallRepositoryTest extends BaseRepositoryTest {

    @BeforeEach
    void setUp() {
        apiCallRepository.deleteAll();
    }

    @Test
    @DisplayName("해당 기간 내의 모든 기록을 반환한다")
    void findAllByDateBetweenAndClientType() {
        // given
        LocalDate start = LocalDate.of(2025, 2, 1);
        LocalDate mid = LocalDate.of(2025, 2, 15);
        LocalDate end = LocalDate.of(2025, 2, 28);
        fixtureGenerator.generateApiCall(ClientType.KAKAO, start, 10);
        fixtureGenerator.generateApiCall(ClientType.KAKAO, mid, 20);
        fixtureGenerator.generateApiCall(ClientType.GOOGLE, mid, 30);
        fixtureGenerator.generateApiCall(ClientType.KAKAO, end, 40);

        // when
        List<ApiCall> result = apiCallRepository.findAllByDateBetweenAndClientType(
                start, mid, ClientType.KAKAO);

        // then
        assertThat(result)
                .hasSize(2)
                .extracting("clientType", "date", "count")
                .containsExactlyInAnyOrder(
                        tuple(ClientType.KAKAO, start, 10),
                        tuple(ClientType.KAKAO, mid, 20)
                );
    }

    @Test
    @DisplayName("일치하는 기록이 있으면 반환한다")
    void findByClientTypeAndDate() {
        // given
        LocalDate today = LocalDate.now();
        fixtureGenerator.generateApiCall(ClientType.KAKAO, today, 10);
        fixtureGenerator.generateApiCall(ClientType.KAKAO, today.plusDays(1), 20);
        fixtureGenerator.generateApiCall(ClientType.GOOGLE, today, 30);

        // when
        Optional<ApiCall> result = apiCallRepository.findByClientTypeAndDate(
                ClientType.KAKAO, today);

        // then
        assertThat(result)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getClientType()).isEqualTo(ClientType.KAKAO);
                    assertThat(found.getDate()).isEqualTo(today);
                    assertThat(found.getCount()).isEqualTo(10);
                });
    }
}