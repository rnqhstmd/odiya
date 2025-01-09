package org.example.odiya.mate.repository;

import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.repository.MeetingRepository;
import org.example.odiya.member.domain.Member;
import org.example.odiya.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MateRepositoryTest {

    @Autowired
    private MateRepository mateRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MeetingRepository meetingRepository;

    private Member member;
    private Meeting meeting;

    @BeforeEach
    void setUp() {
        member = Member.builder().name("사용자1").email("test@test.com").password("abcd1234").build();
        memberRepository.save(member);
        meeting = Meeting.builder().name("모임1").inviteCode("123456").build();
        meetingRepository.save(meeting);
    }

    @Test
    @DisplayName("이미 약속에 참여한 멤버인지 확인한다.")
    void existsByMemberIdAndMeetingId_NotExists() {
        // given
        Long memberId = 1L;
        Long meetingId = 1L;

        // when
        boolean exists = mateRepository.existsByMemberIdAndMeetingId(memberId, meetingId);

        // then
        assertThat(exists).isFalse();
    }
}