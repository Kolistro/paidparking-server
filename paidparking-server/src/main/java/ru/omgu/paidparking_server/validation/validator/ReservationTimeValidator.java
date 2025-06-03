package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.dto.request.ReservationRequestDto;
import ru.omgu.paidparking_server.validation.annotation.ValidReservationTime;

import java.time.Duration;
import java.time.LocalDateTime;

public class ReservationTimeValidator implements ConstraintValidator<ValidReservationTime, ReservationRequestDto> {

    @Override
    public boolean isValid(ReservationRequestDto reservation, ConstraintValidatorContext context) {
        LocalDateTime start = reservation.startTime();
        LocalDateTime end = reservation.endTime();

        if (start == null || end == null) {
            return true; // Обрабатывается отдельно аннотациями @NotNull
        }

        if (!end.isAfter(start)) {
            return false;
        }

        Duration duration = Duration.between(start, end);
        return duration.toHours() >= 1;
    }
}