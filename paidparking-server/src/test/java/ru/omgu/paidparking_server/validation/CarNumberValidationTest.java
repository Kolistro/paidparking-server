package ru.omgu.paidparking_server.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.omgu.paidparking_server.dto.request.CarRequestDto;
import ru.omgu.paidparking_server.enums.CarColor;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CarNumberValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenCarNumberIsValid() {
        // Arrange
        String validCarNumber = "А123ВС777";
        CarRequestDto dto = new CarRequestDto(validCarNumber, "Toyota", "Camry", CarColor.BLACK);

        // Act
        Set<ConstraintViolation<CarRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректного номера.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenCarNumberIsInvalid() {
        // Arrange
        String invalidCarNumber = "INVALID123";
        CarRequestDto dto = new CarRequestDto(invalidCarNumber, "Toyota", "Camry", CarColor.BLACK);

        // Act
        Set<ConstraintViolation<CarRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для некорректного номера.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Неверный формат номера автомобиля. Пример: А123ВС777.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenCarNumberIsEmpty() {
        // Arrange
        String emptyCarNumber = "";
        CarRequestDto dto = new CarRequestDto(emptyCarNumber, "Toyota", "Camry", CarColor.BLACK);

        // Act
        Set<ConstraintViolation<CarRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для пустого номера.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Неверный формат номера автомобиля. Пример: А123ВС777.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenCarNumberIsNull() {
        // Arrange
        String nullCarNumber = null;
        CarRequestDto dto = new CarRequestDto(nullCarNumber, "Toyota", "Camry", CarColor.BLACK);

        // Act
        Set<ConstraintViolation<CarRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для null-значения.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Неверный формат номера автомобиля. Пример: А123ВС777.", violationMessage);
    }
}