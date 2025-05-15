package ru.omgu.paidparking_server.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.WorkingHoursValidator;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WorkingHoursValidator.class)
@Documented
public @interface ValidWorkingHours {
    String message() default "Конец рабочего времени должно быть позже начала рабочего времени.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
