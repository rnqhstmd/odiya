package org.example.odiya.route.service;

import org.example.odiya.apicall.service.ApiClient;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.route.domain.RouteInfo;

public interface RouteClient extends ApiClient {

    RouteInfo calculateRouteTime(Coordinates origin, Coordinates target);
}
