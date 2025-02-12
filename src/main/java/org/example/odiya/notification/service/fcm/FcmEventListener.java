package org.example.odiya.notification.service.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.message.DirectMessage;
import org.example.odiya.notification.domain.message.GroupMessage;
import org.example.odiya.notification.dto.request.HurryUpRequest;
import org.example.odiya.notification.dto.request.NoticeRequest;
import org.example.odiya.notification.dto.request.PushRequest;
import org.example.odiya.notification.dto.request.SubscribeRequest;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmEventListener {

    private final FcmPushSender fcmPushSender;
    private final FcmSubscriber fcmSubscriber;

    @Async("fcmExecutor")
    @EventListener
    public void handleSubscribe(SubscribeRequest request) {
        fcmSubscriber.subscribeTopic(request.getFcmTopic(), request.getDeviceToken());
        log.info("토픽 구독 완료 - topic: {}, token: {}",
                request.getFcmTopic().getValue(),
                request.getDeviceToken().getValue());
    }

    @Async("fcmExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePush(PushRequest request) {
        GroupMessage groupMessage = request.getGroupMessage();
        Notification notification = request.getNotification();

        fcmPushSender.sendGroupMessage(groupMessage, notification);
        log.info("푸시 알림 전송 - id: {}, type: {}",
                notification.getId(),
                notification.getType());
    }

    @Async("fcmExecutor")
    @EventListener
    public void handleNotice(NoticeRequest request) {
        GroupMessage groupMessage = request.getGroupMessage();
        fcmPushSender.sendNoticeMessage(groupMessage);
        log.info("공지 알림 전송 시간: {}", Instant.now().atZone(ZoneId.systemDefault()));
    }

    @Async("fcmExecutor")
    @EventListener
    public void handleHurryUp(HurryUpRequest request) {
        Mate mate = request.getMate();
        Notification notification = request.getNotification();

        DirectMessage directMessage = DirectMessage.createMessageToOther(mate, notification);
        fcmPushSender.sendDirectMessage(directMessage);
        log.info("재촉하기 알림 전송 - id: {}, 전송시간: {}",
                notification.getId(),
                notification.getSendAt());
    }
}
