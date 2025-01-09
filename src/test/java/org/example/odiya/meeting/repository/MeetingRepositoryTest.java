package org.example.odiya.meeting.repository;

import org.example.odiya.meeting.domain.Meeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

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
}