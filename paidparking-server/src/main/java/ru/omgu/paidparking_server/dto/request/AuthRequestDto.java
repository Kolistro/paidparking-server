package ru.omgu.paidparking_server.dto.request;

import ru.omgu.paidparking_server.validation.annotation.ValidPassword;
import ru.omgu.paidparking_server.validation.annotation.ValidPhoneNumber;

public record AuthRequestDto(
        @ValidPhoneNumber
        String phoneNumber,
        @ValidPassword
        String password
) {
}
