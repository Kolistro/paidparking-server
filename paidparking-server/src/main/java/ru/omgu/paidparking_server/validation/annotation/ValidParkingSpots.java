package ru.omgu.paidparking_server.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.ParkingSpotsValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ParkingSpotsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidParkingSpots {
    String message() default "Недопустимые парковочные места: количество доступных мест должно быть <= общему числу " +
            "и оба должны быть неотрицательными";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}