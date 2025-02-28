package org.example.odiya.apicall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.domain.ClientType;
import org.example.odiya.apicall.dto.request.ClientStatusUpdateRequest;
import org.example.odiya.apicall.dto.response.ApiCallCountResponse;
import org.example.odiya.apicall.dto.response.ClientStatusResponse;
import org.example.odiya.apicall.dto.response.ClientStatusResponses;
import org.example.odiya.apicall.service.ApiCallService;
import org.example.odiya.apicall.util.ClientTypeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ApiCall API", description = "외부 API 호출 관련 API")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping("/admin/api/api-call")
@RequiredArgsConstructor
public class ApiCallController {

    private final ApiCallService apiCallService;

    @Operation(summary = "API 호출 횟수 조회 API", description = "특정 클라이언트의 API 호출 횟수를 조회합니다.")
    @GetMapping("/count/{clientName}")
    public ResponseEntity<ApiCallCountResponse> countApiCall(@PathVariable String clientName) {
        ApiCallCountResponse apiCallCountResponse = apiCallService.returnApiCountResponse(ClientTypeMapper.from(clientName));
        return ResponseEntity.ok(apiCallCountResponse);
    }

    @Operation(summary = "API 클라이언트 상태 조회 API", description = "특정 클라이언트의 활성화/비활성화 상태와 API 호출 횟수를 조회합니다.")
    @GetMapping("/status/{clientName}")
    public ResponseEntity<ClientStatusResponse> getClientStatus(@PathVariable String clientName) {
        ClientType clientType = ClientTypeMapper.from(clientName);
        ApiCall apiCall = apiCallService.findOrSaveTodayApiCallByClientType(clientType);

        ClientStatusResponse response = new ClientStatusResponse(
                clientType.name(),
                apiCall.getCount(),
                apiCall.getEnabled()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 API 클라이언트 상태 조회 API", description = "모든 클라이언트의 활성화/비활성화 상태와 API 호출 횟수를 조회합니다.")
    @GetMapping("/status")
    public ResponseEntity<ClientStatusResponses> getAllClientStatus() {
        ClientStatusResponses responses = apiCallService.findAllClientStatuses();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "API 클라이언트 상태 변경 API", description = "특정 클라이언트의 활성화/비활성화 상태를 변경합니다.")
    @PatchMapping("/status/{clientName}")
    public ResponseEntity<ClientStatusResponse> updateClientStatus(@PathVariable String clientName,
                                                                   @RequestBody @Valid ClientStatusUpdateRequest request) {
        ClientType clientType = ClientTypeMapper.from(clientName);
        ClientStatusResponse response = apiCallService.updateClientStatus(clientType, request.enabled());
        return ResponseEntity.ok(response);
    }
}
