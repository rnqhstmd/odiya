package org.example.odiya.route.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.common.exception.type.ErrorType;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.route.config.RouteClientProperties;
import org.example.odiya.route.domain.ClientType;
import org.example.odiya.route.domain.RouteTime;
import org.example.odiya.route.dto.response.GoogleDirectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

import static org.example.odiya.common.constant.Constants.MODE_TRANSIT;
import static org.example.odiya.common.constant.Constants.STATUS_OK;
import static org.example.odiya.common.exception.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleRouteClient implements RouteClient {

    private final RestTemplate restTemplate;
    private final RouteClientProperties properties;

    private GoogleDirectionResponse getDirectionsResponse(Coordinates origin, Coordinates target) {
        String url = buildDirectionsUrl(origin, target);
        return restTemplate.getForObject(url, GoogleDirectionResponse.class);
    }

    private String buildDirectionsUrl(Coordinates origin, Coordinates target) {
        return UriComponentsBuilder.fromHttpUrl(properties.getGoogle().url())
                .queryParam("origin", formatCoordinate(origin))
                .queryParam("destination", formatCoordinate(target))
                .queryParam("mode", MODE_TRANSIT)
                .queryParam("key", properties.getGoogle().key())
                .build()
                .toUriString();
    }

    @Override
    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        try {
            GoogleDirectionResponse response = getDirectionsResponse(origin, target);
            validateResponse(response);
            checkWarnings(response);
            return convertToRouteTime(response);
        } catch (RestClientException e) {
            log.error("Google Directions API 호출 실패", e);
            throw new InternalServerException(
                    ErrorType.REST_TEMPLATE_ERROR,
                    "Google API 호출 중 오류가 발생했습니다." + e.getMessage()
            );
        } catch (Exception e) {
            log.error("Google Directions API 호출 실패: {}", e.getMessage());
            throw new InternalServerException(
                    ErrorType.INTERNAL_SERVER_ERROR,
                    "경로 계산 중 오류가 발생했습니다."
            );
        }
    }

    private static void validateResponse(GoogleDirectionResponse response) {
        if (response == null) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, "Google API 응답이 null 입니다.");
        }
        if (!STATUS_OK.equals(response.getStatus())) {
            ErrorType errorType = resolveClientApiStatus(response.getStatus());
            throw new InternalServerException(errorType, response.getStatus());
        }
    }

    private void checkWarnings(GoogleDirectionResponse response) {
        if (!response.getRoutes().isEmpty() &&
                response.getRoutes().get(0).getWarnings() !=
                        null) {
            response.getRoutes().get(0).getWarnings()
                    .forEach(warning -> log.warn("구글 경로 경고: {}", warning));
        }
    }

    private String formatCoordinate(Coordinates coordinates) {
        return String.format("%s,%s", coordinates.getLatitude(), coordinates.getLongitude());
    }

    private RouteTime convertToRouteTime(GoogleDirectionResponse response) {
        GoogleDirectionResponse.Route route = response.getRoutes().get(0);
        GoogleDirectionResponse.Leg leg = route.getLegs().get(0);

        long durationSeconds = leg.getDuration().getValue();
        long durationMinutes = Duration.ofSeconds(durationSeconds).toMinutes();

        if (durationMinutes <= 1) {
            return RouteTime.CLOSEST_EXCEPTION_TIME;
        }

        return new RouteTime(durationMinutes);
    }

    @Override
    public ClientType getClientType() {
        return ClientType.GOOGLE;
    }
}
