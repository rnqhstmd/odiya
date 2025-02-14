package org.example.odiya.eta.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.odiya.common.exception.NotFoundException;
import org.example.odiya.meeting.domain.Meeting;

import java.util.Arrays;
import java.util.function.BiPredicate;

import static org.example.odiya.common.exception.type.ErrorType.MATE_ETA_NOT_FOUND_ERROR;

@Slf4j
@Getter
public enum EtaStatus {
    MISSING((eta, meeting) -> eta.isMissing()),
    ARRIVED((eta, meeting) -> eta.isArrived()),
    ARRIVAL_EXPECTED((eta, meeting) -> eta.isArrivalSoon(meeting) && !meeting.isEnd()),
    LATE((eta, meeting) -> !eta.isArrivalSoon(meeting) && meeting.isEnd()),
    LATE_EXPECTED((eta, meeting) -> !eta.isArrivalSoon(meeting) && !meeting.isEnd());

    private final BiPredicate<Eta, Meeting> condition;

    EtaStatus(BiPredicate<Eta, Meeting> condition) {
        this.condition = condition;
    }

    public static EtaStatus of(Eta eta, Meeting meeting) {
        EtaStatus etaStatus = Arrays.stream(values())
                .filter(status -> status.condition.test(eta, meeting))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(MATE_ETA_NOT_FOUND_ERROR));

        if (etaStatus == EtaStatus.LATE) {
            log.info("[report_LATE_MATE] mate_id: {}, member_id: {}, meeting_id: {}",
                    eta.getMate().getId(),
                    eta.getMate().getMember().getId(),
                    meeting.getId()
            );
        }
        return etaStatus;
    }
}
