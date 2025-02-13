package org.example.odiya.notification.service.event;

import lombok.Getter;
import org.example.odiya.notification.domain.message.GroupMessage;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoticeEvent extends ApplicationEvent {

    private final GroupMessage groupMessage;

    public NoticeEvent(Object object, GroupMessage groupMessage) {
        super(object);
        this.groupMessage = groupMessage;
    }
}
