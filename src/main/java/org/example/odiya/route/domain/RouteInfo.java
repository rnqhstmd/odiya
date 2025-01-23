package org.example.odiya.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class RouteInfo {

    public static final RouteInfo CLOSEST_EXCEPTION_TIME = new RouteInfo(-1L, 0L);
    public static final RouteInfo ZERO = new RouteInfo(0L, 0L);

    private final long minutes;
    private final long distance;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteInfo routeInfo = (RouteInfo) o;
        return getMinutes() == routeInfo.getMinutes() && getDistance() == routeInfo.getDistance();
    }

    @Override
    public int hashCode() {
        return Objects.hash(minutes, distance);
    }
}
