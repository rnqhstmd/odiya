package org.example.odiya.notification.service.event;

import lombok.Getter;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.Notification;
import org.springframework.context.ApplicationEvent;

@Getter
public class HurryUpEvent extends ApplicationEvent {

    private final Mate mate;
    private final Notification notification;

    public HurryUpEvent(Object object, Mate mate, Notification notification) {
        super(object);
        this.mate = mate;
        this.notification = notification;
    }
}
