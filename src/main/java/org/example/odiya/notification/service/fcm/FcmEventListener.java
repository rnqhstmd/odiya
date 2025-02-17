package org.example.odiya.notification.service.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.member.domain.DeviceToken;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.message.DirectMessage;
import org.example.odiya.notification.domain.message.GroupMessage;
import org.example.odiya.notification.service.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmEventListener {

    private final FcmPushSender fcmPushSender;
    private final FcmSubscriber fcmSubscriber;

    @Async("fcmExecutor")
    @EventListener
    public void handleSubscribe(SubscribeEvent request) {
        fcmSubscriber.subscribeTopic(request.getFcmTopic(), request.getDeviceToken());
        log.info("토픽 구독 완료 - topic: {}, token: {}",
                request.getFcmTopic().getValue(),
                request.getDeviceToken().getValue());
    }

    @Async("fcmAsyncExecutor")
    @EventListener
    public void handleUnSubscribe(SubscribeEvent request) {
        FcmTopic topic = request.getFcmTopic();
        DeviceToken deviceToken = request.getDeviceToken();
        fcmSubscriber.unsubscribeTopic(topic, deviceToken);
        log.info("토픽 구독 해제 - topic: {}, token: {}",
                request.getFcmTopic().getValue(),
                request.getDeviceToken().getValue());
    }

    @Async("fcmExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePush(PushEvent request) {
        GroupMessage groupMessage = request.getGroupMessage();
        Notification notification = request.getNotification();

        fcmPushSender.sendGroupMessage(groupMessage, notification);
        log.info("푸시 알림 전송 - id: {}, type: {}",
                notification.getId(),
                notification.getType());
    }

    @Async("fcmExecutor")
    @EventListener
    public void handleLeave(LeaveEvent event) {
        Notification notification = event.getNotification();
        List<DirectMessage> directMessages = event.getDirectMessages();

        for (DirectMessage directMessage : directMessages) {
            fcmPushSender.sendDirectMessage(directMessage);
        }

        log.info("퇴장 알림 전송 완료 - id: {}, type: {}, 수신자 수: {}",
                notification.getId(),
                notification.getType(),
                directMessages.size());
    }

    @Async("fcmExecutor")
    @EventListener
    public void handleNotice(NoticeEvent request) {
        GroupMessage groupMessage = request.getGroupMessage();
        fcmPushSender.sendNoticeMessage(groupMessage);
        log.info("공지 알림 전송 시간: {}", Instant.now().atZone(ZoneId.systemDefault()));
    }

    @Async("fcmExecutor")
    @EventListener
    public void handleHurryUp(HurryUpEvent request) {
        Mate mate = request.getMate();
        Notification notification = request.getNotification();

        DirectMessage directMessage = DirectMessage.createMessageToOther(mate, notification);
        fcmPushSender.sendDirectMessage(directMessage);
        log.info("재촉하기 알림 전송 - id: {}, 전송시간: {}",
                notification.getId(),
                notification.getSendAt());
    }
}
