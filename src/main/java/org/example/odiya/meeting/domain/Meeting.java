package org.example.odiya.meeting.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.common.domain.BaseEntity;
import org.example.odiya.mate.domain.Mate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Location target;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(columnDefinition = "CHAR(6)", unique = true)
    private String inviteCode;

    @Column(nullable = false)
    private boolean overdue;

    @OneToMany(mappedBy = "meeting")
    private List<Mate> mates = new ArrayList<>();
}
