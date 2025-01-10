package org.example.odiya.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.odiya.place.dto.response.MapSearchResponse;
import org.example.odiya.place.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Map API", description = "카카오맵 장소 관련 API")
@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @Operation(summary = "장소 전체 조회 API", description = "사용자가 키워드로 장소를 검색합니다.")
    @GetMapping("/places")
    public ResponseEntity<MapSearchResponse> searchLocation(@RequestParam String query) {
        return ResponseEntity.ok(placeService.searchByKeyword(query));
    }
}
