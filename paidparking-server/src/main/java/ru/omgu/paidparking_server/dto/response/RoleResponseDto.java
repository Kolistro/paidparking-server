package ru.omgu.paidparking_server.dto.response;

import ru.omgu.paidparking_server.enums.Role;

public record RoleResponseDto(
        Long id,
        Role role
) {
}
