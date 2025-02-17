package org.example.odiya.apicall.repository;

import org.example.odiya.apicall.domain.ApiCall;
import org.example.odiya.apicall.domain.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApiCallRepository extends JpaRepository<ApiCall, Long> {

    List<ApiCall> findAllByDateBetweenAndClientType(LocalDate start, LocalDate end, ClientType clientType);

    Optional<ApiCall> findByClientTypeAndDate(ClientType clientType, LocalDate date);
}
