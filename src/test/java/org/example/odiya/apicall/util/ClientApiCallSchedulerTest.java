package org.example.odiya.apicall.util;

import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.repository.ApiCallRepository;
import org.example.odiya.apicall.service.ApiClient;
import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.place.service.KakaoPlaceSearchClient;
import org.example.odiya.route.service.GoogleRouteClient;
import org.example.odiya.route.service.TmapRouteClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ClientApiCallSchedulerTest extends BaseServiceTest {

    @Autowired
    private ClientApiCallScheduler clientApiCallScheduler;

    @Autowired
    private ApiCallRepository apiCallRepository;

    @MockBean
    private GoogleRouteClient googleRouteClient;

    @MockBean
    private TmapRouteClient tmapRouteClient;

    @MockBean
    private KakaoPlaceSearchClient kakaoPlaceSearchClient;

    private List<ApiClient> clients;

    @BeforeEach
    void setUp() {
        apiCallRepository.deleteAll();

        when(googleRouteClient.getClientType()).thenReturn(ClientType.GOOGLE);
        when(tmapRouteClient.getClientType()).thenReturn(ClientType.TMAP);
        when(kakaoPlaceSearchClient.getClientType()).thenReturn(ClientType.KAKAO);
        clients = List.of(googleRouteClient, tmapRouteClient, kakaoPlaceSearchClient);
    }


    @Test
    @DisplayName("매일 23:59에 다음날의 API 호출 기록을 초기화한다")
    void initializeClientApiCalls() {
        // given
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        clients.forEach(client -> {
            ApiCall todayApiCall = new ApiCall(
                    client.getClientType(),
                    today,
                    50,
                    false
            );
            apiCallRepository.save(todayApiCall);
        });

        // when
        clientApiCallScheduler.initializeClientApiCalls();

        // then
        // 1. 내일 날짜의 모든 API 호출 기록이 초기화되었는지 확인
        clients.forEach(client -> {
            ApiCall tomorrowApiCall = apiCallRepository.findByClientTypeAndDate(
                    client.getClientType(),
                    tomorrow
            ).orElseThrow();

            assertThat(tomorrowApiCall)
                    .satisfies(apiCall -> {
                        assertThat(apiCall.getCount()).isZero();
                        assertThat(apiCall.getEnabled()).isTrue();
                        assertThat(apiCall.getDate()).isEqualTo(tomorrow);
                        assertThat(apiCall.getClientType()).isEqualTo(client.getClientType());
                    });
        });

        // 2. 오늘 날짜의 기록은 변경되지 않았는지 확인
        clients.forEach(client -> {
            ApiCall todayApiCall = apiCallRepository.findByClientTypeAndDate(
                    client.getClientType(),
                    today
            ).orElseThrow();

            assertThat(todayApiCall)
                    .satisfies(apiCall -> {
                        assertThat(apiCall.getCount()).isEqualTo(50);
                        assertThat(apiCall.getEnabled()).isFalse();
                        assertThat(apiCall.getDate()).isEqualTo(today);
                        assertThat(apiCall.getClientType()).isEqualTo(client.getClientType());
                    });
        });
    }

    @Test
    @DisplayName("모든 Client 타입에 대해 초기화가 수행된다")
    void initializeClientApiCalls_ForAllClients() {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // when
        clientApiCallScheduler.initializeClientApiCalls();

        // then
        List<ApiCall> tomorrowApiCalls = apiCallRepository.findAllByDate(tomorrow);

        assertThat(tomorrowApiCalls).hasSize(clients.size());

        clients.forEach(client -> {
            List<ApiCall> clientApiCalls = tomorrowApiCalls.stream()
                    .filter(apiCall -> apiCall.getClientType() == client.getClientType())
                    .toList();

            assertThat(clientApiCalls)
                    .hasSize(1)
                    .allSatisfy(apiCall -> {
                        assertThat(apiCall.getCount()).isZero();
                        assertThat(apiCall.getEnabled()).isTrue();
                        assertThat(apiCall.getDate()).isEqualTo(tomorrow);
                        assertThat(apiCall.getClientType()).isNotNull()
                                .isEqualTo(client.getClientType());
                    });
        });
    }
}