package org.example.odiya.route.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.service.ApiCallService;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.route.domain.RouteInfo;
import org.springframework.stereotype.Service;

import static org.example.odiya.common.constant.Constants.WALKING_THRESHOLD;
import static org.example.odiya.common.exception.type.ErrorType.TOO_MANY_REQUEST_ERROR;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final GoogleRouteClient googleRouteClient;
    private final TmapRouteClient tmapRouteClient;
    private final ApiCallService apiCallService;

    public long calculateOptimalRoute(Coordinates origin, Coordinates target) {
        validateBothClientsAvailable();

        apiCallService.validateApiCallAvailable(ClientType.GOOGLE);
        RouteInfo transitInfo = googleRouteClient.calculateRouteTime(origin, target);
        apiCallService.increaseCountByClientType(ClientType.GOOGLE);

        if (transitInfo.getDistance() <= WALKING_THRESHOLD) {
            apiCallService.validateApiCallAvailable(ClientType.TMAP);
            RouteInfo walkingInfo = tmapRouteClient.calculateRouteTime(origin, target);
            apiCallService.increaseCountByClientType(ClientType.TMAP);

            return Math.min(transitInfo.getMinutes(), walkingInfo.getMinutes());
        }

        return transitInfo.getMinutes();
    }

    private void validateBothClientsAvailable() {
        boolean googleEnabled = apiCallService.getEnabledByClientType(ClientType.GOOGLE);
        boolean tmapEnabled = apiCallService.getEnabledByClientType(ClientType.TMAP);

        if (!googleEnabled || !tmapEnabled) {
            throw new BadRequestException(TOO_MANY_REQUEST_ERROR,
                    "일부 경로 계산 서비스가 사용 불가능한 상태입니다.");
        }
    }
}
