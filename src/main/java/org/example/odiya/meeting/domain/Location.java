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

    private String address;

    @Embedded
    private Coordinates coordinates;

    public Location(String address, String latitude, String longitude) {
        this(address, new Coordinates(latitude, longitude));
    }

    public Location( String address, Coordinates coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }
}