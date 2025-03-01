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
import org.example.odiya.notification.service.NotificationQueryService;
import org.example.odiya.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class FcmPushSenderTest extends BaseServiceTest {

    @Autowired
    private FcmPushSender fcmPushSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationQueryService notificationQueryService;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("DISMISSED 상태의 알림은 메시지를 전송하지 않는다")
    void skipDismissedNotification() {
        // given
        Mate mate = fixtureGenerator.generateMate();
        Notification dismissedNotification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.REMINDER,
                NotificationStatus.DISMISSED
        );
        GroupMessage groupMessage = GroupMessage.createGlobalNotice(dismissedNotification);

        when(notificationQueryService.findById(dismissedNotification.getId()))
                .thenReturn(dismissedNotification);

        // when
        fcmPushSender.sendGroupMessage(groupMessage, dismissedNotification);

        // then
        assertAll(
                () -> verifyNoInteractions(firebaseMessaging),
                () -> verifyNoInteractions(notificationService)
        );
    }

    @Test
    @DisplayName("다이렉트 메시지를 전송한다")
    void sendDirectMessage() throws FirebaseMessagingException {
        // given
        Message message = mock(Message.class);
        DirectMessage directMessage = new DirectMessage(message);

        // when
        fcmPushSender.sendDirectMessage(directMessage);

        // then
        verify(firebaseMessaging).send(message);
    }

    @Test
    @DisplayName("공지 메시지를 전송한다")
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