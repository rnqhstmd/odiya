package org.example.odiya.meeting.service;

import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.service.MateQueryService;
import org.example.odiya.mate.service.MateService;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.dto.request.MeetingCreateRequest;
import org.example.odiya.meeting.dto.response.MeetingResponse;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeetingServiceTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MateService mateService;

    @Mock
    private MateQueryService mateQueryService;

    @Mock
    private MeetingQueryService meetingQueryService;

    @InjectMocks
    private MeetingService meetingService;

    private Member member;
    private Meeting meeting;
    private MeetingCreateRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = Member.builder().id(1L).name("사용자1").email("test@test.com").password("abcd1234").build();
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        meeting = Meeting.builder().id(1L).name("모임1").inviteCode("123456").build();
        when(meetingRepository.save(any(Meeting.class))).thenReturn(meeting);

        request = new MeetingCreateRequest(
                "Test Meeting",
                LocalDate.now(),
                LocalTime.now(),
                "111222333",
                "Test Place",
                "Test Address",
                "37.5665",
                "126.9780"
        );
    }

    @Test
    @DisplayName("새로운 약속을 생성한다.")
    void createMeeting_Success() {
        when(meetingRepository.save(any(Meeting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MeetingResponse response = meetingService.createMeeting(member, request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(request.getName());
        verify(meetingRepository, times(1)).save(any(Meeting.class));
        verify(mateService, times(1)).joinMeeting(any(Mate.class));
    }

    @Test
    @DisplayName("이미 참가한 사용자가 아니라면 약속 참가에 성공한다.")
    void joinMeeting_Success() {
        when(meetingQueryService.findMeetingByInviteCode(anyString())).thenReturn(meeting);
        doNothing().when(mateQueryService).isMateExist(anyLong(), anyLong());

        meetingService.joinMeeting(member, "123456");

        verify(meetingQueryService, times(1)).findMeetingByInviteCode(anyString());
        verify(mateQueryService, times(1)).isMateExist(anyLong(), anyLong());
        verify(mateService, times(1)).joinMeeting(any(Mate.class));
    }
}