package org.example.odiya.meeting.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.service.MateService;
import org.example.odiya.meeting.domain.Coordinates;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.dto.request.MeetingCreateRequest;
import org.example.odiya.meeting.dto.response.MeetingResponse;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MateService mateService;

    public MeetingResponse createMeeting(Member member, MeetingCreateRequest request) {
        Location location = new Location(
                request.getAddress(),
                new Coordinates(request.getLatitude(), request.getLongitude())
        );

        Meeting newMeeting = Meeting.builder()
                .name(request.getName())
                .date(request.getDate())
                .time(request.getTime())
                .target(location)
                .build();
        newMeeting.generateInviteCode();

        Meeting savedMeeting = meetingRepository.save(newMeeting);

        // 약속 생성자를 참가자로 등록
        Mate mate = new Mate(member, savedMeeting);
        mateService.saveMate(mate);

        return MeetingResponse.from(savedMeeting);
    }
}
