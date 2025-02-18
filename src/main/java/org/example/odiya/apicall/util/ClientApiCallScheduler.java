package org.example.odiya.apicall.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.service.ApiCallService;
import org.example.odiya.apicall.service.ApiClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientApiCallScheduler {

    private final List<ApiClient> apiClients;
    private final ApiCallService apiCallService;

    @Scheduled(cron = "0 59 23 * * *", zone = "Asia/Seoul")
    public void initializeClientApiCalls() {
        LocalDate nextDay = LocalDate.now().plusDays(1);
        apiClients.stream()
                .map(client -> apiCallService.findOrSaveTodayApiCallByClientType(client.getClientType()))
                .map(apiCall -> new ApiCall(apiCall.getClientType(), 0, nextDay))
                .forEach(apiCallService::saveApiCall);
    }
}
