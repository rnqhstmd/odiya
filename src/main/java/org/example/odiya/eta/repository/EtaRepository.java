package org.example.odiya.eta.repository;

import org.example.odiya.eta.domain.Eta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtaRepository extends JpaRepository<Eta, Long> {
    Optional<Eta> findByMateId(Long mateId);
}
