package ru.omgu.paidparking_server.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.omgu.paidparking_server.dto.request.ReservationRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTimeValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_ShouldPassValidation_WhenReservationTimeIsValid() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusHours(2);
        ReservationRequestDto dto = new ReservationRequestDto(
                start,
                end,
                "А123ВС777",
                1L
        );

        // Act
        Set<ConstraintViolation<ReservationRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для корректного времени бронирования.");
    }

    @Test
    void isValid_ShouldFailValidation_WhenEndTimeIsBeforeStartTime() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.minusHours(1);
        ReservationRequestDto dto = new ReservationRequestDto(
                start,
                end,
                "А123ВС777",
                1L
        );

        // Act
        Set<ConstraintViolation<ReservationRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку, если время окончания раньше времени начала.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Время окончания должно быть как минимум на 1 час позже времени начала.", violationMessage);
    }

    @Test
    void isValid_ShouldFailValidation_WhenDurationIsLessThanOneHour() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusMinutes(30);
        ReservationRequestDto dto = new ReservationRequestDto(
                start,
                end,
                "А123ВС777",
                1L
        );

        // Act
        Set<ConstraintViolation<ReservationRequestDto>> violations = validator.validate(dto);

        // Assert
        assertFalse(violations.isEmpty(), "Валидация должна найти ошибку, если длительность бронирования меньше 1 часа.");
        assertEquals(1, violations.size());
        String violationMessage = violations.iterator().next().getMessage();
        assertEquals("Время окончания должно быть как минимум на 1 час позже времени начала.", violationMessage);
    }

    @Test
    void isValid_ShouldPassValidation_WhenStartAndEndAreNull() {
        // Arrange
        ReservationRequestDto dto = new ReservationRequestDto(
                null,
                null,
                "А123ВС777",
                1L
        );

        // Act
        Set<ConstraintViolation<ReservationRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно, если время начала и окончания равно null.");
    }

    @Test
    void isValid_ShouldPassValidation_WhenOnlyStartTimeIsNull() {
        // Arrange
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        ReservationRequestDto dto = new ReservationRequestDto(
                null,
                end,
                "А123ВС777",
                1L
        );

        // Act
        Set<ConstraintViolation<ReservationRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно, если время начала равно null.");
    }

    @Test
    void isValid_ShouldPassValidation_WhenOnlyEndTimeIsNull() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        ReservationRequestDto dto = new ReservationRequestDto(
                start,
                null,
                "А123ВС777",
                1L
        );

        // Act
        Set<ConstraintViolation<ReservationRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно, если время окончания равно null.");
    }
}