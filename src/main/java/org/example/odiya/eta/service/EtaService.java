package org.example.odiya.eta.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.route.domain.RouteTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EtaService {

    private final EtaRepository etaRepository;

    @Transactional
    public Eta saveFirstEtaOfMate(Mate mate, RouteTime routeTime) {
        return etaRepository.save(new Eta(mate, routeTime.getMinutes()));
    }
}
