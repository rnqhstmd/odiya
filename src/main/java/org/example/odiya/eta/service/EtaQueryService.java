package org.example.odiya.eta.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.eta.repository.EtaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EtaQueryService {

    private final EtaRepository etaRepository;

}
