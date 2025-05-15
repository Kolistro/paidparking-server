package ru.omgu.paidparking_server.validation.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.CarNumberValidator;


import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CarNumberValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCarNumber {
    String message() default "Неверный формат номера автомобиля. Пример: А123ВС777.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
