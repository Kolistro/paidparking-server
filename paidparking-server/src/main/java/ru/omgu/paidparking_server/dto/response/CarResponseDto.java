package ru.omgu.paidparking_server.dto.response;

import ru.omgu.paidparking_server.enums.CarColor;
import ru.omgu.paidparking_server.validation.annotation.ValidCarNumber;

public record CarResponseDto(
        Long id,
        String carNumber,
        String brand,
        String model,
        CarColor color
) {
}
