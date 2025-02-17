package org.example.odiya.apicall.domain;

import lombok.Getter;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

@Getter
public enum ClientType {

    GOOGLE(date -> date.withDayOfMonth(1), 100),
    TMAP(date -> date.withDayOfMonth(1), 100),
    KAKAO(date -> date.withDayOfMonth(1), 100);
    ;

    private final UnaryOperator<LocalDate> resetDateOperation;
    private final int dailyLimit;

    ClientType(UnaryOperator<LocalDate> resetDateOperation, int dailyLimit) {
        this.resetDateOperation = resetDateOperation;
        this.dailyLimit = dailyLimit;
    }

    public LocalDate determineResetDate(LocalDate date) {
        return resetDateOperation.apply(date);
    }
}
