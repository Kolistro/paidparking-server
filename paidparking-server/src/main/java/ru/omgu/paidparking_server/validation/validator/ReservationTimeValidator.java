package ru.omgu.paidparking_server.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.omgu.paidparking_server.entity.ReservationEntity;
import ru.omgu.paidparking_server.validation.annotation.ValidReservationTime;

import java.time.Duration;
import java.time.LocalDateTime;

public class ReservationTimeValidator implements ConstraintValidator<ValidReservationTime, ReservationEntity> {

    @Override
    public boolean isValid(ReservationEntity reservation, ConstraintValidatorContext context) {
        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();

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