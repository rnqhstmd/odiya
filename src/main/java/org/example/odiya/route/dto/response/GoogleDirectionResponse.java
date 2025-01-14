package org.example.odiya.route.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GoogleDirectionResponse {

    private List<Route> routes;
    private String status;
    private List<GeocodedWaypoint> geocodedWaypoints;

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Route {
        private Bounds bounds;
        private String copyrights;
        private List<Leg> legs;
        private OverviewPolyline overviewPolyline;
        private String summary;
        private List<String> warnings;
        private List<Integer> waypointOrder;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Leg {
        private TextValue distance;
        private TextValue duration;
        private String endAddress;
        private Location endLocation;
        private String startAddress;
        private Location startLocation;
        private List<Step> steps;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Step {
        private TextValue distance;
        private TextValue duration;
        private Location endLocation;
        private String htmlInstructions;
        private Polyline polyline;
        private Location startLocation;
        private String travelMode;
        private TransitDetails transitDetails;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class GeocodedWaypoint {
        private String geocoderStatus;
        private String placeId;
        private List<String> types;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Bounds {
        private Location northeast;
        private Location southwest;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Location {
        private double lat;
        private double lng;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class TextValue {
        private String text;
        private Long value;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class OverviewPolyline {
        private String points;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Polyline {
        private String points;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class TransitDetails {
        private Line line;
        private Stop departureStop;
        private Stop arrivalStop;
        private TextValue departureTime;
        private TextValue arrivalTime;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Line {
        private String name;
        private String shortName;
        private String vehicleType;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Stop {
        private String name;
        private Location location;
    }
}
