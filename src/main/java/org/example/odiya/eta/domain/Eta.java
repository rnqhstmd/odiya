package org.example.odiya.eta.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.odiya.common.domain.BaseEntity;
import org.example.odiya.mate.domain.Mate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Eta extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "mate_id")
    private Mate mate;

    @Column(nullable = false)
    private long remainingMinutes;

    @Builder.Default
    @Column(nullable = false)
    private boolean isArrived = false;

    public void updateRemainingMinutes(long remainingMinutes) {
        this.remainingMinutes = remainingMinutes;
    }

    public void markAsArrived() {
        this.isArrived = true;
    }
}
