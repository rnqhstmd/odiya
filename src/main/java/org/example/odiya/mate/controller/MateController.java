package org.example.odiya.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.odiya.common.annotation.AuthMember;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.mate.dto.response.MateJoinResponse;
import org.example.odiya.mate.service.MateService;
import org.example.odiya.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mate API", description = "Mate 관련 API")
@RestController
@RequestMapping("/api/mates")
@RequiredArgsConstructor
public class MateController {

    private final MateService mateService;

    @Operation(summary = "약속 참여 API", description = "사용자가 약속에 참여합니다.")
    @PostMapping("/join")
    public ResponseEntity<MateJoinResponse> joinMeeting(@AuthMember Member member,
                                                        @Valid @RequestBody MateJoinRequest request) {
        MateJoinResponse mateJoinResponse = mateService.joinMeeting(member, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mateJoinResponse);
    }
}
