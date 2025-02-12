package org.example.odiya.notification.domain.types;

import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.NotificationType;

import java.time.LocalDateTime;

public abstract class AbstractNotification {

    private final Mate mate;
    private final NotificationType type;
    private final LocalDateTime sendAt;
    private final NotificationStatus status;
    private final FcmTopic fcmTopic;

    public AbstractNotification(Mate mate, LocalDateTime sendAt, NotificationStatus status, FcmTopic fcmTopic) {
        this.mate = mate;
        this.type = getType();
        this.sendAt = calculateSentAt(sendAt);
        this.status = status;
        this.fcmTopic = fcmTopic;
    }

    private static LocalDateTime calculateSentAt(LocalDateTime sentAt) {
        if (sentAt.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        } else {
            return sentAt;
        }
    }

    abstract NotificationType getType();

    public Notification toNotification() {
        return new Notification(null, mate, sendAt, type, status, fcmTopic);
    }
}
