package ru.omgu.paidparking_server.validation.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.NameValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName {
    String message() default "Имя должно содержать только буквы латинского " +
            "или русского алфавита и не может быть пустым.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
