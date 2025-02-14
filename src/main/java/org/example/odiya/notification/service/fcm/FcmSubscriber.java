package org.example.odiya.notification.service.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.InternalServerException;
import org.example.odiya.member.domain.DeviceToken;
import org.example.odiya.notification.domain.FcmTopic;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.example.odiya.common.exception.type.ErrorType.FIREBASE_SUBSCRIBE_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.FIREBASE_UNSUBSCRIBE_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmSubscriber {

    private final FirebaseMessaging firebaseMessaging;

    public void subscribeTopic(FcmTopic fcmTopic, DeviceToken deviceToken) {
        try {
            firebaseMessaging.subscribeToTopic(
                    List.of(deviceToken.getValue()),
                    fcmTopic.getValue()
            );
        } catch (FirebaseMessagingException e) {
            log.error("토픽 구독 실패 - topic: {}, error: {}", fcmTopic.getValue(), e.getMessage());
            throw new InternalServerException(FIREBASE_SUBSCRIBE_ERROR, e.getMessage());
        }
    }

    public void unsubscribeTopic(FcmTopic topic, DeviceToken deviceToken) {
        try {
            firebaseMessaging.unsubscribeFromTopic(
                    List.of(deviceToken.getValue()),
                    topic.getValue()
            );
        } catch (FirebaseMessagingException e) {
            log.error("토픽 구독 해제 실패 - topic: {}, error: {}", topic.getValue(), e.getMessage());
            throw new InternalServerException(FIREBASE_UNSUBSCRIBE_ERROR, e.getMessage());
        }
    }
}
