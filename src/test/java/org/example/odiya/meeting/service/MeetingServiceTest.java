package org.example.odiya.meeting.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private MeetingQueryService meetingQueryService;
    @Mock
    private MateService mateService;
    @Mock
    private MateQueryService mateQueryService;

    @InjectMocks
    private MeetingService meetingService;

    private Member member;
    private Meeting meeting;
    private MeetingCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = Member.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .build();

        Location targetLocation = new Location(
                "서울시 강남구",
                new Coordinates("37.123456", "127.123456")
        );

        meeting = Meeting.builder()
                .id(1L)
                .name("테스트 모임")
                .target(targetLocation)
                .date(LocalDate.now())
                .time(LocalTime.now())
                .build();

        createRequest = new MeetingCreateRequest(
                "테스트 모임",
                LocalDate.now(),
                LocalTime.now(),
                "placeId",
                "placeName",
                "서울시 강남구",
                "127.123456",
                "37.123456",
                "서울시 서초구",
                "37.123456",
                "127.123456"
        );
    }

    @Test
    @DisplayName("새로운 약속을 생성한다.")
    void createMeeting_Success() {
        // given
        when(meetingRepository.save(any(Meeting.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MeetingCreateResponse response = meetingService.createMeeting(member, createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(createRequest.getName());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(mateService, times(1)).createAndSaveMate(
                eq(member),
                any(Meeting.class),
                eq(createRequest.getOriginAddress()),
                eq(createRequest.getOriginLatitude()),
                eq(createRequest.getOriginLongitude())
        );
    }

    @Test
    @DisplayName("내 약속 목록을 조회한다.")
    void getMyMeetingList_Success() {
        // given
        List<Meeting> meetings = List.of(meeting);
        when(meetingQueryService.findOverdueMeetings(member.getId())).thenReturn(meetings);
        when(mateQueryService.countByMeetingId(member.getId())).thenReturn(1);

        // when
        MeetingListResponse response = meetingService.getMyMeetingList(member);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getMeetings()).hasSize(1);
        verify(meetingQueryService, times(1)).findOverdueMeetings(member.getId());
        verify(mateQueryService, times(1)).countByMeetingId(member.getId());
    }

    @Test
    @DisplayName("약속 상세 정보를 조회한다.")
    void getMeetingDetail_Success() {
        // given
        Long meetingId = 1L;
        when(meetingQueryService.findMeetingsByMemberId(meetingId)).thenReturn(meeting);

        // when
        MeetingDetailResponse response = meetingService.getMeetingDetail(member, meetingId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(meeting.getName());
        verify(meetingQueryService, times(1)).findMeetingsByMemberId(meetingId);
        verify(mateQueryService, times(1)).validateMateExists(member.getId(), meetingId);
    }
}