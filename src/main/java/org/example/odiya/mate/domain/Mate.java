package org.example.odiya.mate.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.odiya.common.domain.BaseEntity;
import org.example.odiya.meeting.domain.Location;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.Member;
import org.example.odiya.notification.domain.Notification;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Embedded
    private Location origin;

    @Column
    private long estimatedTime;

    @OneToMany(mappedBy = "mate", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    public Mate(Member member, Meeting meeting) {
        this.member = member;
        this.meeting = meeting;
    }

    public Mate(Member member, Meeting meeting, Location origin, long estimatedTime) {
        this.member = member;
        this.meeting = meeting;
        this.origin = origin;
        this.estimatedTime = estimatedTime;
    }
}
