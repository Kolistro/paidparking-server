package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.validation.annotation.ValidCarNumber;


import java.util.regex.Pattern;

public class CarNumberValidator implements ConstraintValidator<ValidCarNumber, String> {
    private static final Pattern RUSSIAN_CAR_NUMBER_PATTERN =
            Pattern.compile("^[АВЕКМНОРСТУХ]\\d{3}[АВЕКМНОРСТУХ]{2}\\d{2,3}$");

    @Override
    public boolean isValid(String carNumber, ConstraintValidatorContext context) {
        if (carNumber == null || carNumber.trim().isEmpty()) {
            return false;
        }
        return RUSSIAN_CAR_NUMBER_PATTERN.matcher(carNumber).matches();
    }
}
