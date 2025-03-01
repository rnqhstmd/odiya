package org.example.odiya.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.meeting.domain.Meeting;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmTopic {

    private static final String TOPIC_NAME_DELIMITER = "_";
    private static final DateTimeFormatter MEETING_CREATE_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Column(name = "fcm_topic")
    private String value;

    public FcmTopic(Meeting meeting) {
        this(build(meeting));
    }

    public FcmTopic(String rawValue) {
        this.value = rawValue.replace(":", "-");
    }

    private static String build(Meeting meeting) {
        return meeting.getId().toString()
                + TOPIC_NAME_DELIMITER
                + meeting.getCreatedAt().format(MEETING_CREATE_AT_FORMAT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FcmTopic fcmTopic = (FcmTopic) o;
        return Objects.equals(getValue(), fcmTopic.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
