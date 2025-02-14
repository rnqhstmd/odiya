package org.example.odiya.mate.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.eta.domain.EtaStatus;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.dto.request.HurryUpRequest;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.mate.dto.response.MateJoinResponse;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.example.odiya.member.domain.Member;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.types.EntryNotification;
import org.example.odiya.notification.domain.types.HurryUpNotification;
import org.example.odiya.notification.domain.types.ReminderNotification;
import org.example.odiya.notification.service.NotificationService;
import org.example.odiya.route.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.exception.type.ErrorType.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MateService {

    private final MateRepository mateRepository;
    private final MateQueryService mateQueryService;
    private final MeetingQueryService meetingQueryService;
    private final RouteService routeService;
    private final EtaService etaService;
    private final NotificationService notificationService;

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

        long estimatedTime = routeService.calculateOptimalRoute(originLocation.getCoordinates(), meeting.getTargetCoordinates());

        Mate mate = Mate.builder()
                .member(member)
                .meeting(meeting)
                .origin(originLocation)
                .estimatedTime(estimatedTime)
                .build();

        Mate savedMate = mateRepository.save(mate);
        etaService.saveFirstEtaOfMate(savedMate, estimatedTime);

        FcmTopic fcmTopic = new FcmTopic(meeting);

        // 약속 토픽 구독
        notificationService.subscribeTopic(member.getDeviceToken(), fcmTopic);

        // 약속 참가 알림 스케줄링
        EntryNotification entryNotification = new EntryNotification(savedMate, fcmTopic);
        notificationService.saveAndScheduleNotification(entryNotification.toNotification());

        // 약속 리마인더 알림 스케줄링
        ReminderNotification reminderNotification = new ReminderNotification(meeting, savedMate, fcmTopic);
        notificationService.saveAndScheduleNotification(reminderNotification.toNotification());
    }

    @Transactional
    public void HurryUpMate(HurryUpRequest request) {
        Mate sender = mateQueryService.findById(request.getSenderId());
        Mate receiver = mateQueryService.findById(request.getReceiverId());
        validateMateStatus(sender, receiver);

        Meeting meeting = meetingQueryService.findById(sender.getMeeting().getId());
        validateMeetingStatus(meeting);

        HurryUpNotification hurryUpNotification = new HurryUpNotification(receiver);
        notificationService.sendHurryUpNotification(sender, hurryUpNotification);
    }

    private void validateMateStatus(Mate sender, Mate receiver) {
        if (!sender.getMeeting().getId().equals(receiver.getMeeting().getId())) {
            throw new BadRequestException(NOT_SAME_MEETING_ERROR);
        }

        if (!validateLateMate(receiver)) {
            throw new BadRequestException(NOT_LATE_MATE_ERROR);
        }
    }

    private void validateMeetingStatus(Meeting meeting) {
        if (meeting.isOverdue()) {
            throw new BadRequestException(MEETING_OVERDUE_ERROR);
        }

        if (meeting.isBeforeOneHourMeetingTime()) {
            throw new BadRequestException(NOT_ONE_HOUR_BEFORE_MEETING_ERROR);
        }
    }

    private boolean validateLateMate(Mate receiver) {
        EtaStatus etaStatus = etaService.findEtaStatus(receiver);
        return etaStatus == EtaStatus.LATE;
    }
}
