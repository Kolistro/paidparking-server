package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.validation.annotation.ValidPhoneNumber;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+7\\d{10}$");

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        phone = phone.trim();
        return PHONE_PATTERN.matcher(phone).matches();
    }
}