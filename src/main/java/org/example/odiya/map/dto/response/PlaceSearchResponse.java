package org.example.odiya.map.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSearchResponse {
    private List<PlaceDto> documents;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceDto {
        private String id;
        private String placeName;
        private String addressName;
        private String roadAddressName;
        private String x;
        private String y;
    }
}
