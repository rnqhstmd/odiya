package org.example.odiya.route.service;

import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.route.domain.RouteInfo;

public interface RouteClient {

    RouteInfo calculateRouteTime(Coordinates origin, Coordinates target);

    ClientType getClientType();
}
