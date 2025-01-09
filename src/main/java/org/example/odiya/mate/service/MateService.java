package org.example.odiya.mate.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.repository.MateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MateService {

    private final MateRepository mateRepository;

    public void saveMate(Mate mate) {
        mateRepository.save(mate);
    }
}
