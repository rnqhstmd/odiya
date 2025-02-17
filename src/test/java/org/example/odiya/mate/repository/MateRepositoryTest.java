package org.example.odiya.mate.repository;

import org.example.odiya.common.BaseTest.BaseRepositoryTest;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MateRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private MateRepository mateRepository;

    private Member member;
    private Meeting meeting;

    @BeforeEach
    void setUpMateRepository() {
        member = fixtureGenerator.generateMember();
        meeting = fixtureGenerator.generateMeeting();
    }

    @Test
    @DisplayName("약속에 참여하지 않은 멤버 확인")
    void existsByMemberIdAndMeetingId_WhenNotExists_ReturnsFalse() {
        // Given
        Long memberId = member.getId();
        Long meetingId = meeting.getId();

        // When
        boolean exists = mateRepository.existsByMemberIdAndMeetingId(memberId, meetingId);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("약속에 이미 참여한 멤버 확인")
    void existsByMemberIdAndMeetingId_WhenExists_ReturnsTrue() {
        // Given
        Mate mate = fixtureGenerator.generateMate(meeting, member);

        // When
        boolean exists = mateRepository.existsByMemberIdAndMeetingId(member.getId(), meeting.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("약속의 참여자 수 확인")
    void countByMeetingId_ReturnsCorrectCount() {
        // Given
        fixtureGenerator.generateMate(meeting, member);
        Member anotherMember = fixtureGenerator.generateMember();
        fixtureGenerator.generateMate(meeting, anotherMember);

        // When
        int count = mateRepository.countByMeetingId(meeting.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("멤버와 약속으로 참여 정보 조회")
    void findByMemberId_AndMeetingIdv1_WhenExists_ReturnsMate() {
        // Given
        Mate savedMate = fixtureGenerator.generateMate(meeting, member);

        // When
        Optional<Mate> foundMate = mateRepository.findByMeetingIdAndMemberId(member.getId(), meeting.getId());

        // Then
        assertThat(foundMate).isPresent();
        assertThat(foundMate.get().getId()).isEqualTo(savedMate.getId());
        assertThat(foundMate.get().getMember().getId()).isEqualTo(member.getId());
        assertThat(foundMate.get().getMeeting().getId()).isEqualTo(meeting.getId());
    }

    @Test
    @DisplayName("존재하지 않는 참여 정보 조회")
    void findByMemberId_AndMeetingIdv1_WhenNotExists_ReturnsEmpty() {
        // Given
        Long nonExistentMemberId = 999L;
        Long nonExistentMeetingId = 999L;

        // When
        Optional<Mate> foundMate = mateRepository.findByMeetingIdAndMemberId(nonExistentMemberId, nonExistentMeetingId);

        // Then
        assertThat(foundMate).isEmpty();
    }
}