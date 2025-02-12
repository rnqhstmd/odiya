package org.example.odiya.notification.domain.types;

import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.NotificationType;

import java.time.LocalDateTime;

public class EntryNotification extends AbstractNotification {

    public EntryNotification(Mate mate, FcmTopic fcmTopic) {
        super(mate, LocalDateTime.now(), NotificationStatus.DONE, fcmTopic);
    }

    @Override
    NotificationType getType() {
        return NotificationType.ENTRY;
    }
}
