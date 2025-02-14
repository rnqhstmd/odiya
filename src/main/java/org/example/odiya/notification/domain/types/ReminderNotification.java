package org.example.odiya.notification.domain.types;

import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.NotificationStatus;
import org.example.odiya.notification.domain.NotificationType;

import java.time.LocalDateTime;

public class ReminderNotification extends AbstractNotification {

    public ReminderNotification(Meeting meeting, Mate mate, FcmTopic fcmTopic) {
        super(mate, getDepartureTime(meeting, mate.getEstimatedTime()), NotificationStatus.PENDING, fcmTopic);
    }

    public static LocalDateTime getDepartureTime(Meeting meeting, long estimatedTime) {
        return meeting.getMeetingTime().minusMinutes(estimatedTime);
    }

    @Override
    NotificationType getType() {
        return NotificationType.REMINDER;
    }
}
