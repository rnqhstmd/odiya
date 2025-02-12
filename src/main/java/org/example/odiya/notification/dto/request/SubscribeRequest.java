package org.example.odiya.notification.dto.request;

import lombok.Getter;
import org.example.odiya.member.domain.DeviceToken;
import org.example.odiya.notification.domain.FcmTopic;
import org.springframework.context.ApplicationEvent;

@Getter
public class SubscribeRequest extends ApplicationEvent {

    private final DeviceToken deviceToken;
    private final FcmTopic fcmTopic;


    public SubscribeRequest(Object object, DeviceToken deviceToken, FcmTopic fcmTopic) {
        super(object);
        this.deviceToken = deviceToken;
        this.fcmTopic = fcmTopic;
    }
}
