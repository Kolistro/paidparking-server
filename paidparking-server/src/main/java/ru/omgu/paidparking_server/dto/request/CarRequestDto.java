package ru.omgu.paidparking_server.dto.request;

import ru.omgu.paidparking_server.enums.CarColor;
import ru.omgu.paidparking_server.validation.annotation.ValidCarNumber;

public record CarRequestDto(
        @ValidCarNumber
        String carNumber,
        String brand,
        String model,
        CarColor color
) {
}
