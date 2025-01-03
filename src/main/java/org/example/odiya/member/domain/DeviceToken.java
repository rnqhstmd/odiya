package org.example.odiya.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {

    @Column(name = "deviceToken")
    private String value;

    public DeviceToken(String value) {
        this.value = value;
    }
}