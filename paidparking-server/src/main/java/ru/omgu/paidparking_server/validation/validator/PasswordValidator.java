package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.validation.annotation.ValidPassword;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return false;
        }

        // Проверка каждого условия отдельно
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[@$!%*?&].*");
        boolean hasMinLength = password.length() >= 8;


        //return PASSWORD_PATTERN.matcher(password).matches();
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar && hasMinLength;
    }
}
