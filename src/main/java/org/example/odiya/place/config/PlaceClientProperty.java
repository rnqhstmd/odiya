package org.example.odiya.place.config;

import java.util.Map;

public record PlaceClientProperty(String key, String host, Map<String, String> paths) {
}
