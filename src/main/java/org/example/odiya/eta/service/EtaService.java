package org.example.odiya.eta.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.mate.service.MateQueryService;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.example.odiya.route.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.example.odiya.common.exception.type.ErrorType.MEETING_OVERDUE_ERROR;
import static org.example.odiya.common.exception.type.ErrorType.NOT_ONE_HOUR_BEFORE_MEETING_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtaService {

    private final EtaRepository etaRepository;
    private final MeetingQueryService meetingQueryService;
    private final MateQueryService mateQueryService;
    private final EtaQueryService etaQueryService;
    private final RouteService routeService;

    @Transactional
    public void saveFirstEtaOfMate(Mate mate, long estimatedTime) {
        etaRepository.save(new Eta(mate, estimatedTime));
    }

    @Transactional
    public void updateEtaOfMate(Long meetingId, Long memberId) {
        Meeting meeting = meetingQueryService.findById(meetingId);
        mateQueryService.validateMateExists(meetingId, memberId);
        if (meeting.isOverdue()) {
            throw new BadRequestException(MEETING_OVERDUE_ERROR);
        }
        verifyAnHourBeforeMeetingTime(meeting);

        meeting.getMates().forEach(mate -> {
            Eta eta = etaQueryService.findByMateId(mate.getId());
            if (eta.isArrived() || eta.isMissing()) {
                return;
            }

            try {
                long remainingMinutes = routeService.calculateOptimalRoute(
                        mate.getOrigin().getCoordinates(),
                        meeting.getTargetCoordinates()
                );
                eta.updateRemainingMinutes(remainingMinutes);
            } catch (Exception e) {
                eta.markAsMissing();
                log.error("Failed to update ETA for mate: {}", mate.getId(), e);
            }

        });
    }

    private void verifyAnHourBeforeMeetingTime(Meeting meeting) {
        LocalDateTime meetingTime = meeting.getMeetingTime();
        if (LocalDateTime.now().isBefore(meetingTime.minusHours(1))) {
            throw new BadRequestException(NOT_ONE_HOUR_BEFORE_MEETING_ERROR);
        }
    }
}
