package org.example.odiya.eta.repository;

import org.example.odiya.eta.domain.Eta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EtaRepository extends JpaRepository<Eta, Long> {
    Optional<Eta> findByMateId(Long mateId);

    @Query("SELECT e FROM Eta e JOIN FETCH e.mate m WHERE m.meeting.id = :meetingId")
    List<Eta> findAllByMeetingIdWithMate(@Param("meetingId") Long meetingId);
}
