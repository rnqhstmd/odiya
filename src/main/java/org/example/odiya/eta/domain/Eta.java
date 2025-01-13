package org.example.odiya.eta.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.odiya.common.domain.BaseEntity;
import org.example.odiya.common.util.TimeUtil;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Eta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "mate_id")
    private Mate mate;

    @Column(nullable = false)
    private long remainingMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType type;

    @Column(nullable = false)
    private boolean isMissing;

    @Column(nullable = false)
    private boolean isArrived;

    public Eta(Mate mate, Long remainingMinutes, TransportType type) {
        this(
                null,
                mate,
                remainingMinutes,
                type,
                false,
                false
        );
    }

    public void updateRemainingMinutes(long remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public void markAsArrived() {
        this.isArrived = true;
    }

    public boolean isArrivalSoon(Meeting meeting) {
        LocalDateTime now = TimeUtil.nowWithTrim();
        LocalDateTime estimatedArrival = now.plusMinutes(remainingMinutes);
        return !isArrived && (estimatedArrival.isBefore(meeting.getMeetingTime()) ||
                estimatedArrival.isEqual(meeting.getMeetingTime()));
    }

    public boolean isLateExpected(Meeting meeting) {
        if (isArrived || isMissing) {
            return false;
        }
        LocalDateTime now = TimeUtil.nowWithTrim();
        LocalDateTime estimatedArrival = now.plusMinutes(remainingMinutes);
        return estimatedArrival.isAfter(meeting.getMeetingTime());
    }
}
