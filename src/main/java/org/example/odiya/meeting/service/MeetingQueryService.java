package org.example.odiya.meeting.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.example.odiya.common.exception.type.ErrorType.MEETING_NOT_FOUND_ERROR;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeetingQueryService {

    private final MeetingRepository meetingRepository;

    public Meeting findMeetingByInviteCode(String inviteCode) {
        return meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND_ERROR));
    }

    public int updateOverdueMeetings(LocalDateTime now) {
        return meetingRepository.bulkUpdateOverdueStatus(now.toLocalDate(), now.toLocalTime());
    }

    public List<Meeting> findOverdueMeetings(Long memberId) {
        return meetingRepository.findAllByMemberIdAndOverdueFalse(memberId);
    }

    public Meeting findById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new NotFoundException(MEETING_NOT_FOUND_ERROR));
    }
}
