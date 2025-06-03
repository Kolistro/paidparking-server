package ru.omgu.paidparking_server.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.omgu.paidparking_server.validation.annotation.ValidCarNumber;
import ru.omgu.paidparking_server.validation.annotation.ValidName;
import ru.omgu.paidparking_server.validation.annotation.ValidPassword;
import ru.omgu.paidparking_server.validation.annotation.ValidPhoneNumber;

public record RegisterRequestDto(
        @ValidName String firstName,
        @ValidName String lastName,
        @ValidPhoneNumber @NotNull String phoneNumber,
        @ValidCarNumber String carNumber,
        @ValidPassword String password
) {
}
