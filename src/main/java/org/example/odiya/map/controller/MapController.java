package org.example.odiya.map.controller;

import lombok.RequiredArgsConstructor;
import org.example.odiya.map.dto.response.MapSearchResponse;
import org.example.odiya.map.service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("/search")
    public ResponseEntity<MapSearchResponse> searchLocation(@RequestParam String query) {
        return ResponseEntity.ok(mapService.searchByKeyword(query));
    }
}
