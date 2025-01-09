package org.example.odiya.meeting.repository;

import jakarta.persistence.EntityManager;
import org.example.odiya.meeting.domain.Meeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EntityManager entityManager;

    private Meeting meeting;

    @BeforeEach
    void setUp() {
        meeting = Meeting.builder().name("모임1").inviteCode("123456").build();
        meetingRepository.save(meeting);
    }

    @Test
    @DisplayName("초대 코드로 약속 객체를 조회할 수 있다.")
    void findByInviteCode_Success() {
        // When
        Optional<Meeting> foundMeeting = meetingRepository.findByInviteCode("123456");

        // Then
        assertThat(foundMeeting).isPresent();
        assertThat(foundMeeting.get().getInviteCode()).isEqualTo("123456");
    }

    @Test
    @DisplayName("현재 시간보다 이전의 약속들이 만료 상태로 업데이트된다.")
    void bulkUpdateOverdueStatus_Success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        // 과거 미팅 (만료되어야 함)
        Meeting pastMeeting = Meeting.builder()
                .name("지난 모임")
                .date(today.minusDays(1))
                .time(currentTime)
                .inviteCode("342122")
                .overdue(false)
                .build();

        // 미래 미팅 (만료되지 않아야 함)
        Meeting futureMeeting = Meeting.builder()
                .name("앞으로의 모임")
                .date(today.plusDays(1))
                .time(currentTime)
                .inviteCode("432512")
                .overdue(false)
                .build();

        meetingRepository.saveAll(Arrays.asList(pastMeeting, futureMeeting));

        // when
        int updatedCount = meetingRepository.bulkUpdateOverdueStatus(today, currentTime);

        // then
        assertThat(updatedCount).isEqualTo(1);

        entityManager.clear();

        Meeting updatedPastMeeting = meetingRepository.findByInviteCode("342122").orElseThrow();
        Meeting updatedFutureMeeting = meetingRepository.findByInviteCode("432512").orElseThrow();

        assertThat(updatedPastMeeting.isOverdue()).isTrue();
        assertThat(updatedFutureMeeting.isOverdue()).isFalse();
    }
}