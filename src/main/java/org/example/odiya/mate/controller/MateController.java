package org.example.odiya.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.odiya.common.annotation.AuthMember;
import org.example.odiya.mate.dto.request.HurryUpRequest;
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

    @Operation(summary = "참여자 재촉 API", description = "참여자가 약속 참여자들을 재촉합니다.")
    @PostMapping("/hurry-up")
    public ResponseEntity<Void> hurryUp(@AuthMember Member member,
                                        @Valid @RequestBody HurryUpRequest request) {
        mateService.hurryUpMate(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}