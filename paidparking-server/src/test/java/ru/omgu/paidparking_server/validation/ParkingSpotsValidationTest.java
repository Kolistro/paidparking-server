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

class ParkingSpotsValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenParkingSpotsAreValid() {
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
    void isValid_ShouldFailValidation_WhenTotalParkingSpotsIsNegative() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                -10L,
                30L,
                100L,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для отрицательного общего количества мест.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Недопустимые парковочные места: количество доступных мест должно быть <= общему числу " +
                "и оба должны быть неотрицательными", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenAvailableParkingSpotsIsNegative() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                50L,
                -5L,
                100L,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку для отрицательного доступного количества мест.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Недопустимые парковочные места: количество доступных мест должно быть <= общему числу " +
                "и оба должны быть неотрицательными", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenAvailableExceedsTotal() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                50L,
                60L,
                100L,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку, если доступных мест больше общих.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Недопустимые парковочные места: количество доступных мест должно быть <= общему числу " +
                "и оба должны быть неотрицательными", violationMessage);
    }

    @Test
    void isValid_ShouldPassValidation_WhenParkingSpotsAreNull() {
        // Arrange
        BuildingRequestDto dto = new BuildingRequestDto(
                "Офисный центр",
                "ул. Ленина, 10",
                null,
                null,
                100L,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0)
        );

        // Act
        Set<ConstraintViolation<BuildingRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно, если поля парковочных мест равны null.");
    }
}