package org.example.odiya.eta.service;

import lombok.RequiredArgsConstructor;
import org.example.odiya.common.exception.BadRequestException;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.meeting.service.MeetingQueryService;
import org.example.odiya.route.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.example.odiya.common.exception.type.ErrorType.NOT_ONE_HOUR_BEFORE_MEETING_ERROR;

@Service
@RequiredArgsConstructor
public class EtaService {

    private final EtaRepository etaRepository;
    private final MeetingQueryService meetingQueryService;
    private final EtaQueryService etaQueryService;
    private final RouteService routeService;

    @Transactional
    public void saveFirstEtaOfMate(Mate mate, long estimatedTime) {
        etaRepository.save(new Eta(mate, estimatedTime));
    }

    @Transactional
    public void updateEtaOfMate(Long meetingId) {
        Meeting meeting = meetingQueryService.findById(meetingId);
        verifyAnHourBeforeMeetingTime(meeting);

        meeting.getMates().forEach(mate -> {
            Eta eta = etaQueryService.findByMateId(mate.getId());

            long remainingMinutes = routeService.calculateOptimalRoute(
                    mate.getOrigin().getCoordinates(),
                    meeting.getTargetCoordinates()
            );
            eta.updateRemainingMinutes(remainingMinutes);
        });
    }

    private void verifyAnHourBeforeMeetingTime(Meeting meeting) {
        LocalDateTime meetingTime = meeting.getMeetingTime();
        if (LocalDateTime.now().isBefore(meetingTime.minusHours(1))) {
            throw new BadRequestException(NOT_ONE_HOUR_BEFORE_MEETING_ERROR);
        }
    }
}
