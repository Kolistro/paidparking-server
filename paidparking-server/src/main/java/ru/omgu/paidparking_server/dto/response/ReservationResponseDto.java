package ru.omgu.paidparking_server.dto.response;

import ru.omgu.paidparking_server.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponseDto(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status
) {
}
