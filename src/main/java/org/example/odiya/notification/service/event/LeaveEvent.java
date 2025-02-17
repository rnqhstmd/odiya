package org.example.odiya.notification.service.event;

import com.google.firebase.messaging.Message;
import lombok.Getter;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.notification.domain.Notification;
import org.example.odiya.notification.domain.message.DirectMessage;
import org.springframework.context.ApplicationEvent;

import java.util.List;


@Getter
public class LeaveEvent extends ApplicationEvent {

    private final Mate leavingMate;
    private final Notification notification;
    private final List<DirectMessage> directMessages;

    public LeaveEvent(Object source, Mate leavingMate, Notification notification) {
        super(source);
        this.leavingMate = leavingMate;
        this.notification = notification;
        this.directMessages = createMeetingLeaveNotice(leavingMate, notification);
    }

    private static List<DirectMessage> createMeetingLeaveNotice(Mate leavingMate, Notification notification) {
        return leavingMate.getMeeting().getMates().stream()
                .filter(mate -> !mate.getId().equals(leavingMate.getId())) // 퇴장하는 사람 제외
                .map(mate -> new DirectMessage(Message.builder()
                        .putData("type", notification.getType().name())
                        .putData("name", leavingMate.getMember().getName())
                        .putData("meetingId", leavingMate.getMeeting().getId().toString())
                        .putData("title", String.format("%s님이 약속에서 나갔습니다.",
                                leavingMate.getMember().getName()))
                        .putData("body", String.format("%s님이 %s 약속에서 나갔습니다.",
                                leavingMate.getMember().getName(), leavingMate.getMeeting().getName()))
                        .setToken(mate.getMember().getDeviceToken().getValue())
                        .build())
                )
                .toList();
    }
}
