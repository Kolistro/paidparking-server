package ru.omgu.paidparking_server.validation.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.omgu.paidparking_server.validation.validator.ReceiptFileValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReceiptFileValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReceiptFile {

    String message() default "Недопустимый файл чека. Разрешены только JPG, PNG и PDF, размер до 5 МБ.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}