package org.example.odiya.notification.domain.message;

import com.google.firebase.messaging.Message;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.Notification;

public record DirectMessage(Message message) {

    public static DirectMessage createMessageToOther(Mate sender, Notification notification) {
        return new DirectMessage(
                Message.builder()
                        .putData("type", notification.getType().name())
                        .putData("name", sender.getMember().getName())
                        .putData("meetingId", sender.getMeeting().getId().toString())
                        .setToken(notification.getMate().getMember().getDeviceToken().getValue())
                        .build()
        );
    }

    public static DirectMessage createMessageToSelf(Notification notification) {
        return new DirectMessage(
                Message.builder()
                        .putData("type", notification.getType().name())
                        .putData("name", notification.getMate().getMember().getName())
                        .putData("meetingId", notification.getMate().getMeeting().getId().toString())
                        .setToken(notification.getMate().getMember().getDeviceToken().getValue())
                        .build()
        );
    }
}
