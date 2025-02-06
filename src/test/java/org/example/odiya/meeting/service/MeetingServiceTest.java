package org.example.odiya.meeting.service;

import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.common.Fixture.DtoGenerator;
import org.example.odiya.common.Fixture.Fixture;
import org.example.odiya.mate.service.MateQueryService;
import org.example.odiya.mate.service.MateService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeetingServiceTest extends BaseServiceTest {

    @MockBean
    private MeetingRepository meetingRepository;

    @MockBean
    private MeetingQueryService meetingQueryService;

    @MockBean
    private MateService mateService;

    @MockBean
    private MateQueryService mateQueryService;

    @Autowired
    private MeetingService meetingService;

    private Member member;
    private Meeting meeting;
    private MeetingCreateRequest createRequest;

    @BeforeEach
    void setUpMeetingTest() {
        // Given
        member = fixtureGenerator.generateMember();
        meeting = Fixture.SOJU_MEETING;

        createRequest = DtoGenerator.generateMeetingCreateRequest(Fixture.SOJU_MEETING);
    }

    @Test
    @DisplayName("새로운 약속을 생성한다")
    void createMeeting_Success() {
        // Given
        when(meetingRepository.save(any(Meeting.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MeetingCreateResponse response = meetingService.createMeeting(member, createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(createRequest.getName());
        verify(meetingRepository).save(any(Meeting.class));
        verify(mateService).createAndSaveMate(
                eq(member),
                any(Meeting.class),
                eq(createRequest.getOriginAddress()),
                eq(createRequest.getOriginLatitude()),
                eq(createRequest.getOriginLongitude())
        );
    }

    @Test
    @DisplayName("내 약속 목록을 조회한다")
    void getMyMeetingList_Success() {
        // Given
        List<Meeting> meetings = List.of(meeting);
        when(meetingQueryService.findOverdueMeetings(member.getId())).thenReturn(meetings);
        when(mateQueryService.countByMeetingId(member.getId())).thenReturn(1);

        // When
        MeetingListResponse response = meetingService.getMyMeetingList(member);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMeetings()).hasSize(1);
        verify(meetingQueryService).findOverdueMeetings(member.getId());
        verify(mateQueryService).countByMeetingId(member.getId());
    }

    @Test
    @DisplayName("약속 상세 정보를 조회한다")
    void getMeetingDetail_Success() {
        // Given
        Long meetingId = meeting.getId();
        when(meetingQueryService.findById(meetingId)).thenReturn(meeting);

        // When
        MeetingDetailResponse response = meetingService.getMeetingDetail(member, meetingId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(meeting.getName());
        verify(meetingQueryService).findById(meetingId);
        verify(mateQueryService).validateMateExists(member.getId(), meetingId);
    }
}