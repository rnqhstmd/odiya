package org.example.odiya.route.service;

import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.route.domain.ClientType;
import org.example.odiya.route.domain.RouteTime;

public interface RouteClient {

    RouteTime calculateRouteTime(Coordinates origin, Coordinates target);

    ClientType getClientType();
}
