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

class NameValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenNameIsValid() {
        // Arrange
        String validName = "John";
        UserRequestDto dto = new UserRequestDto(validName, "Doe", "+79991234567");

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректного имени.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenFirstNameContainsInvalidCharacters() {
        // Arrange
        String invalidName = "John123";
        UserRequestDto dto = new UserRequestDto(invalidName, "Doe", "+79991234567");

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для имени с недопустимыми символами.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Имя должно содержать только буквы латинского или русского алфавита и не может быть пустым.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenFirstNameIsEmpty() {
        // Arrange
        String emptyName = "";
        UserRequestDto dto = new UserRequestDto(emptyName, "Doe", "+79991234567");

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пустого имени.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Имя должно содержать только буквы латинского или русского алфавита и не может быть пустым.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenFirstNameIsNull() {
        // Arrange
        String nullName = null;
        UserRequestDto dto = new UserRequestDto(nullName, "Doe", "+79991234567");

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для null-значения.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Имя должно содержать только буквы латинского или русского алфавита и не может быть пустым.", violationMessage);
    }

    @Test
    void isValid_ShouldPassValidation_WhenLastNameIsValid() {
        // Arrange
        String validLastName = "Doe";
        UserRequestDto dto = new UserRequestDto("John", validLastName, "+79991234567");

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректной фамилии.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenLastNameContainsInvalidCharacters() {
        // Arrange
        String invalidLastName = "Doe123";
        UserRequestDto dto = new UserRequestDto("John", invalidLastName, "+79991234567");

        // Act
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для фамилии с недопустимыми символами.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Имя должно содержать только буквы латинского или русского алфавита и не может быть пустым.", violationMessage);
    }
}