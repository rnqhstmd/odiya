package org.example.odiya.notification.dto.request;

import lombok.Getter;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.Notification;
import org.springframework.context.ApplicationEvent;

@Getter
public class HurryUpRequest extends ApplicationEvent {

    private final Mate mate;
    private final Notification notification;

    public HurryUpRequest(Object object, Mate mate, Notification notification) {
        super(object);
        this.mate = mate;
        this.notification = notification;
    }
}
