package org.example.odiya.meeting.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.odiya.common.domain.BaseEntity;
import org.example.odiya.common.util.TimeUtil;
import org.example.odiya.mate.domain.Mate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Location target;

    @Column
    @NotNull
    private LocalDate date;

    @Column
    private LocalTime time;

    @Column(columnDefinition = "CHAR(6)", unique = true)
    private String inviteCode;

    @Builder.Default
    @Column(nullable = false)
    private boolean overdue = false;

    @Builder.Default
    @OneToMany(mappedBy = "meeting")
    private List<Mate> mates = new ArrayList<>();

    public Meeting(String name, LocalDate date, LocalTime time, Location target) {
        this(null, name, target, date, TimeUtil.trimSecondsAndNanos(time), null, false, null);
    }

    // 6자리 숫자로 구성된 초대 코드를 생성
    public void generateInviteCode() {
        this.inviteCode = RandomStringUtils.randomNumeric(6);
    }

    public boolean isEnd() {
        return LocalDateTime.now().isAfter(LocalDateTime.of(date, time));
    }

    public LocalDateTime getMeetingTime() {
        return TimeUtil.trimSecondsAndNanos(LocalDateTime.of(date, time));
    }
}
