package org.example.odiya.route.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.route.config.RouteClientProperties;
import org.example.odiya.route.domain.ClientType;
import org.example.odiya.route.domain.RouteTime;
import org.example.odiya.route.dto.response.TmapDirectionResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Optional;

import static org.example.odiya.common.exception.type.ErrorType.INTERNAL_SERVER_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.REST_TEMPLATE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmapRouteClient implements RouteClient {

    private final RestTemplate restTemplate;
    private final RouteClientProperties properties;

    private static final String VERSION = "1";
    private static final String COORD_TYPE = "WGS84GEO";
    private static final String SEARCH_OPTION = "0";
    private static final String SORT = "index";

    private TmapDirectionResponse getDirectionsResponse(Coordinates origin, Coordinates target) {
        HttpHeaders headers = createHeaders();
        String url = buildDirectionsUrl(origin, target);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                TmapDirectionResponse.class
        ).getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("appKey", properties.getTmap().key());
        return headers;
    }

    private String buildDirectionsUrl(Coordinates origin, Coordinates target) {
        return UriComponentsBuilder.fromHttpUrl(properties.getTmap().url())
                .queryParam("version", VERSION)
                .queryParam("startX", origin.getLongitude())
                .queryParam("startY", origin.getLatitude())
                .queryParam("endX", target.getLongitude())
                .queryParam("endY", target.getLatitude())
                .queryParam("reqCoordType", COORD_TYPE)
                .queryParam("resCoordType", COORD_TYPE)
                .queryParam("searchOption", SEARCH_OPTION)
                .queryParam("sort", SORT)
                .build()
                .toUriString();
    }

    @Override
    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        try {
            TmapDirectionResponse response = getDirectionsResponse(origin, target);
            validateResponse(response);
            return convertToRouteTime(response);
        } catch (RestClientException e) {
            log.error("T Map Directions API 호출 실패", e);
            throw new InternalServerException(
                    REST_TEMPLATE_ERROR,
                    "T Map API 호출 중 오류가 발생했습니다." + e.getMessage()
            );
        } catch (Exception e) {
            log.error("T Map Directions API 호출 실패: {}", e.getMessage());
            throw new InternalServerException(
                    INTERNAL_SERVER_ERROR,
                    "경로 계산 중 오류가 발생했습니다."
            );
        }
    }

    private void validateResponse(TmapDirectionResponse response) {
        if (response == null) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, "T Map API 응답이 null 입니다.");
        }
        if (response.getFeatures() == null || response.getFeatures().isEmpty()) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, "경로를 찾을 수 없습니다.");
        }
    }

    private RouteTime convertToRouteTime(TmapDirectionResponse response) {
        Optional<TmapDirectionResponse.Feature> routeFeature = response.getFeatures().stream()
                .filter(feature -> feature.getProperties().getTotalTime() != null)
                .findFirst();

        if (routeFeature.isEmpty()) {
            return RouteTime.ZERO;
        }

        int totalSeconds = routeFeature.get().getProperties().getTotalTime();
        long durationMinutes = Duration.ofSeconds(totalSeconds).toMinutes();

        if (durationMinutes <= 1) {
            return RouteTime.CLOSEST_EXCEPTION_TIME;
        }

        return new RouteTime(durationMinutes);
    }

    @Override
    public ClientType getClientType() {
        return ClientType.TMAP;
    }
}
