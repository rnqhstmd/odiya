package org.example.odiya.mate.repository;

import org.example.odiya.mate.domain.Mate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateRepository extends JpaRepository<Mate, Long> {

    boolean existsByMemberIdAndMeetingId(Long memberId, Long meetingId);
}