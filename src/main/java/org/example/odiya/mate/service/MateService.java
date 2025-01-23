package org.example.odiya.mate.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.mate.dto.response.MateJoinResponse;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.example.odiya.member.domain.Member;
import org.example.odiya.route.domain.RouteInfo;
import org.example.odiya.route.service.GoogleRouteClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.constant.Constants.WALKING_THRESHOLD;
import static org.example.odiya.common.exception.type.ErrorType.MEETING_OVERDUE_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class MateService {

    private final MateRepository mateRepository;
    private final MateQueryService mateQueryService;
    private final MeetingQueryService meetingQueryService;
    private final GoogleRouteClient googleRouteClient;
    private final EtaService etaService;

    public MateJoinResponse joinMeeting(Member member, MateJoinRequest request) {
        // inviteCode 로 약속 찾기
        Meeting meeting = meetingQueryService.findMeetingByInviteCode(request.getInviteCode());

        // 종료된 약속인지 확인
        if (meeting.isOverdue()) {
            throw new BadRequestException(MEETING_OVERDUE_ERROR);
        }

        // 이미 참가한 사용자인지 확인
        mateQueryService.validateMateNotExists(member.getId(), meeting.getId());

        // Mate 생성 및 저장
        createAndSaveMate(
                member,
                meeting,
                request.getOriginAddress(),
                request.getOriginLatitude(),
                request.getOriginLongitude()
        );

        return MateJoinResponse.from(meeting);
    }

    public void createAndSaveMate(Member member, Meeting meeting, String address, String latitude, String longitude) {
        Location originLocation = new Location(
                address,
                new Coordinates(latitude, longitude)
        );

        Coordinates origin = new Coordinates(latitude, longitude);
        RouteInfo routeInfo = googleRouteClient.calculateRouteTime(origin, meeting.getTargetCoordinates());

        Mate mate = Mate.builder()
                .member(member)
                .meeting(meeting)
                .origin(originLocation)
                .estimatedTime(routeInfo.getMinutes())
                .build();

        Mate savedMate = mateRepository.save(mate);
        etaService.saveFirstEtaOfMate(savedMate, routeInfo);
    }

    private RouteInfo calculateOptimalRoute(Coordinates origin, Coordinates target) {
        RouteInfo transitTime = googleRouteClient.calculateRouteTime(origin, target);

        if (transitTime.getDistance() <= WALKING_THRESHOLD) {
            RouteInfo walkingTime = tmapRouteClient.calculateRouteTime(origin, target);
            return transitTime.getMinutes() <= walkingTime.getMinutes() ? transitTime : walkingTime;
        }

        return transitTime;
    }
}
