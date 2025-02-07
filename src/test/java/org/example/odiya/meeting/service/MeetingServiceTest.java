package org.example.odiya.meeting.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.common.Fixture.DtoGenerator;
import org.example.odiya.common.Fixture.Fixture;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.common.exception.ForbiddenException;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.service.EtaQueryService;
import org.example.odiya.eta.service.EtaService;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.service.MateQueryService;
import org.example.odiya.mate.service.MateService;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.dto.request.MeetingCreateRequest;
import org.example.odiya.meeting.dto.response.MeetingCreateResponse;
import org.example.odiya.meeting.dto.response.MeetingDetailResponse;
import org.example.odiya.meeting.dto.response.MeetingListResponse;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.Member;
import org.example.odiya.route.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.example.odiya.common.exception.type.ErrorType.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
class MeetingServiceTest extends BaseServiceTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MeetingQueryService meetingQueryService;

    @Autowired
    private EtaQueryService etaQueryService;

    @Autowired
    private MateService mateService;

    @Autowired
    private EtaService etaService;

    @Autowired
    private MateQueryService mateQueryService;

    @Autowired
    private MeetingService meetingService;

    @MockBean
    private RouteService routeService;

    private Member member;
    private Meeting meeting;
    private Mate mate;
    private List<Eta> etas;
    private MeetingCreateRequest createRequest;

    @BeforeEach
    void setUpTest() {
        member = fixtureGenerator.generateMember();

        meeting = Meeting.builder()
                .name(Fixture.SOJU_MEETING.getName())
                .target(Fixture.SOJU_MEETING.getTarget())
                .date(Fixture.SOJU_MEETING.getDate())
                .time(Fixture.SOJU_MEETING.getTime())
                .inviteCode(RandomStringUtils.randomNumeric(6))
                .build();
        meetingRepository.save(meeting);

        mate = fixtureGenerator.generateMate(meeting, member);
        Mate mate2 = fixtureGenerator.generateMate(meeting, fixtureGenerator.generateMember());

        etas = List.of(
                fixtureGenerator.generateEta(mate),
                fixtureGenerator.generateEta(mate2)
        );

        createRequest = DtoGenerator.generateMeetingCreateRequest(meeting);

        // RouteService Mock 설정
        given(routeService.calculateOptimalRoute(any(), any()))
                .willReturn(15L);
    }

    @Test
    @DisplayName("새로운 약속을 생성한다")
    void createMeeting_Success() {
        // When
        MeetingCreateResponse response = meetingService.createMeeting(member, createRequest);
        Meeting savedMeeting = meetingQueryService.findById(response.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(createRequest.getName());
        assertThat(savedMeeting).isNotNull();
        assertThat(savedMeeting.getName()).isEqualTo(createRequest.getName());
    }

    @Test
    @DisplayName("내 약속 목록을 조회한다")
    void getMyMeetingList_Success() {
        // When
        MeetingListResponse response = meetingService.getMyMeetingList(member);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMeetings()).isNotEmpty();
        assertThat(response.getMeetings().size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("약속 상세 정보를 조회한다")
    void getMeetingDetail_Success() {
        // When
        MeetingDetailResponse response = meetingService.getMeetingDetail(member, meeting.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(meeting.getName());
        assertThat(response.getAddress()).isEqualTo(meeting.getTarget().getAddress());
    }

    @Test
    @DisplayName("정상적으로 ETA를 업데이트한다")
    void updateEtaForMeetingMatesSuccess() {
        // Given
        given(routeService.calculateOptimalRoute(any(), any()))
                .willReturn(15L);

        // When
        meetingService.updateEtaForMeetingMates(meeting.getId(), member.getId());

        // Then
        // 비동기 작업 완료 대기 후 확인
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Eta> updatedEtas = etaQueryService.findAllByMeetingIdWithMate(meeting.getId());
            assertThat(updatedEtas)
                    .as("모든 ETA가 업데이트됨")
                    .hasSize(2)
                    .allSatisfy(eta -> {
                        assertThat(eta.getRemainingMinutes()).isEqualTo(15L);
                    });
        });
    }

    @Test
    @DisplayName("경로 계산에 실패한 경우 ETA는 missing으로 표시된다")
    void markEtaAsMissingWhenRouteCalculationFails() {
        // Given
        given(routeService.calculateOptimalRoute(any(), any()))
                .willThrow(new RuntimeException("Route calculation failed"));

        // When
        meetingService.updateEtaForMeetingMates(meeting.getId(), member.getId());

        // Then
        // 비동기 작업 완료 대기 후 확인
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Eta> updatedEtas = etaQueryService.findAllByMeetingIdWithMate(meeting.getId());
            assertThat(updatedEtas)
                    .as("모든 ETA가 missing으로 표시됨")
                    .hasSize(2)
                    .allMatch(Eta::isMissing);
        });
    }

    @Test
    @DisplayName("모임 시간이 지난 경우 예외를 발생시킨다")
    void throwExceptionWhenMeetingIsOverdue() {
        // Given
        Meeting overdueMeeting = fixtureGenerator.generateOverdueMeeting();

        fixtureGenerator.generateMate(overdueMeeting, member);

        // When & Then
        assertThatThrownBy(() ->
                meetingService.updateEtaForMeetingMates(overdueMeeting.getId(), member.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(MEETING_OVERDUE_ERROR.getMessage());
    }

    @Test
    @DisplayName("모임 시간 1시간 전이 아닌 경우 예외를 발생시킨다")
    void throwExceptionWhenNotOneHourBeforeMeeting() {
        // Given
        Meeting futureMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusDays(1));

        fixtureGenerator.generateMate(futureMeeting, member);

        // When & Then
        assertThatThrownBy(() ->
                meetingService.updateEtaForMeetingMates(futureMeeting.getId(), member.getId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(NOT_ONE_HOUR_BEFORE_MEETING_ERROR.getMessage());
    }

    @Test
    @DisplayName("멤버가 모임에 속하지 않은 경우 예외를 발생시킨다")
    void throwExceptionWhenMemberNotInMeeting() {
        // Given
        Member nonParticipant = fixtureGenerator.generateMember("모임 외부인");

        // When & Then
        assertThatThrownBy(() ->
                meetingService.updateEtaForMeetingMates(meeting.getId(), nonParticipant.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(NOT_PARTICIPATED_MATE_ERROR.getMessage());
    }
}