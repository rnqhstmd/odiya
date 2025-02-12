package org.example.odiya.notification.dto.request;

import lombok.Getter;
import org.example.odiya.notification.domain.message.GroupMessage;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoticeRequest extends ApplicationEvent {

    private final GroupMessage groupMessage;

    public NoticeRequest(Object object, GroupMessage groupMessage) {
        super(object);
        this.groupMessage = groupMessage;
    }
}
