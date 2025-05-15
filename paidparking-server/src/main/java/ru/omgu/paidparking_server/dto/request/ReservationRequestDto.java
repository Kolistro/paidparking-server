package ru.omgu.paidparking_server.dto.request;

import ru.omgu.paidparking_server.validation.annotation.ValidReservationTime;

import java.time.LocalDateTime;

@ValidReservationTime
public record ReservationRequestDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String carNumber,
        Long buildingId
) {
}
