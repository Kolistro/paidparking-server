package ru.omgu.paidparking_server.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.PhoneNumberValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Номер телефона должен начинаться с '+7' и содержать 10 цифр " +
            "после него (например, +79991234567).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
