package ru.omgu.paidparking_server.dto.response;

public record UserResponseDto(
    Long id,
    String firstName,
    String lastName,
    String phoneNumber
) {
}
