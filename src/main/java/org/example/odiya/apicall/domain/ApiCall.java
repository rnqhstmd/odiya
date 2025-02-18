package org.example.odiya.apicall.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.odiya.common.domain.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiCall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType clientType;

    @Column(nullable = false)
    private LocalDate date;

    @Column
    private Integer count;

    @Column
    @Builder.Default
    private Boolean enabled = true;

    public ApiCall(ClientType clientType, Integer count, LocalDate date) {
        this(null, clientType, date, count, true);
    }

    public ApiCall(ClientType clientType, LocalDate today, int count, boolean enabled) {
        this(null, clientType, today, count, enabled);
    }

    public void increaseCount() {
        count++;
    }

    public void markAsDisabled() {
        this.enabled = false;
    }
}
