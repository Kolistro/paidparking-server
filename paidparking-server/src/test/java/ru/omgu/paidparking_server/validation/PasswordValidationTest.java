package ru.omgu.paidparking_server.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.omgu.paidparking_server.dto.request.AuthRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenPasswordIsValid() {
        // Arrange
        String validPassword = "Password1!";
        AuthRequestDto dto = new AuthRequestDto("+79991234567", validPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректного пароля.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenPasswordIsTooShort() {
        // Arrange
        String invalidPassword = "Pass1!";
        AuthRequestDto dto = new AuthRequestDto("+79991234567", invalidPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для слишком короткого пароля.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Пароль должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPasswordHasNoUpperCase() {
        // Arrange
        String invalidPassword = "password1!";
        AuthRequestDto dto = new AuthRequestDto("+79991234567", invalidPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пароля без заглавной буквы.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Пароль должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPasswordHasNoLowerCase() {
        // Arrange
        String invalidPassword = "PASSWORD1!";
        AuthRequestDto dto = new AuthRequestDto("+79991234567", invalidPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пароля без строчной буквы.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Пароль должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPasswordHasNoDigit() {
        // Arrange
        String invalidPassword = "Password!";
        AuthRequestDto dto = new AuthRequestDto("+79991234567", invalidPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пароля без цифры.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Пароль должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPasswordHasNoSpecialChar() {
        // Arrange
        String invalidPassword = "Password1";
        AuthRequestDto dto = new AuthRequestDto("+79991234567", invalidPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пароля без специального символа.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Пароль должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPasswordIsEmpty() {
        // Arrange
        String emptyPassword = "";
        AuthRequestDto dto = new AuthRequestDto("+79991234567", emptyPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пустого пароля.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Пароль должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPasswordIsNull() {
        // Arrange
        String nullPassword = null;
        AuthRequestDto dto = new AuthRequestDto("+79991234567", nullPassword);

        // Act
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для null-значения.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Пароль должен содержать как минимум одну заглавную букву, одну строчную букву, одну цифру и один специальный символ.", violationMessage);
    }
}