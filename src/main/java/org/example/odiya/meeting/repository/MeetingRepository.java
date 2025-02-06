package org.example.odiya.meeting.repository;

import org.example.odiya.meeting.domain.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByInviteCode(String inviteCode);

    @Query("UPDATE Meeting m SET m.overdue = true " +
            "WHERE m.overdue = false " +
            "AND ((m.date < :date) " +
            "OR (m.date = :date AND m.time <= :time))")
    @Modifying
    int bulkUpdateOverdueStatus(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    @Query("SELECT DISTINCT m " +
            "FROM Meeting m " +
            "JOIN FETCH m.mates mate " +
            "WHERE mate.member.id = :memberId " +
            "AND m.overdue = false")
    List<Meeting> findAllByMemberIdAndOverdueFalse(@Param("memberId") Long memberId);
}
