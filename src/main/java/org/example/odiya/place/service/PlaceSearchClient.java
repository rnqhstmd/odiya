package org.example.odiya.place.service;

import org.example.odiya.apicall.service.ApiClient;
import org.example.odiya.place.dto.response.PlaceSearchResponse;

public interface PlaceSearchClient extends ApiClient {

    PlaceSearchResponse searchByKeyword(String query);
}
