package org.example.odiya.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.odiya.map.dto.response.MapSearchResponse;
import org.example.odiya.map.service.MapService;
import org.example.odiya.map.dto.response.PlaceSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Map API", description = "카카오맵 장소 관련 API")
@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @Operation(summary = "장소 전체 조회 API", description = "사용자가 키워드로 장소를 검색합니다.")
    @GetMapping("/places")
    public ResponseEntity<MapSearchResponse> searchLocation(@RequestParam String query) {
        return ResponseEntity.ok(mapService.searchByKeyword(query));
    }

    @Operation(summary = "장소 상세 조회 API", description = "사용자가 선택한 장소의 상세 정보를 조회합니다.")
    @GetMapping("/places/{placeId}")
    public ResponseEntity<PlaceSearchResponse.PlaceDto> getSelectedPlace(@PathVariable String placeId) {
        return ResponseEntity.ok(mapService.findPlaceById(placeId).toPlaceDto());
    }
}
