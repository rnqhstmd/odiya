package org.example.odiya.meeting.repository;

import org.example.odiya.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByInviteCode(String inviteCode);
}
