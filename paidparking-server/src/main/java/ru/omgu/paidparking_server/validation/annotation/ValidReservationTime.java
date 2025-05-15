package ru.omgu.paidparking_server.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.ReservationTimeValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReservationTimeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReservationTime {
    String message() default "Время окончания должно быть как минимум на 1 час позже времени начала.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}