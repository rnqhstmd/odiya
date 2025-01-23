package org.example.odiya.eta.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.repository.EtaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.exception.type.ErrorType.MATE_ETA_NOT_FOUND_ERROR;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EtaQueryService {

    private final EtaRepository etaRepository;

    public Eta findByMateId(Long mateId) {
        return etaRepository.findByMateId(mateId)
                .orElseThrow(() -> new NotFoundException(MATE_ETA_NOT_FOUND_ERROR));
    }
}
