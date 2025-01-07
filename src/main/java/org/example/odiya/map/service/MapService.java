package org.example.odiya.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;

import static org.example.odiya.common.exception.type.ErrorType.REST_CLIENT_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.SEARCH_RESULT_NOT_FOUND;

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
            headers.set("Authorization", "KakaoAK " + key);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // UriComponentsBuilder를 사용하여 URL 생성
            String url = UriComponentsBuilder.fromHttpUrl(host + searchPath)
                    .queryParam("query", query)  // UriComponentsBuilder가 자동으로 인코딩
                    .build()
                    .encode()
                    .toUriString();

            log.info("Request URL: {}", url);
            log.info("Request Headers: {}", headers);

            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.info("Response Status Code: {}", rawResponse.getStatusCode());
            log.info("Response Headers: {}", rawResponse.getHeaders());
            log.info("Raw API Response: {}", rawResponse.getBody());

            // ObjectMapper를 사용하여 직접 변환
            ObjectMapper mapper = new ObjectMapper();
            MapSearchResponse response = mapper.readValue(rawResponse.getBody(), MapSearchResponse.class);

            if (response.getDocuments().isEmpty()) {
                throw new NotFoundException(SEARCH_RESULT_NOT_FOUND);
            }

//            ResponseEntity<MapSearchResponse> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    entity,
//                    MapSearchResponse.class
//            );
//
//            if (response.getBody() == null || response.getBody().getDocuments().isEmpty()) {
//                throw new NotFoundException(SEARCH_RESULT_NOT_FOUND);
//            }

            return response;

        } catch (RestClientException e) {
            log.error("카카오 API 호출 중 오류 발생", e);
            throw new InternalServerException(REST_CLIENT_ERROR, e.getMessage());
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
