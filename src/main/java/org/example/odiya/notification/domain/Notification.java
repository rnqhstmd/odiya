package org.example.odiya.notification.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.odiya.common.domain.BaseEntity;
import org.example.odiya.mate.domain.Mate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id")
    private Mate mate;

    @Column(columnDefinition = "TIMESTAMP(6)")
    private LocalDateTime sendAt;

    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    @Enumerated(value = EnumType.STRING)
    private NotificationStatus status;

    @Embedded
    private FcmTopic fcmTopic;

    public boolean isStatusDismissed() {
        return status == NotificationStatus.DISMISSED;
    }

    public boolean isReminder() {
        return type == NotificationType.REMINDER;
    }

    public void updateStatusToDone() {
        this.status = NotificationStatus.DONE;
    }
}