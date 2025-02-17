package org.example.odiya.mate.repository;

import org.example.odiya.mate.domain.Mate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MateRepository extends JpaRepository<Mate, Long> {

    boolean existsByMemberIdAndMeetingId(Long memberId, Long meetingId);

    int countByMeetingId(Long meetingId);

    @Query("SELECT m " +
            "FROM Mate m " +
            "JOIN FETCH m.member " +
            "JOIN FETCH m.meeting  " +
            "WHERE m.meeting.id = :meetingId " +
            "AND m.member.id = :memberId")
    Optional<Mate> findByMeetingIdAndMemberId(Long memberId, Long meetingId);
}