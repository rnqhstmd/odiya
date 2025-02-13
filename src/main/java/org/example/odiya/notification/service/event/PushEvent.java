package org.example.odiya.notification.service.event;

import lombok.Getter;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.message.GroupMessage;
import org.springframework.context.ApplicationEvent;

@Getter
public class PushEvent extends ApplicationEvent {

    private final Notification notification;
    private final GroupMessage groupMessage;

    public PushEvent(Object object, Notification notification) {
        super(object);
        this.notification = notification;
        this.groupMessage = GroupMessage.createGlobalNotice(notification);
    }
}
