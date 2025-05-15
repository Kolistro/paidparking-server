package ru.omgu.paidparking_server.dto.response;

import java.time.LocalTime;

public record BuildingResponseDto(
        Long id,
        String locationName,
        String address,
        Long totalParkingSpots,
        Long availableParkingSpots,
        Long costPerHour,
        LocalTime workingHoursStart,
        LocalTime workingHoursEnd
) {
}
