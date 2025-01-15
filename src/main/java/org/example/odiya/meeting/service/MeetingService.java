package org.example.odiya.meeting.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingQueryService meetingQueryService;
    private final MateService mateService;
    private final MateQueryService mateQueryService;

    public MeetingCreateResponse createMeeting(Member member, MeetingCreateRequest request) {
        Location targetLocation = new Location(
                request.getTargetAddress(),
                new Coordinates(request.getTargetLatitude(), request.getTargetLongitude())
        );

        Meeting newMeeting = Meeting.builder()
                .name(request.getName())
                .date(request.getDate())
                .time(request.getTime())
                .target(targetLocation)
                .build();
        newMeeting.generateInviteCode();

        Meeting savedMeeting = meetingRepository.save(newMeeting);

        // 약속 생성자의 Mate 생성
        mateService.createAndSaveMate(
                member,
                savedMeeting,
                request.getOriginAddress(),
                request.getOriginLatitude(),
                request.getOriginLongitude()
        );

        return MeetingCreateResponse.from(savedMeeting);
    }

    public MeetingListResponse getMyMeetingList(Member member) {
        int mateCount = mateQueryService.countByMeetingId(member.getId());
        List<Meeting> meetingList = meetingQueryService.findOverdueMeetings(member.getId());
        return MeetingListResponse.of(meetingList,mateCount);
    }

    public MeetingDetailResponse getMeetingDetail(Member member, Long meetingId) {
        Meeting meeting = meetingQueryService.findMeetingsByMemberId(meetingId);
        mateQueryService.validateMateExists(member.getId(), meetingId);
        return MeetingDetailResponse.from(meeting);
    }
}
