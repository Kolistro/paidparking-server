package ru.omgu.paidparking_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.omgu.paidparking_server.validation.annotation.ValidParkingSpots;
import ru.omgu.paidparking_server.validation.annotation.ValidWorkingHours;

import java.time.LocalTime;

@ValidWorkingHours
@ValidParkingSpots
public record BuildingRequestDto(

        @Size(max = 100, message = "Название объекта не должно превышать 100 символов.")
        String locationName,
        @NotBlank(message = "Адрес не может быть пустым")
        @Size(min = 5, max = 255, message = "Адрес должен содержать от 5 до 255 символов.")
        String address,
        Long totalParkingSpots,
        Long availableParkingSpots,
        Long costPerHour,
        LocalTime workingHoursStart,
        LocalTime workingHoursEnd
) {
}
