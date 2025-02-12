package org.example.odiya.notification.domain.message;

import com.google.firebase.messaging.Message;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.notification.domain.FcmTopic;
import org.example.odiya.notification.domain.Notification;

public record GroupMessage(Message message) {

    public static GroupMessage createGlobalNotice(Notification notification) {
        return new GroupMessage(
                Message.builder()
                        .putData("type", notification.getType().name())
                        .putData("name", notification.getMate().getMember().getName())
                        .putData("meetingId", notification.getMate().getMeeting().getId().toString())
                        .setToken(notification.getMate().getMember().getDeviceToken().getValue())
                        .build()
        );
    }

    public static GroupMessage createMeetingNotice(Meeting meeting, Notification notification) {
        FcmTopic fcmTopic = new FcmTopic(meeting);
        return new GroupMessage(
                Message.builder()
                        .putData("type", notification.getType().name())
                        .putData("name", notification.getMate().getMember().getName())
                        .putData("meetingId", meeting.getId().toString())
                        .setTopic(fcmTopic.getValue())
                        .build()
        );
    }
}
