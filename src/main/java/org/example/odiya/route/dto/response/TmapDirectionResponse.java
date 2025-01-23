package org.example.odiya.route.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class TmapDirectionResponse {

    private String type;
    private List<Feature> features;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Feature {
        private String type;
        private Geometry geometry;
        private Properties properties;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Geometry {
        private String type;
        private List<Double> coordinates;
        private List<List<Double>> coordinates2D;

        @JsonProperty("coordinates")
        public void setCoordinates(Object coordinates) {
            if (coordinates instanceof List<?>) {
                if (((List<?>) coordinates).get(0) instanceof Double) {
                    this.coordinates = (List<Double>) coordinates;
                } else if (((List<?>) coordinates).get(0) instanceof List) {
                    this.coordinates2D = (List<List<Double>>) coordinates;
                }
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Properties {
        private Integer totalDistance;
        private Integer totalTime;
        private Integer index;
        private Integer pointIndex;
        private String name;
        private String description;
        private String direction;
        private String nearPoiName;
        private String nearPoiX;
        private String nearPoiY;
        private String intersectionName;
        private String facilityType;
        private String facilityName;
        private Integer turnType;
        private String pointType;
        private Integer lineIndex;
        private Integer distance;
        private Integer time;
        private Integer roadType;
        private Integer categoryRoadType;
    }
}
