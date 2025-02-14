package org.example.odiya.notification.service.fcm;

import org.example.odiya.common.BaseTest.BaseServiceTest;
import org.example.odiya.notification.service.event.HurryUpEvent;
import org.example.odiya.notification.service.event.NoticeEvent;
import org.example.odiya.notification.service.event.PushEvent;
import org.example.odiya.notification.service.event.SubscribeEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

class FcmEventListenerTest extends BaseServiceTest {

    @Autowired
    private FcmPublisher fcmPublisher;

    @Test
    @DisplayName("SubscribeEvent 이벤트가 발행되면 구독 로직이 실행된다")
    void subscribeTopic() {
        // given
        SubscribeEvent subscribeEvent = mock(SubscribeEvent.class);

        // when
        fcmPublisher.publish(subscribeEvent);

        // then
        verify(fcmEventListener, times(1)).handleSubscribe(eq(subscribeEvent));
    }

    @Test
    @DisplayName("UnsubscribeEvent 발생 시, 주제 구독 해제 로직을 실행한다")
    void handleUnSubscribe() {
        // given
        SubscribeEvent unSubscribeEvent = mock(SubscribeEvent.class);

        // when
        fcmPublisher.publish(unSubscribeEvent);

        // then
        verify(fcmEventListener, times(1)).handleUnSubscribe(eq(unSubscribeEvent));
    }

    @Test
    @DisplayName("푸시 알림 이벤트가 발행되고 트랜잭션이 커밋되면 알림 발송 로직이 실행된다")
    void handlePushEventAfterCommit() {
        // given
        PushEvent pushEvent = mock(PushEvent.class);

        // when
        fcmPublisher.publishWithTransaction(pushEvent);

        // then
        verify(fcmEventListener, times(1)).handlePush(eq(pushEvent));
        verify(fcmEventListener).handlePush(pushEvent);
    }

    @Test
    @DisplayName("NoticeEvent 발생 시, 공지 알림 발송 로직을 실행한다")
    void sendNoticeMessage() {
        // given
        NoticeEvent noticeEvent = mock(NoticeEvent.class);

        // when
        fcmPublisher.publish(noticeEvent);

        // then
        verify(fcmEventListener, times(1)).handleNotice(eq(noticeEvent));
    }

    @Test
    @DisplayName("트랜잭션이 열리지 않으면, 푸시 알림 발송 로직이 실행되지 않는다")
    void notEventTriggerWhenTransactionNotOpen() {
        PushEvent pushEvent = mock(PushEvent.class);

        fcmPublisher.publish(pushEvent);

        verifyNoInteractions(fcmEventListener);
    }

    @Test
    @DisplayName("NudgeEvent 발생 시, 넛지 알림 발송 로직을 실행한다")
    void sendHurryUpMessage() {
        HurryUpEvent nudgeEvent = mock(HurryUpEvent.class);

        fcmPublisher.publish(nudgeEvent);

        verify(fcmEventListener, times(1)).handleHurryUp(eq(nudgeEvent));
    }
}