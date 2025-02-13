package org.example.odiya.notification.service;

import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.DeviceToken;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.NotificationType;
import org.example.odiya.notification.domain.types.HurryUpNotification;
import org.example.odiya.notification.repository.NotificationRepository;
import org.example.odiya.notification.service.event.HurryUpEvent;
import org.example.odiya.notification.service.event.SubscribeEvent;
import org.example.odiya.notification.service.fcm.FcmPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

class NotificationServiceTest extends BaseServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private TaskScheduler taskScheduler;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private FcmPublisher fcmPublisher;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @DisplayName("알림을 저장하고 스케줄링한다")
    @Test
    void saveAndScheduleNotification() {
        // given
        Mate mate = fixtureGenerator.generateMate();
        Notification notification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.REMINDER,
                NotificationStatus.PENDING
        );

        // when
        notificationService.saveAndScheduleNotification(notification);

        // then
        verify(taskScheduler).schedule(
                any(Runnable.class),
                any(Instant.class)
        );
        assertThat(notificationRepository.findAll()).hasSize(1);
    }

    @DisplayName("REMINDER 타입의 알림 상태를 DONE으로 변경한다")
    @Test
    void updateReminderStatusToDone() {
        // given
        Mate mate = fixtureGenerator.generateMate();
        Notification notification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.REMINDER,
                NotificationStatus.PENDING
        );

        // when
        notificationService.updateStatusToDone(notification);

        // then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.DONE);
    }

    @DisplayName("REMINDER가 아닌 알림은 상태가 변경되지 않는다")
    @Test
    void notUpdateNonReminderStatus() {
        // given
        Mate mate = fixtureGenerator.generateMate();
        Notification notification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.ENTRY,
                NotificationStatus.PENDING
        );

        // when
        notificationService.updateStatusToDone(notification);

        // then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
    }

    @DisplayName("재촉하기 알림을 저장하고 이벤트를 발행한다")
    @Test
    void sendHurryUpNotification() {
        // given
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate sender = fixtureGenerator.generateMate(meeting);
        Mate receiver = fixtureGenerator.generateMate(meeting);
        HurryUpNotification hurryUpNotification = new HurryUpNotification(receiver);

        // when
        notificationService.sendHurryUpNotification(sender, hurryUpNotification);

        // then
        verify(fcmPublisher).publishWithTransaction(any(HurryUpEvent.class));
        assertThat(notificationRepository.findAll()).hasSize(1);
    }

    @DisplayName("토픽 구독 이벤트를 발행한다")
    @Test
    void subscribeTopic() {
        // given
        Meeting meeting = fixtureGenerator.generateMeeting();
        DeviceToken deviceToken = new DeviceToken("test-token");
        FcmTopic fcmTopic = new FcmTopic(meeting);

        // when
        notificationService.subscribeTopic(deviceToken, fcmTopic);

        // then
        verify(fcmPublisher).publishWithTransaction(any(SubscribeEvent.class));
    }

    @DisplayName("토픽 구독 해제 이벤트를 발행한다")
    @Test
    void unsubscribeTopic() {
        // given
        Meeting meeting = fixtureGenerator.generateMeeting();
        DeviceToken deviceToken = new DeviceToken("test-token");

        // when
        notificationService.unsubscribeTopic(meeting, deviceToken);

        // then
        verify(fcmPublisher).publish(any(SubscribeEvent.class));
    }
}