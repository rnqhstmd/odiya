package org.example.odiya.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.map.dto.response.MapSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.example.odiya.common.exception.type.ErrorType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapService {

    @Value("${kakao.api.key}")
    private String key;

    @Value("${kakao.api.host}")
    private String host;

    @Value("${kakao.api.keyword-search-path}")
    private String searchPath;

    private final RestTemplate restTemplate;

    public MapSearchResponse searchByKeyword(String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "KakaoAK " + key);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI uri = UriComponentsBuilder
                    .fromUriString(host)
                    .path(searchPath)
                    .queryParam("query", query)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            ResponseEntity<MapSearchResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    MapSearchResponse.class
            );

            if (response.getBody() == null || response.getBody().getDocuments().isEmpty()) {
                throw new NotFoundException(SEARCH_RESULT_NOT_FOUND_ERROR);
            }
            return response.getBody();

        } catch (RestClientException e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new InternalServerException(REST_CLIENT_ERROR, e.getMessage());
        }
    }

    public MapSearchResponse.Document findPlaceById(String placeId) {
        MapSearchResponse response = searchByKeyword(placeId);
        return response.getDocuments().stream()
                .filter(doc -> doc.getId().equals(placeId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(PLACE_NOT_FOUND_ERROR));
    }
}
