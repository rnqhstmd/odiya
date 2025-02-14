package org.example.odiya.eta.domain;

import org.example.odiya.common.Fixture.Fixture;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class EtaStatusTest {

    @DisplayName("MISSING: 위치추적 불가 상태일 때")
    @Test
    void returnMissing() {
        // Given
        Meeting meeting = Fixture.SOJU_MEETING;
        Member member = Fixture.MEMBER1;

        Mate mate = Mate.builder()
                .member(member)
                .meeting(meeting)
                .origin(Fixture.ORIGIN_LOCATION)
                .estimatedTime(1000L)
                .build();

        Eta eta = new Eta(mate, 10L);
        eta.markAsMissing();

        // When & Then
        assertThat(EtaStatus.of(eta, meeting)).isEqualTo(EtaStatus.MISSING);
    }

    @DisplayName("LATE_EXPECTED: 아직 약속시간이 안됐지만 도착예정시간이 약속시간 이후일 때")
    @Test
    void returnLateExpected() {
        // Given
        Meeting meeting = new Meeting(
                null,
                "테스트 미팅",
                Fixture.TARGET_LOCATION,
                LocalDate.now(),
                LocalTime.now().plusMinutes(1),
                "invite_code",
                false,
                new ArrayList<>()
        );

        Mate mate = Mate.builder()
                .member(Fixture.MEMBER1)
                .meeting(meeting)
                .origin(Fixture.ORIGIN_LOCATION)
                .estimatedTime(1000L)
                .build();

        Eta eta = new Eta(mate, 2L); // 2분 후 도착 예정

        // When & Then
        assertThat(EtaStatus.of(eta, meeting)).isEqualTo(EtaStatus.LATE_EXPECTED);
    }

    @DisplayName("LATE: 약속시간이 지났고 도착예정시간이 약속시간 이후일 때")
    @Test
    void returnLate() {
        // Given
        Meeting meeting = new Meeting(
                null,
                "테스트 미팅",
                Fixture.TARGET_LOCATION,
                LocalDate.now(),
                LocalTime.now().minusMinutes(1),
                "invite_code",
                false,
                new ArrayList<>()
        );

        Mate mate = Mate.builder()
                .member(Fixture.MEMBER1)
                .meeting(meeting)
                .origin(Fixture.ORIGIN_LOCATION)
                .estimatedTime(1000L)
                .build();

        Eta eta = new Eta(mate, 1L); // 1분 후 도착 예정

        // When & Then
        assertThat(EtaStatus.of(eta, meeting)).isEqualTo(EtaStatus.LATE);
    }

    @DisplayName("ARRIVED: 도착 상태일 때")
    @Test
    void returnArrived() {
        // Given
        Meeting meeting = Fixture.SOJU_MEETING;
        Mate mate = Mate.builder()
                .member(Fixture.MEMBER1)
                .meeting(meeting)
                .origin(Fixture.ORIGIN_LOCATION)
                .estimatedTime(1000L)
                .build();

        Eta eta = new Eta(mate, 10L);
        eta.markAsArrived();

        // When & Then
        assertThat(EtaStatus.of(eta, meeting)).isEqualTo(EtaStatus.ARRIVED);
    }

    @DisplayName("ARRIVAL_EXPECTED: 아직 약속시간이 안됐고 도착예정시간이 약속시간 이전일 때")
    @Test
    void returnArrivalExpected() {
        // Given
        Meeting meeting = new Meeting(
                null,
                "테스트 미팅",
                Fixture.TARGET_LOCATION,
                LocalDate.now(),
                LocalTime.now().plusMinutes(2),
                "invite_code",
                false,
                new ArrayList<>()
        );

        Mate mate = Mate.builder()
                .member(Fixture.MEMBER1)
                .meeting(meeting)
                .origin(Fixture.ORIGIN_LOCATION)
                .estimatedTime(1000L)
                .build();

        Eta eta = new Eta(mate, 0L); // 바로 도착 예정

        // When & Then
        assertThat(EtaStatus.of(eta, meeting)).isEqualTo(EtaStatus.ARRIVAL_EXPECTED);
    }
}