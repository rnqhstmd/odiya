package org.example.odiya.meeting.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.odiya.common.BaseTest.BaseRepositoryTest;
import org.example.odiya.common.Fixture.Fixture;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MeetingRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Member member;
    private Meeting pastMeeting;
    private Meeting futureMeeting;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Given
        member = fixtureGenerator.generateMember();

        pastMeeting = Meeting.builder()
                .name(Fixture.CELEBRATE_MEETING.getName())
                .target(Fixture.CELEBRATE_MEETING.getTarget())
                .date(Fixture.CELEBRATE_MEETING.getDate())
                .time(Fixture.CELEBRATE_MEETING.getTime())
                .inviteCode(Fixture.CELEBRATE_MEETING.getInviteCode())
                .build();
        meetingRepository.save(pastMeeting);

        futureMeeting = Meeting.builder()
                .name(Fixture.SOJU_MEETING.getName())
                .target(Fixture.SOJU_MEETING.getTarget())
                .date(Fixture.SOJU_MEETING.getDate())
                .time(Fixture.SOJU_MEETING.getTime())
                .inviteCode(Fixture.SOJU_MEETING.getInviteCode())
                .build();
        meetingRepository.save(futureMeeting);

        // 각 모임에 참여자 추가
        fixtureGenerator.generateMate(pastMeeting, member);
        fixtureGenerator.generateMate(futureMeeting, member);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("초대 코드로 약속을 조회할 수 있다")
    void findByInviteCode_Success() {
        // Given
        String inviteCode = Fixture.CELEBRATE_MEETING.getInviteCode();

        // When
        Optional<Meeting> foundMeeting = meetingRepository.findByInviteCode(inviteCode);

        // Then
        assertThat(foundMeeting).isPresent();
        assertThat(foundMeeting.get().getInviteCode()).isEqualTo(inviteCode);
        assertThat(foundMeeting.get().getName()).isEqualTo(Fixture.CELEBRATE_MEETING.getName());
    }

    @Test
    @DisplayName("존재하지 않는 초대 코드로 조회시 빈 Optional을 반환한다")
    void findByInviteCode_WhenNotExists_ReturnsEmpty() {
        // Given
        String nonExistentInviteCode = "000000";

        // When
        Optional<Meeting> foundMeeting = meetingRepository.findByInviteCode(nonExistentInviteCode);

        // Then
        assertThat(foundMeeting).isEmpty();
    }

    @Test
    @DisplayName("현재 시간 이전의 약속들이 만료 상태로 업데이트된다")
    void bulkUpdateOverdueStatus_Success() {
        // Given
        LocalDateTime currentTime = now;

        // When
        int updatedCount = meetingRepository.bulkUpdateOverdueStatus(
                currentTime.toLocalDate(),
                currentTime.toLocalTime()
        );
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedCount).isEqualTo(1);

        Meeting updatedPastMeeting = meetingRepository.findByInviteCode(pastMeeting.getInviteCode()).orElseThrow();
        Meeting updatedFutureMeeting = meetingRepository.findByInviteCode(futureMeeting.getInviteCode()).orElseThrow();

        assertThat(updatedPastMeeting.isOverdue()).isTrue();
        assertThat(updatedFutureMeeting.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("사용자의 유효한 모임을 조회할 수 있다")
    void findAllByMemberIdAndOverdueFalse_Success() {
        // When
        List<Meeting> meetings = meetingRepository.findAllByMemberIdAndOverdueFalse(member.getId());

        // Then
        assertThat(meetings).hasSize(2);
        assertThat(meetings)
                .extracting("name")
                .containsExactlyInAnyOrder(
                        Fixture.SOJU_MEETING.getName(),
                        Fixture.CELEBRATE_MEETING.getName()
                );
    }

    @Test
    @DisplayName("모임이 없는 사용자의 유효한 모임 조회시 빈 리스트를 반환한다")
    void findAllByMemberIdAndOverdueFalse_WhenNoMeetings_ReturnsEmptyList() {
        // Given
        Member newMember = fixtureGenerator.generateMember();

        // When
        List<Meeting> meetings = meetingRepository.findAllByMemberIdAndOverdueFalse(newMember.getId());

        // Then
        assertThat(meetings).isEmpty();
    }
}