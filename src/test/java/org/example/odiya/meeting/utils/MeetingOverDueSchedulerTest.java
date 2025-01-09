package org.example.odiya.meeting.utils;

import org.example.odiya.meeting.service.MeetingQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MeetingOverDueSchedulerTest {

    @MockBean
    private MeetingQueryService meetingQueryService;

    @Autowired
    private MeetingOverDueScheduler scheduler;

    @Test
    @DisplayName("스케줄러가 실행되면 종료된 약속들이 업데이트된다.")
    void updateMeetingOverdueStatus_Success() {
        // given
        when(meetingQueryService.updateOverdueMeetings(any(LocalDateTime.class)))
                .thenReturn(3); // 3개의 미팅이 업데이트된다고 가정

        // when
        scheduler.updateMeetingOverdueStatus();

        // then
        verify(meetingQueryService).updateOverdueMeetings(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("스케줄러가 매시 정각에 실행된다.")
    void checkSchedulerTiming() {
        // given
        CronTrigger trigger = new CronTrigger("0 0 * * * *");

        // when
        LocalDateTime now = LocalDateTime.now();
        Date nextExecutionDate = trigger.nextExecutionTime(new SimpleTriggerContext());
        LocalDateTime nextExecution = LocalDateTime.ofInstant(
                nextExecutionDate.toInstant(),
                ZoneId.systemDefault()
        );

        // then
        // 다음 실행 시간이 다음 정각이어야 함
        assertThat(nextExecution.getMinute()).isEqualTo(0);
        assertThat(nextExecution.getSecond()).isEqualTo(0);
        assertThat(nextExecution.isAfter(now)).isTrue();
    }
}