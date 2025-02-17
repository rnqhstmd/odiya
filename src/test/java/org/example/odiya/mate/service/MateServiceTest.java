package org.example.odiya.mate.service;

import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.common.exception.ConflictException;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.domain.EtaStatus;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.dto.request.HurryUpRequest;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.mate.dto.response.MateJoinResponse;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.types.HurryUpNotification;
import org.example.odiya.notification.service.NotificationService;
import org.example.odiya.route.domain.RouteInfo;
import org.example.odiya.route.service.GoogleRouteClient;
import org.example.odiya.route.service.RouteService;
import org.example.odiya.route.service.TmapRouteClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MateServiceTest extends BaseServiceTest {

    @Autowired
    private MateService mateService;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private RouteService routeService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private EtaService etaService;

    @MockBean
    private GoogleRouteClient googleRouteClient;

    @MockBean
    private TmapRouteClient tmapRouteClient;

    private Member member;
    private Meeting meeting;
    private Mate sender;
    private Mate receiver;
    private Eta eta;
    private HurryUpRequest request;

    @BeforeEach
    void setUpMateTest() {
        member = fixtureGenerator.generateMember();
        meeting = fixtureGenerator.generateMeeting();

        sender = fixtureGenerator.generateMate(meeting, member);
        Member receiverMember = fixtureGenerator.generateMember();
        receiver = fixtureGenerator.generateLateMate(meeting, receiverMember);
        eta = fixtureGenerator.generateEta(receiver);
        request = dtoGenerator.generateHurryUpRequest(sender, receiver);
    }

    @Test
    @DisplayName("약속 참가에 성공한다")
    void joinMeeting_Success() {
        // Given
        Member newMember = fixtureGenerator.generateMember();
        MateJoinRequest mateJoinRequest = dtoGenerator.generateMateJoinRequest(meeting);
        RouteInfo transitRouteInfo = new RouteInfo(4L, 1000L);
        RouteInfo walkingRouteInfo = new RouteInfo(3L, 500L);

        when(googleRouteClient.calculateRouteTime(any(Coordinates.class), any(Coordinates.class)))
                .thenReturn(transitRouteInfo);
        when(tmapRouteClient.calculateRouteTime(any(Coordinates.class), any(Coordinates.class)))
                .thenReturn(walkingRouteInfo);

        // When
        MateJoinResponse response = mateService.joinMeeting(newMember, mateJoinRequest);

        // Then
        assertThat(response).isNotNull();
        Mate savedMate = mateRepository.findByMeetingIdAndMemberId(newMember.getId(), meeting.getId())
                .orElseThrow();
        assertThat(savedMate.getMember().getId()).isEqualTo(newMember.getId());
        assertThat(savedMate.getMeeting().getId()).isEqualTo(meeting.getId());
    }

    @Test
    @DisplayName("종료된 약속에 참가하려고 하면 예외가 발생한다.")
    void joinMeeting_MeetingOverdue() {
        // Given
        Meeting overdueMeeting = fixtureGenerator.generateOverdueMeeting();
        MateJoinRequest mateJoinRequest = dtoGenerator.generateMateJoinRequest(overdueMeeting);

        // When & Then
        assertThatThrownBy(() -> mateService.joinMeeting(member, mateJoinRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("이미 참가한 사용자가 모임에 다시 참가하려고 하면 예외가 발생한다.")
    void joinMeeting_AlreadyJoined() {
        // Given
        MateJoinRequest mateJoinRequest = dtoGenerator.generateMateJoinRequest(meeting);

        // When & Then
        assertThatThrownBy(() -> mateService.joinMeeting(member, mateJoinRequest))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("설정된 약속시간 1시간 전부터 재촉에 성공한다")
    void hurryUpMate_Success() {
        // Given
        when(etaService.findEtaStatus(any(Mate.class))).thenReturn(EtaStatus.LATE);

        // When
        assertDoesNotThrow(() -> mateService.hurryUpMate(member, request));

        // Then
        verify(notificationService, times(1))
                .sendHurryUpNotification(any(Mate.class), any(HurryUpNotification.class));
    }

    @Test
    @DisplayName("서로 다른 미팅의 메이트에게 재촉할 수 없다")
    void hurryUpMate_DifferentMeeting() {
        // Given
        Meeting otherMeeting = fixtureGenerator.generateMeeting();
        Mate otherReceiver = fixtureGenerator.generateMate(
                otherMeeting,
                fixtureGenerator.generateMember()
        );
        HurryUpRequest invalidRequest = new HurryUpRequest(
                sender.getId(),
                otherReceiver.getId()
        );

        // When & Then
        assertThatThrownBy(() -> mateService.hurryUpMate(member, invalidRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("약속 장소에 도착한 Mate에게 재촉할 수 없다")
    void hurryUpMate_NotLateMate() {
        // Given
        when(etaService.findEtaStatus(receiver)).thenReturn(EtaStatus.ARRIVED);

        // When & Then
        assertThatThrownBy(() -> mateService.hurryUpMate(member, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("종료된 약속에서는 재촉할 수 없다")
    void hurryUpMate_OverdueMeeting() {
        // Given
        Meeting overdueMeeting = fixtureGenerator.generateOverdueMeeting();
        Mate overdueReceiver = fixtureGenerator.generateMate(
                overdueMeeting,
                fixtureGenerator.generateMember()
        );
        HurryUpRequest overdueRequest = new HurryUpRequest(
                sender.getId(),
                overdueReceiver.getId()
        );

        // When & Then
        assertThatThrownBy(() -> mateService.hurryUpMate(member, overdueRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("현재시간이 설정된 약속시간의 1시간 전 보다 이전이라면 재촉할 수 없다")
    void hurryUpMate_BeforeOneHour() {
        // Given
        Meeting earlyMeeting = fixtureGenerator.generateMeetingBeforeOneHour();
        Mate earlyReceiver = fixtureGenerator.generateMate(
                earlyMeeting,
                fixtureGenerator.generateMember()
        );
        HurryUpRequest earlyRequest = new HurryUpRequest(
                sender.getId(),
                earlyReceiver.getId()
        );

        // When & Then
        assertThatThrownBy(() -> mateService.hurryUpMate(member, earlyRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("약속 탈퇴에 성공한다")
    void leaveMeeting_Success() {
        // Given
        Member newMember = fixtureGenerator.generateMember();
        Meeting newMeeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(newMeeting, newMember);
        fixtureGenerator.generateNotification(mate, LocalDateTime.now().plusHours(1), NotificationStatus.PENDING);
        fixtureGenerator.generateNotification(mate, LocalDateTime.now().minusHours(1), NotificationStatus.DONE);

        // When
        mateService.leaveMeeting(newMeeting.getId(), newMember);

        // Then
        assertThatThrownBy(() -> mateService.leaveMeeting(newMeeting.getId(), newMember))
                .isInstanceOf(NotFoundException.class);
    }
}