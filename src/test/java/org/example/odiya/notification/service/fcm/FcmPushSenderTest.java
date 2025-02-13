package org.example.odiya.notification.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.NotificationType;
import org.example.odiya.notification.domain.message.DirectMessage;
import org.example.odiya.notification.domain.message.GroupMessage;
import org.example.odiya.notification.repository.NotificationRepository;
import org.example.odiya.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class FcmPushSenderTest extends BaseServiceTest {

    @Autowired
    private FcmPushSender fcmPushSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @DisplayName("그룹 메시지를 전송하고 알림 상태를 DONE으로 변경한다")
    @Test
    void sendGroupMessageSuccess() throws FirebaseMessagingException {
        // given
        Mate mate = fixtureGenerator.generateMate();
        Notification pendingNotification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.REMINDER,
                NotificationStatus.PENDING
        );
        GroupMessage groupMessage = GroupMessage.createGlobalNotice(pendingNotification);
        // firebaseMessaging mock 설정 추가
        when(firebaseMessaging.send(any(Message.class)))
                .thenReturn("message_id");  // 성공 시 반환값 설정

        // when
        fcmPushSender.sendGroupMessage(groupMessage, pendingNotification);

        // then
        assertAll(
                () -> verify(firebaseMessaging).send(any(Message.class)),
                () -> verify(notificationService).updateStatusToDone(any(Notification.class))
        );
    }

    @DisplayName("DISMISSED 상태의 알림은 메시지를 전송하지 않는다")
    @Test
    void skipDismissedNotification() {
        // given
        Mate mate = fixtureGenerator.generateMate();
        Notification notification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.REMINDER,
                NotificationStatus.DISMISSED
        );
        GroupMessage groupMessage = GroupMessage.createGlobalNotice(notification);

        // when
        fcmPushSender.sendGroupMessage(groupMessage, notification);

        // then
        assertAll(
                () -> verifyNoInteractions(firebaseMessaging),
                () -> verifyNoInteractions(notificationService)
        );
    }

    @DisplayName("다이렉트 메시지를 전송한다")
    @Test
    void sendDirectMessage() throws FirebaseMessagingException {
        // given
        Message message = mock(Message.class);
        DirectMessage directMessage = new DirectMessage(message);

        // when
        fcmPushSender.sendDirectMessage(directMessage);

        // then
        verify(firebaseMessaging).send(message);
    }

    @DisplayName("공지 메시지를 전송한다")
    @Test
    void sendNoticeMessage() throws FirebaseMessagingException {
        // given
        Message message = mock(Message.class);
        GroupMessage groupMessage = new GroupMessage(message);

        // when
        fcmPushSender.sendNoticeMessage(groupMessage);

        // then
        verify(firebaseMessaging).send(message);
    }
}