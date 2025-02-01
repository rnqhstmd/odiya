package org.example.odiya.meeting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.odiya.common.annotation.AuthMember;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.meeting.dto.request.MeetingCreateRequest;
import org.example.odiya.meeting.dto.response.MeetingCreateResponse;
import org.example.odiya.meeting.dto.response.MeetingDetailResponse;
import org.example.odiya.meeting.dto.response.MeetingListResponse;
import org.example.odiya.meeting.service.MeetingService;
import org.example.odiya.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Meeting API", description = "Meeting 관련 API")
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final EtaService etaService;

    @Operation(summary = "약속 생성 API", description = "사용자가 약속을 생성합니다.")
    @PostMapping
    public ResponseEntity<MeetingCreateResponse> createMeeting(@AuthMember Member member,
                                                               @Valid @RequestBody MeetingCreateRequest request) {
        MeetingCreateResponse meetingCreateResponse = meetingService.createMeeting(member, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meetingCreateResponse);
    }

    @Operation(summary = "약속 전체 조회 API", description = "사용자가 자신이 속한 약속리스트를 전체 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<MeetingListResponse> getMyMeetingList(@AuthMember Member member) {
        MeetingListResponse myMeetingList = meetingService.getMyMeetingList(member);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(myMeetingList);
    }

    @Operation(summary = "약속 상세 조회 API", description = "사용자가 자신이 속한 약속을 상세 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<MeetingDetailResponse> getMeetingDetail(@AuthMember Member member,
                                                                  @PathVariable("id") Long meetingId) {
        MeetingDetailResponse meetingDetail = meetingService.getMeetingDetail(member, meetingId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(meetingDetail);
    }

    @Operation(summary = "참여자 도착 예정 시간 업데이트 API", description = "해당 약속의 모든 참여자들의 도착 예정 시간을 업데이트합니다.")
    @PutMapping("/{id}/eta")
    public ResponseEntity<Void> updateMeetingEta(
            @AuthMember Member member,
            @PathVariable("id") Long meetingId) {
        etaService.updateEtaOfMate(meetingId, member.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
