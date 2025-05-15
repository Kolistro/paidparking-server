package ru.omgu.paidparking_server.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.PasswordValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Пароль должен содержать как минимум одну заглавную букву, " +
            "одну строчную букву, одну цифру и один специальный символ.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
