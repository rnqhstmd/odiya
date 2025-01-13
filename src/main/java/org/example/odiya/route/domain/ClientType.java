package org.example.odiya.route.domain;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

public enum ClientType {

    GOOGLE(date -> date.withDayOfMonth(1)),
    ;

    private final UnaryOperator<LocalDate> resetDateOperation;

    ClientType(UnaryOperator<LocalDate> resetDateOperation) {
        this.resetDateOperation = resetDateOperation;
    }

    public LocalDate determineResetDate(LocalDate date) {
        return resetDateOperation.apply(date);
    }
}
