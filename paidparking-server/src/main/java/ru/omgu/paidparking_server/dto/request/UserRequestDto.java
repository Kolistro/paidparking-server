package ru.omgu.paidparking_server.dto.request;

import ru.omgu.paidparking_server.validation.annotation.ValidName;
import ru.omgu.paidparking_server.validation.annotation.ValidPhoneNumber;

public record UserRequestDto(
        @ValidName
        String firstName,
        @ValidName
        String lastName,
        @ValidPhoneNumber
        String phoneNumber
) {
}
