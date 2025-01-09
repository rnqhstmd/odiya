package org.example.odiya.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.odiya.common.annotation.AuthMember;
import org.example.odiya.meeting.dto.request.MeetingCreateRequest;
import org.example.odiya.meeting.dto.response.MeetingResponse;
import org.example.odiya.meeting.service.MeetingService;
import org.example.odiya.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Meeting API", description = "약속 관련 API")
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "약속 생성 API", description = "사용자가 약속을 생성합니다.")
    @PostMapping
    public ResponseEntity<MeetingResponse> createMeeting(@AuthMember Member member,
                                                         @Valid @RequestBody MeetingCreateRequest request) {
        MeetingResponse meetingResponse = meetingService.createMeeting(member, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingResponse);
    }
}
