package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.validation.annotation.ValidName;

public class NameValidator implements ConstraintValidator<ValidName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return value.matches("^[A-Za-zА-Яа-я]+$");
    }
}
