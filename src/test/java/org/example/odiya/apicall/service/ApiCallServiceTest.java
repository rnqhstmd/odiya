package org.example.odiya.apicall.service;

import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.repository.ApiCallRepository;
import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.common.exception.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiCallServiceTest extends BaseServiceTest {

    @Autowired
    private ApiCallService apiCallService;

    @Autowired
    private ApiCallRepository apiCallRepository;

    @BeforeEach
    void setUp() {
        apiCallRepository.deleteAll();
    }

    @Test
    @DisplayName("API 호출을 기록하면 당일 호출 횟수가 증가한다")
    void recordApiCall_Success() {
        // given
        ClientType clientType = ClientType.KAKAO;

        // when
        apiCallService.recordApiCall(clientType);
        apiCallService.recordApiCall(clientType);

        // then
        ApiCall apiCall = apiCallRepository.findByClientTypeAndDate(
                clientType, LocalDate.now()).orElseThrow();
        assertThat(apiCall.getCount()).isEqualTo(2);
        assertThat(apiCall.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("월간 호출 한도에 도달 시 API 호출이 비활성화된다")
    void recordApiCall_DisabledWhenLimitReached() {
        // given
        ClientType clientType = ClientType.KAKAO;
        LocalDate today = LocalDate.now();
        apiCallRepository.save(new ApiCall(
                clientType,
                99,
                today
        ));

        // when
        apiCallService.recordApiCall(clientType);

        // then
        ApiCall apiCall = apiCallRepository.findByClientTypeAndDate(clientType, today).orElseThrow();
        assertThat(apiCall.getCount()).isEqualTo(100);
        assertThat(apiCall.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("월간 호출 한도에 도달한 API 호출 시 예외가 발생한다")
    void validateRouteClients_ThrowsException_WhenLimitReached() {
        // given
        ClientType clientType = ClientType.KAKAO;
        LocalDate today = LocalDate.now();
        ApiCall apiCall = new ApiCall(clientType, 100, today);
        apiCall.markAsDisabled();
        apiCallRepository.save(apiCall);

        // when & then
        assertThrows(
                InternalServerException.class,
                () -> apiCallService.validateClientsAvailable()
        );
    }
}