package ru.omgu.paidparking_server.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.omgu.paidparking_server.dto.request.UserRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenPhoneNumberIsValid() {
        // Arrange
        String validPhoneNumber = "+79991234567";
        UserRequestDto dto = new UserRequestDto("John", "Doe", validPhoneNumber);

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректного номера телефона.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenPhoneNumberIsInvalid() {
        // Arrange
        String invalidPhoneNumber = "INVALID_PHONE";
        UserRequestDto dto = new UserRequestDto("John", "Doe", invalidPhoneNumber);

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для некорректного номера телефона.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Номер телефона должен начинаться с '+7' и содержать 10 цифр после него (например, +79991234567).", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPhoneNumberIsEmpty() {
        // Arrange
        String emptyPhoneNumber = "";
        UserRequestDto dto = new UserRequestDto("John", "Doe", emptyPhoneNumber);

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пустого номера телефона.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Номер телефона должен начинаться с '+7' и содержать 10 цифр после него (например, +79991234567).", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenPhoneNumberIsNull() {
        // Arrange
        String nullPhoneNumber = null;
        UserRequestDto dto = new UserRequestDto("John", "Doe", nullPhoneNumber);

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для null-значения.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Номер телефона должен начинаться с '+7' и содержать 10 цифр после него (например, +79991234567).", violationMessage);
    }
}