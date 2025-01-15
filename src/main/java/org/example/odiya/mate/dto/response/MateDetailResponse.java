package org.example.odiya.mate.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.odiya.mate.domain.Mate;

@Getter
@NoArgsConstructor
public class MateDetailResponse {

    private Long id;
    private String originAddress;

    public MateDetailResponse(Mate mate) {
        this.id = mate.getId();
        this.originAddress = mate.getOrigin().getAddress();
    }
}
