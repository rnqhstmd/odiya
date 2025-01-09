package org.example.odiya.meeting.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingOverDueScheduler {

    private final MeetingQueryService meetingQueryService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateMeetingOverdueStatus() {
        int updatedMeetingCount = meetingQueryService.updateOverdueMeetings(LocalDateTime.now());
        log.info("Updated overdue status of {} meetings", updatedMeetingCount);
    }
}
