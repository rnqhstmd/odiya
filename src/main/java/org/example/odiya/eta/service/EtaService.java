package org.example.odiya.eta.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.eta.domain.Eta;
import org.example.odiya.eta.domain.EtaStatus;
import org.example.odiya.eta.dto.response.EtaUpdateResult;
import org.example.odiya.eta.repository.EtaRepository;
import org.example.odiya.mate.domain.Mate;
import org.example.odiya.meeting.domain.Meeting;
import org.example.odiya.route.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtaService {

    private static final long ARRIVAL_THRESHOLD_MINUTES = 3;

    private final EtaRepository etaRepository;
    private final EtaQueryService etaQueryService;
    private final RouteService routeService;

    @Transactional
    public void saveFirstEtaOfMate(Mate mate, long estimatedTime) {
        etaRepository.save(new Eta(mate, estimatedTime));
    }

    @Transactional
    public void saveEtaUpdates(List<CompletableFuture<EtaUpdateResult>> futures) {
        List<Eta> etasToUpdate = futures.stream()
                .map(CompletableFuture::join)
                .map(result -> {
                    Eta eta = result.eta();
                    if (result.isFailed()) {
                        eta.markAsMissing();
                    } else {
                        if (result.remainingMinutes() <= ARRIVAL_THRESHOLD_MINUTES) {
                            eta.markAsArrived();
                        }
                        eta.updateRemainingMinutes(result.remainingMinutes());
                    }
                    return eta;
                })
                .toList();

        etaRepository.saveAll(etasToUpdate);
    }

    public CompletableFuture<EtaUpdateResult> calculateEtaAsync(Eta eta, Meeting meeting) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                long remainingMinutes = routeService.calculateOptimalRoute(
                        eta.getMate().getOrigin().getCoordinates(),
                        meeting.getTargetCoordinates()
                );
                return new EtaUpdateResult(eta, remainingMinutes, false);
            } catch (Exception e) {
                log.error("Failed to update ETA for mate: {}", eta.getMate().getId(), e);
                return new EtaUpdateResult(eta, 0, true);
            }
        });
    }

    public List<Eta> filterUpdatableEtas(List<Eta> etas) {
        return etas.stream()
                .filter(eta -> !eta.isArrived())
                .toList();
    }

    public EtaStatus findEtaStatus(Mate mate) {
        Eta eta = etaQueryService.findByMateId(mate.getId());
        return EtaStatus.of(eta, mate.getMeeting());
    }
}
