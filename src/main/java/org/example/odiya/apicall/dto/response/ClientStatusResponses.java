package org.example.odiya.apicall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.odiya.apicall.domain.ApiCall;
import java.util.List;

public record ClientStatusResponses(

        @Schema(description = "ClientStatusResponse를 담은 리스트")
        List<ClientStatusResponse> clientStatuses
) {
    public static ClientStatusResponses from(List<ApiCall> apiCalls) {
        List<ClientStatusResponse> responses = apiCalls.stream()
                .map(ClientStatusResponse::from)
                .toList();

        return new ClientStatusResponses(responses);
    }
}
