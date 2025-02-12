package org.example.odiya.notification.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.message.DirectMessage;
import org.example.odiya.notification.domain.message.GroupMessage;
import org.example.odiya.notification.service.NotificationQueryService;
import org.example.odiya.notification.service.NotificationService;
import org.springframework.stereotype.Component;

import static org.example.odiya.common.exception.type.ErrorType.FIREBASE_SEND_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    private final FirebaseMessaging firebaseMessaging;
    private final NotificationService notificationService;
    private final NotificationQueryService notificationQueryService;

    public void sendGroupMessage(GroupMessage groupMessage, Notification notification) {
        Notification savedNotification = notificationQueryService.findById(notification.getId());
        if (savedNotification.isStatusDismissed()) {
            log.info("DISMISSED 상태 알림 전송 스킵 - id: {}", notification.getId());
            return;
        }
        sendMessage(groupMessage.message());
        notificationService.updateStatusToDone(savedNotification);
    }

    public void sendNoticeMessage(GroupMessage groupMessage) {
        sendMessage(groupMessage.message());
    }

    public void sendDirectMessage(DirectMessage directMessage) {
        sendMessage(directMessage.message());
    }

    private void sendMessage(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("메시지 전송 실패 - error: {}", e.getMessage());
            throw new InternalServerException(FIREBASE_SEND_ERROR);
        }
    }
}
