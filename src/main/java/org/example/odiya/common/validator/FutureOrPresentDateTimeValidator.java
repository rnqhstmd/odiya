package org.example.odiya.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.odiya.common.annotation.FutureOrPresentDateTime;
import org.example.odiya.common.exception.InternalServerException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.example.odiya.common.exception.type.ErrorType.INTERNAL_SERVER_ERROR;

public class FutureOrPresentDateTimeValidator implements ConstraintValidator<FutureOrPresentDateTime, Object> {

    private String dateFieldName;
    private String timeFieldName;

    @Override
    public void initialize(FutureOrPresentDateTime constraintAnnotation) {
        this.dateFieldName = constraintAnnotation.dateFieldName();
        this.timeFieldName = constraintAnnotation.timeFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Field dateField = value.getClass().getDeclaredField(dateFieldName);
            Field timeField = value.getClass().getDeclaredField(timeFieldName);
            dateField.setAccessible(true);
            timeField.setAccessible(true);

            LocalDate date = (LocalDate) dateField.get(value);
            LocalTime time = (LocalTime) timeField.get(value);

            if (date == null || time == null) {
                return true;
            }

            LocalDateTime dateTime = LocalDateTime.of(date, time);
            if (dateTime.isBefore(LocalDateTime.now())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "약속 날짜와 시간은 현재 이후여야 합니다. (선택된 시간: " +
                                dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ")"
                ).addConstraintViolation();
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
