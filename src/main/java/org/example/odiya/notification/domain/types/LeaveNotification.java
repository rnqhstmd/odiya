package org.example.odiya.notification.domain.types;

import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.NotificationType;

import java.time.LocalDateTime;

public class LeaveNotification extends AbstractNotification {

    public LeaveNotification(Mate mate) {
        super(mate, LocalDateTime.now(), NotificationStatus.DONE, null);
    }

    @Override
    NotificationType getType() {
        return NotificationType.LEAVE;
    }
}
