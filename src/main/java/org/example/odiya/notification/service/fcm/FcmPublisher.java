package org.example.odiya.notification.service.fcm;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Transactional
    public void publishWithTransaction(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }
}
