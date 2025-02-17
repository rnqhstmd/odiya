package org.example.odiya.apicall.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.dto.response.ApiCallCountResponse;
import org.example.odiya.apicall.repository.ApiCallRepository;
import org.example.odiya.common.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    public void increaseCountByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveTodayApiCallByClientType(clientType);
        apiCall.increaseCount();
    }

    public ApiCall findOrSaveTodayApiCallByClientType(ClientType clientType) {
        LocalDate now = LocalDate.now();
        return apiCallRepository.findByClientTypeAndDate(clientType, now)
                .orElseGet(() -> {
                    log.error("date : {}, clientType : {} apiCall을 찾을 수 없습니다.", now, clientType);
                    return saveApiCall(new ApiCall(clientType, 0, now));
                });
    }

    public void validateApiCallAvailable(ClientType clientType) {
        ApiCall apiCall = findOrSaveTodayApiCallByClientType(clientType);
        int totalCount = countTotalApiCount(clientType);
        if (totalCount >= clientType.getDailyLimit()) {
            apiCall.markAsDisabled();
            log.error("API 호출 한도 초과 - clientType: {}, 현재 호출 수: {}, 일일 제한: {}",
                    clientType, totalCount, clientType.getDailyLimit());
            throw new BadRequestException(TOO_MANY_REQUEST_ERROR,
                    String.format("일일 호출 한도를 초과한 API : %s", clientType.name()));
        }
    }

    public boolean getEnabledByClientType(ClientType clientType) {
        ApiCall apiCall = findOrSaveTodayApiCallByClientType(clientType);
        return apiCall.getEnabled();
    }
}
