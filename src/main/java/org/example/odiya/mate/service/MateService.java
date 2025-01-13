package org.example.odiya.mate.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.mate.dto.response.MateJoinResponse;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.example.odiya.member.domain.Member;
import org.example.odiya.route.domain.RouteTime;
import org.example.odiya.route.service.GoogleRouteClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.exception.type.ErrorType.MEETING_OVERDUE_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class MateService {

    private final MateRepository mateRepository;
    private final MateQueryService mateQueryService;
    private final MeetingQueryService meetingQueryService;
    private final GoogleRouteClient client;
    private final EtaService etaService;

    public void saveMate(Mate mate) {
        mateRepository.save(mate);
    }

    public MateJoinResponse joinMeeting(Member member, MateJoinRequest request) {
        // inviteCode 로 약속 찾기
        Meeting meeting = meetingQueryService.findMeetingByInviteCode(request.getInviteCode());

        // 종료된 약속인지 확인
        if (meeting.isOverdue()) {
            throw new BadRequestException(MEETING_OVERDUE_ERROR);
        }

        // 이미 참가한 사용자인지 확인
        mateQueryService.validateMateNotExists(member.getId(), meeting.getId());

        saveMateAndEta(member, meeting, request);

        return MateJoinResponse.from(meeting);
    }

    private void saveMateAndEta(Member member, Meeting meeting, MateJoinRequest request) {
        Coordinates origin = request.toCoordinates();
        Coordinates target = meeting.getTargetCoordinates();
        RouteTime routeTime = client.calculateRouteTime(origin, target);
        Mate savedMate = mateRepository.save(request.toMate(meeting, member, routeTime.getMinutes()));
        etaService.saveFirstEtaOfMate(savedMate, routeTime);
    }
}
