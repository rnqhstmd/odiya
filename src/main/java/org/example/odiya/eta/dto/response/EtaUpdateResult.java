package org.example.odiya.eta.dto.response;

import org.example.odiya.eta.domain.Eta;

public record EtaUpdateResult(
          Eta eta,
          long remainingMinutes,
          boolean isFailed
) {
}
