package org.example.odiya.mate.service;

import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.common.exception.ConflictException;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.dto.request.MateJoinRequest;
import org.example.odiya.mate.dto.response.MateJoinResponse;
import org.example.odiya.mate.repository.MateRepository;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.example.odiya.route.domain.RouteTime;
import org.example.odiya.route.dto.response.GoogleDirectionResponse;
import org.example.odiya.route.service.GoogleRouteClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.odiya.common.exception.type.ErrorType.DUPLICATION_MATE_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class MateServiceTest {

    @Mock
    private MateRepository mateRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MateQueryService mateQueryService;

    @Mock
    private MeetingQueryService meetingQueryService;

    @Mock
    private GoogleRouteClient client;

    @InjectMocks
    private MateService mateService;

    private Member member;
    private Meeting meeting;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = Member.builder().id(1L).name("사용자1").email("test@test.com").password("abcd1234").build();
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        meeting = Meeting.builder().id(1L).name("모임1").inviteCode("123456").build();
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);
        // Location 객체 생성 및 Meeting에 설정
        Location meetingLocation = new Location("서울 강남구 선릉로 427", "37.504198", "127.049794");
        meeting.setTarget(meetingLocation);

        when(meetingQueryService.findMeetingByInviteCode("123456")).thenReturn(meeting);
    }

    @Test
    @DisplayName("정상적으로 모임에 참가할 수 있다.")
    void joinMeeting_Success() {
        // Request 설정
        MateJoinRequest request = new MateJoinRequest("123456", "서울 강남구 테헤란로 411", "37.505713", "127.050691");

        // Mock GoogleDirectionResponse 생성
        GoogleDirectionResponse mockResponse = createMockGoogleResponse();
        RouteTime routeTime = new RouteTime(mockResponse.getRoutes().get(0).getLegs().get(0).getDuration().getValue() / 60);

        // Mock 설정
        when(meetingQueryService.findMeetingByInviteCode(request.getInviteCode())).thenReturn(meeting);
        when(client.calculateRouteTime(any(Coordinates.class), any(Coordinates.class))).thenReturn(routeTime);

        Mate mate = request.toMate(meeting, member, routeTime.getMinutes());
        when(mateRepository.save(any(Mate.class))).thenReturn(mate);

        MateJoinResponse response = mateService.joinMeeting(member, request);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("종료된 모임에 참가하려고 하면 예외가 발생한다.")
    void joinMeeting_MeetingOverdue() {
        MateJoinRequest request = new MateJoinRequest();
        meeting.setOverdue(true);

        when(meetingQueryService.findMeetingByInviteCode(request.getInviteCode())).thenReturn(meeting);

        assertThatThrownBy(() -> mateService.joinMeeting(member, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("이미 참가한 사용자가 모임에 다시 참가하려고 하면 예외가 발생한다.")
    void joinMeeting_AlreadyJoined() {
        // Given
        MateJoinRequest request = new MateJoinRequest("123456", "서울 강남구 테헤란로 411", "37.505713", "127.050691");

        // When
        when(meetingQueryService.findMeetingByInviteCode(request.getInviteCode())).thenReturn(meeting);
        doThrow(new ConflictException(DUPLICATION_MATE_ERROR))
                .when(mateQueryService)
                .validateMateNotExists(member.getId(), meeting.getId());

        // Then
        assertThatThrownBy(() -> mateService.joinMeeting(member, request))
                .isInstanceOf(ConflictException.class);
    }

    private GoogleDirectionResponse createMockGoogleResponse() {
        GoogleDirectionResponse response = new GoogleDirectionResponse();

        // Route 설정
        GoogleDirectionResponse.Route route = new GoogleDirectionResponse.Route();

        // Leg 설정
        GoogleDirectionResponse.Leg leg = new GoogleDirectionResponse.Leg();
        GoogleDirectionResponse.TextValue duration = new GoogleDirectionResponse.TextValue();
        duration.setText("4 mins");
        duration.setValue(256L); // 256초 = 약 4분
        leg.setDuration(duration);

        // Location 설정
        GoogleDirectionResponse.Location startLocation = new GoogleDirectionResponse.Location();
        startLocation.setLat(37.5160869);
        startLocation.setLng(126.8867896);

        GoogleDirectionResponse.Location endLocation = new GoogleDirectionResponse.Location();
        endLocation.setLat(37.5160638);
        endLocation.setLng(126.8863715);

        leg.setStartLocation(startLocation);
        leg.setEndLocation(endLocation);
        leg.setStartAddress("10 Mullae-dong 5(o)-ga, Yeongdeungpo District, Seoul, South Korea");
        leg.setEndAddress("12 Mullae-dong 5(o)-ga, Yeongdeungpo District, Seoul, South Korea");

        // Steps 설정
        GoogleDirectionResponse.Step step = new GoogleDirectionResponse.Step();
        step.setDuration(duration);
        step.setStartLocation(startLocation);
        step.setEndLocation(endLocation);

        leg.setSteps(Collections.singletonList(step));
        route.setLegs(Collections.singletonList(leg));
        response.setRoutes(Collections.singletonList(route));

        // Status 설정
        response.setStatus("OK");

        return response;
    }
}