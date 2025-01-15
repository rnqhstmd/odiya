package org.example.odiya.meeting.repository;

import jakarta.persistence.EntityManager;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private Meeting pastMeeting;
    private Meeting futureMeeting;

    LocalDateTime now = LocalDateTime.now();
    LocalDate today = now.toLocalDate();
    LocalTime currentTime = now.toLocalTime();

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).name("사용자1").email("test@test.com").password("abcd1234").build();
        member = entityManager.merge(member); // persist 대신 merge 사용

        pastMeeting = Meeting.builder().name("지난 모임").inviteCode("123456").date(today.minusDays(1)).time(currentTime).overdue(false).build();
        futureMeeting = Meeting.builder().name("앞으로의 모임").inviteCode("654321").date(today.plusDays(1)).time(currentTime).overdue(false).build();
        entityManager.persist(pastMeeting);
        entityManager.persist(futureMeeting);

        Mate mate1 = Mate.builder().member(member).meeting(pastMeeting).build();
        Mate mate2 = Mate.builder().member(member).meeting(futureMeeting).build();
        entityManager.persist(mate1);
        entityManager.persist(mate2);

        entityManager.flush();
        entityManager.clear();
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
        // when
        int updatedCount = meetingRepository.bulkUpdateOverdueStatus(today, currentTime);

        // then
        assertThat(updatedCount).isEqualTo(1);

        entityManager.clear();

        Meeting updatedPastMeeting = meetingRepository.findByInviteCode("123456").orElseThrow();
        Meeting updatedFutureMeeting = meetingRepository.findByInviteCode("654321").orElseThrow();

        assertThat(updatedPastMeeting.isOverdue()).isTrue();
        assertThat(updatedFutureMeeting.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("사용자의 유효한 모임을 조회할 수 있다.")
    void findAllByMemberIdAndOverdueFalse_Success() {
        // When
        List<Meeting> meetings = meetingRepository.findAllByMemberIdAndOverdueFalse(member.getId());

        // Then
        assertThat(meetings).hasSize(2);
        assertThat(meetings).extracting("name").containsExactlyInAnyOrder("앞으로의 모임", "지난 모임");
    }
}