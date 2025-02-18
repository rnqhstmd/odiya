package org.example.odiya.route.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.service.ApiCallService;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.route.domain.RouteInfo;
import org.springframework.stereotype.Service;

import static org.example.odiya.common.constant.Constants.WALKING_THRESHOLD;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final GoogleRouteClient googleRouteClient;
    private final TmapRouteClient tmapRouteClient;
    private final ApiCallService apiCallService;

    public long calculateOptimalRoute(Coordinates origin, Coordinates target) {
        apiCallService.validateClientsAvailable();

        RouteInfo transitInfo = googleRouteClient.calculateRouteTime(origin, target);
        apiCallService.recordApiCall(ClientType.GOOGLE);

        if (transitInfo.getDistance() <= WALKING_THRESHOLD) {
            RouteInfo walkingInfo = tmapRouteClient.calculateRouteTime(origin, target);
            apiCallService.recordApiCall(ClientType.TMAP);

            return Math.min(transitInfo.getMinutes(), walkingInfo.getMinutes());
        }

        return transitInfo.getMinutes();
    }
}
