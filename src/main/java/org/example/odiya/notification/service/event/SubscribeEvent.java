package org.example.odiya.notification.service.event;

import lombok.Getter;
import org.example.odiya.member.domain.DeviceToken;
import org.example.odiya.notification.domain.FcmTopic;
import org.springframework.context.ApplicationEvent;

@Getter
public class SubscribeEvent extends ApplicationEvent {

    private final DeviceToken deviceToken;
    private final FcmTopic fcmTopic;


    public SubscribeEvent(Object object, DeviceToken deviceToken, FcmTopic fcmTopic) {
        super(object);
        this.deviceToken = deviceToken;
        this.fcmTopic = fcmTopic;
    }
}
