package org.example.odiya.mate.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.ConflictException;
import org.example.odiya.mate.repository.MateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.odiya.common.exception.type.ErrorType.DUPLICATION_MATE_ERROR;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MateQueryService {

    private final MateRepository mateRepository;

    public void validateMateNotExists(Long memberId, Long meetingId) {
        if (mateRepository.existsByMemberIdAndMeetingId(memberId, meetingId)) {
            throw new ConflictException(DUPLICATION_MATE_ERROR);
        }
    }
}
