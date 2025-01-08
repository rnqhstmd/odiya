package org.example.odiya.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.odiya.common.annotation.FutureOrPresentDateTime;
import org.example.odiya.common.exception.InternalServerException;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Class<?> objectClass = object.getClass();
            Method dateGetter = objectClass.getMethod(dateFieldName);
            Method timeGetter = objectClass.getMethod(timeFieldName);

            LocalDate dateInput = (LocalDate) dateGetter.invoke(object);
            LocalTime timeInput = (LocalTime) timeGetter.invoke(object);

            LocalDateTime dateTimeInput = LocalDateTime.of(dateInput, timeInput);
            LocalDateTime now = LocalDateTime.now();

            if (dateInput.isEqual(now.toLocalDate())) {
                return dateTimeInput.isAfter(now);
            }
            return dateInput.isAfter(LocalDate.now());
        } catch (Exception e) {
            throw new InternalServerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
