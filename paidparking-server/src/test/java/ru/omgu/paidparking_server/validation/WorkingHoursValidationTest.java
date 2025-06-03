package ru.omgu.paidparking_server.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;

import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WorkingHoursValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenWorkingHoursAreValid() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                50L,
                30L,
                100L,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректных данных.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenWorkingHoursEndIsBeforeStart() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                50L,
                30L,
                100L,
                LocalTime.of(22, 0),
                LocalTime.of(8, 0)
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку, если конец рабочего времени раньше начала.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Конец рабочего времени должно быть позже начала рабочего времени.", violationMessage);
    }

    @Test
    void isValid_ShouldPassValidation_WhenWorkingHoursAreNull() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                50L,
                30L,
                100L,
                null,
                null
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно, если время работы равно null.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenWorkingHoursStartIsNullAndEndIsNotNull() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                50L,
                30L,
                100L,
                null,
                LocalTime.of(22, 0)
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно, если начало рабочего времени равно null.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenWorkingHoursEndIsNullAndStartIsNotNull() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                50L,
                30L,
                100L,
                LocalTime.of(8, 0),
                null
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно, если конец рабочего времени равно null.");
    }
}