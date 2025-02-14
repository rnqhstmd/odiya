package org.example.odiya.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.dto.response.EtaUpdateResult;
import org.example.odiya.eta.service.EtaQueryService;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.mate.service.MateQueryService;
import org.example.odiya.mate.service.MateService;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.dto.request.MeetingCreateRequest;
import org.example.odiya.meeting.dto.response.MeetingCreateResponse;
import org.example.odiya.meeting.dto.response.MeetingDetailResponse;
import org.example.odiya.meeting.dto.response.MeetingListResponse;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.Member;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.example.odiya.common.exception.type.ErrorType.MEETING_OVERDUE_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.NOT_ONE_HOUR_BEFORE_MEETING_ERROR;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingQueryService meetingQueryService;
    private final MateQueryService mateQueryService;
    private final EtaQueryService etaQueryService;
    private final MateService mateService;
    private final EtaService etaService;

    public MeetingCreateResponse createMeeting(Member member, MeetingCreateRequest request) {
        Location targetLocation = new Location(
                request.getTargetAddress(),
                new Coordinates(request.getTargetLatitude(), request.getTargetLongitude())
        );

        Meeting newMeeting = Meeting.builder()
                .name(request.getName())
                .date(request.getDate())
                .time(request.getTime())
                .target(targetLocation)
                .build();
        newMeeting.generateInviteCode();

        Meeting savedMeeting = meetingRepository.save(newMeeting);

        // 약속 생성자의 Mate 생성
        mateService.createAndSaveMate(
                member,
                savedMeeting,
                request.getOriginAddress(),
                request.getOriginLatitude(),
                request.getOriginLongitude()
        );

        return MeetingCreateResponse.from(savedMeeting);
    }

    public MeetingListResponse getMyMeetingList(Member member) {
        int mateCount = mateQueryService.countByMeetingId(member.getId());
        List<Meeting> meetingList = meetingQueryService.findOverdueMeetings(member.getId());
        return MeetingListResponse.of(meetingList, mateCount);
    }

    public MeetingDetailResponse getMeetingDetail(Member member, Long meetingId) {
        Meeting meeting = meetingQueryService.findById(meetingId);
        mateQueryService.validateMateExists(member.getId(), meetingId);
        return MeetingDetailResponse.from(meeting);
    }

    public void updateEtaForMeetingMates(Long meetingId, Long memberId) {
        Meeting meeting = meetingQueryService.findById(meetingId);
        mateQueryService.validateMateExists(memberId, meetingId);
        validateMeetingStatus(meeting);

        List<Eta> etas = etaQueryService.findAllByMeetingIdWithMate(meetingId);
        // 업데이트가 필요한 Eta들만 필터링
        List<Eta> etasToUpdate = etaService.filterUpdatableEtas(etas);

        // 비동기로 경로 계산 및 상태 업데이트
        updateEtasAsync(etasToUpdate, meeting);
    }

    @Async
    protected void updateEtasAsync(List<Eta> etas, Meeting meeting) {
        List<CompletableFuture<EtaUpdateResult>> futures = etas.stream()
                .map(eta -> etaService.calculateEtaAsync(eta, meeting))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> etaService.saveEtaUpdates(futures));
    }

    private void validateMeetingStatus(Meeting meeting) {
        if (meeting.isOverdue()) {
            throw new BadRequestException(MEETING_OVERDUE_ERROR);
        }

        if (meeting.isBeforeOneHourMeetingTime()) {
            throw new BadRequestException(NOT_ONE_HOUR_BEFORE_MEETING_ERROR);
        }
    }
}
