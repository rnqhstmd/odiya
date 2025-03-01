package org.example.odiya.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.example.odiya.common.validator.FutureOrPresentDateTimeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FutureOrPresentDateTimeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentDateTime {

    String message() default "날짜와 시간은 현재 이후여야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String dateFieldName();

    String timeFieldName();
}
