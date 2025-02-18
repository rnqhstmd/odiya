package org.example.odiya.apicall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.odiya.apicall.dto.response.ApiCallCountResponse;
import org.example.odiya.apicall.service.ApiCallService;
import org.example.odiya.apicall.util.ClientTypeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ApiCall API", description = "외부 API 호출 관련 API")
@RestController
@RequestMapping("/admin/api-call")
@RequiredArgsConstructor
public class ApiCallController {

    private final ApiCallService apiCallService;

    @Operation(summary = "API 호출 횟수 조회 API", description = "특정 클라이언트의 API 호출 횟수를 조회합니다.")
    @GetMapping("/count/{clientName}")
    public ResponseEntity<ApiCallCountResponse> countApiCall(@PathVariable String clientName) {
        ApiCallCountResponse apiCallCountResponse = apiCallService.returnApiCountResponse(ClientTypeMapper.from(clientName));
        return ResponseEntity.ok(apiCallCountResponse);
    }
}
