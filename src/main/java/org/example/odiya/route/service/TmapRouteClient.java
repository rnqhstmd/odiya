package org.example.odiya.route.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.route.config.RouteClientProperties;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.route.domain.RouteInfo;
import org.example.odiya.route.dto.response.TmapDirectionResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.example.odiya.common.exception.type.ErrorType.INTERNAL_SERVER_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.REST_TEMPLATE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmapRouteClient implements RouteClient {

    private final RestTemplate restTemplate;
    private final RouteClientProperties properties;

    private static final String COORD_TYPE = "WGS84GEO";
    private static final String SEARCH_OPTION = "0";
    private static final String SORT = "index";

    private TmapDirectionResponse getDirectionsResponse(Coordinates origin, Coordinates target) {
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = UriComponentsBuilder.fromHttpUrl(properties.getTmap().url())
                .queryParam("version", "1")
                .queryParam("callback", "function")
                .build()
                .toUriString();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("startX", origin.getLongitude());
        requestBody.put("startY", origin.getLatitude());
        requestBody.put("endX", target.getLongitude());
        requestBody.put("endY", target.getLatitude());
        requestBody.put("reqCoordType", COORD_TYPE);
        requestBody.put("resCoordType", COORD_TYPE);
        requestBody.put("startName", URLEncoder.encode("출발", StandardCharsets.UTF_8));
        requestBody.put("endName", URLEncoder.encode("도착", StandardCharsets.UTF_8));
        requestBody.put("searchOption", SEARCH_OPTION);
        requestBody.put("sort", SORT);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                TmapDirectionResponse.class
        ).getBody();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("appKey", properties.getTmap().key());
        return headers;
    }

    @Override
    public RouteInfo calculateRouteTime(Coordinates origin, Coordinates target) {
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

    private RouteInfo convertToRouteTime(TmapDirectionResponse response) {
        Optional<TmapDirectionResponse.Feature> routeFeature = response.getFeatures().stream()
                .filter(feature -> feature.getProperties().getTotalTime() != null)
                .findFirst();

        if (routeFeature.isEmpty()) {
            return RouteInfo.ZERO;
        }

        int totalSeconds = routeFeature.get().getProperties().getTotalTime();
        long durationMinutes = Duration.ofSeconds(totalSeconds).toMinutes();
        long distance = routeFeature.get().getProperties().getTotalDistance();

        if (durationMinutes <= 1) {
            return RouteInfo.CLOSEST_EXCEPTION_TIME;
        }

        return new RouteInfo(durationMinutes, distance);
    }

    @Override
    public ClientType getClientType() {
        return ClientType.TMAP;
    }
}
