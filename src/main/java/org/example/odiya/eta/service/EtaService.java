package org.example.odiya.eta.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.route.domain.RouteInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EtaService {

    private final EtaRepository etaRepository;

    @Transactional
    public void saveFirstEtaOfMate(Mate mate, RouteInfo routeInfo) {
        etaRepository.save(new Eta(mate, routeInfo.getMinutes()));
    }
}
