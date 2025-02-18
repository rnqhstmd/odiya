package org.example.odiya.apicall.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.dto.response.ApiCallCountResponse;
import org.example.odiya.apicall.repository.ApiCallRepository;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.common.exception.InternalServerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.example.odiya.common.exception.type.ErrorType.API_CALL_DISABLED_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.TOO_MANY_REQUEST_ERROR;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApiCallService {

    private final ApiCallRepository apiCallRepository;

    @Transactional
    public ApiCall saveApiCall(ApiCall apiCall) {
        return apiCallRepository.save(apiCall);
    }

    public ApiCallCountResponse returnApiCountResponse(ClientType clientType) {
        int totalCount = countTotalApiCount(clientType);
        return new ApiCallCountResponse(totalCount);
    }

    private int countTotalApiCount(ClientType clientType) {
        LocalDate end = LocalDate.now();
        LocalDate start = clientType.determineResetDate(end);
        List<ApiCall> apiCalls = apiCallRepository.findAllByDateBetweenAndClientType(start, end, clientType);

        return apiCalls.stream()
                .mapToInt(ApiCall::getCount)
                .sum();
    }

    @Transactional
    public void recordApiCall(ClientType clientType) {
        ApiCall apiCall = findOrSaveTodayApiCallByClientType(clientType);
        apiCall.increaseCount();

        int totalCount = countTotalApiCount(clientType);
        if (totalCount >= clientType.getMonthlyLimit()) {
            apiCall.markAsDisabled();
        }
    }

    public ApiCall findOrSaveTodayApiCallByClientType(ClientType clientType) {
        LocalDate now = LocalDate.now();
        return apiCallRepository.findByClientTypeAndDate(clientType, now)
                .orElseGet(() -> {
                    log.error("date : {}, clientType : {} apiCall을 찾을 수 없습니다.", now, clientType);
                    return saveApiCall(new ApiCall(clientType, 0, now));
                });
    }

    public void validateClientsAvailable() {
        Arrays.stream(ClientType.values())
                .filter(clientType ->
                                clientType == ClientType.GOOGLE ||
                                clientType == ClientType.TMAP ||
                                clientType == ClientType.KAKAO)
                .forEach(clientType -> {
                    ApiCall apiCall = findOrSaveTodayApiCallByClientType(clientType);

                    if (Boolean.FALSE.equals(apiCall.getEnabled())) {
                        throw new InternalServerException(API_CALL_DISABLED_ERROR,
                                String.format("%s API가 비활성화 상태입니다.", clientType.name()));
                    }

                    int totalCount = countTotalApiCount(clientType);
                    if (totalCount >= clientType.getMonthlyLimit()) {
                        apiCall.markAsDisabled();
                        throw new BadRequestException(TOO_MANY_REQUEST_ERROR,
                                String.format("%s API 호출 한도를 초과했습니다.", clientType.name()));
                    }
                });
    }
}
