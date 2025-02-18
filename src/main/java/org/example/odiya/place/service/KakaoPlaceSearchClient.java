package org.example.odiya.place.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.service.ApiCallService;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.place.config.PlaceClientProperties;
import org.example.odiya.place.dto.response.PlaceSearchResponse;
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
public class KakaoPlaceSearchClient implements PlaceSearchClient {

    private final ApiCallService apiCallService;
    private final PlaceClientProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public PlaceSearchResponse searchByKeyword(String query) {
        apiCallService.validateClientsAvailable();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "KakaoAK " + properties.getKakao().key());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI uri = UriComponentsBuilder
                    .fromUriString(properties.getKakao().host())
                    .path(properties.getKakao().paths().get("keyword-search"))
                    .queryParam("query", query)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            ResponseEntity<PlaceSearchResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    PlaceSearchResponse.class
            );

            apiCallService.recordApiCall(ClientType.KAKAO);

            if (response.getBody() == null || response.getBody().getDocuments().isEmpty()) {
                throw new NotFoundException(SEARCH_RESULT_NOT_FOUND_ERROR);
            }
            return response.getBody();

        } catch (RestClientException e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new InternalServerException(REST_TEMPLATE_ERROR, "카카오 API 응답이 null입니다. " + e.getMessage());
        }
    }

    @Override
    public ClientType getClientType() {
        return ClientType.KAKAO;
    }
}
