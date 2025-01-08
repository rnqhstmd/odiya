package org.example.odiya.meeting.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    private String placeName;
    private String address;

    @Embedded
    private Coordinates coordinates;

    public Location(String placeName, String address, String latitude, String longitude) {
        this(placeName, address, new Coordinates(latitude, longitude));
    }

    public Location(String placeName, String address, Coordinates coordinates) {
        this.placeName = placeName;
        this.address = address;
        this.coordinates = coordinates;
    }
}