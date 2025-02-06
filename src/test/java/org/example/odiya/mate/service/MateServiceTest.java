package org.example.odiya.mate.service;

import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.common.exception.ConflictException;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.mate.dto.response.MateJoinResponse;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.example.odiya.route.domain.RouteInfo;
import org.example.odiya.route.service.GoogleRouteClient;
import org.example.odiya.route.service.RouteService;
import org.example.odiya.route.service.TmapRouteClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    private MateQueryService mateQueryService;

    @Autowired
    private EtaService etaService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private MeetingQueryService meetingQueryService;

    @MockBean
    private GoogleRouteClient googleRouteClient;
    @MockBean
    private TmapRouteClient tmapRouteClient;

    private Member member;
    private Meeting meeting;

    @BeforeEach
    void setUpMateTest() {
        // BaseServiceTest의 setUp()이 자동으로 실행됨
        member = fixtureGenerator.generateMember();
        meeting = fixtureGenerator.generateMeeting();
    }

    @Test
    @DisplayName("정상적으로 모임에 참가할 수 있다.")
    void joinMeeting_Success() {
        // Given
        MateJoinRequest request = dtoGenerator.generateMateJoinRequest(meeting);
        RouteInfo transitRouteInfo = new RouteInfo(4L, 1000L);
        RouteInfo walkingRouteInfo = new RouteInfo(3L, 500L);
        when(googleRouteClient.calculateRouteTime(any(Coordinates.class), any(Coordinates.class))).thenReturn(transitRouteInfo);
        when(tmapRouteClient.calculateRouteTime(any(Coordinates.class), any(Coordinates.class))).thenReturn(walkingRouteInfo);

        // When
        MateJoinResponse response = mateService.joinMeeting(member, request);

        // Then
        assertThat(response).isNotNull();
        Mate savedMate = mateRepository.findByMemberIdAndMeetingId(member.getId(), meeting.getId())
                .orElseThrow();
        assertThat(savedMate.getMember().getId()).isEqualTo(member.getId());
        assertThat(savedMate.getMeeting().getId()).isEqualTo(meeting.getId());
    }

    @Test
    @DisplayName("종료된 모임에 참가하려고 하면 예외가 발생한다.")
    void joinMeeting_MeetingOverdue() {
        // Given
        Meeting overdueMeeting = fixtureGenerator.generateOverdueMeeting();
        MateJoinRequest request = dtoGenerator.generateMateJoinRequest(overdueMeeting);

        // When & Then
        assertThatThrownBy(() -> mateService.joinMeeting(member, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("이미 참가한 사용자가 모임에 다시 참가하려고 하면 예외가 발생한다.")
    void joinMeeting_AlreadyJoined() {
        // Given
        Mate existingMate = fixtureGenerator.generateMate(meeting, member);
        MateJoinRequest request = dtoGenerator.generateMateJoinRequest(meeting);

        // When & Then
        assertThatThrownBy(() -> mateService.joinMeeting(member, request))
                .isInstanceOf(ConflictException.class);
    }
}