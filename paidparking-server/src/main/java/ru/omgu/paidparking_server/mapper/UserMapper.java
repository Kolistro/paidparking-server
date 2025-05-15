package ru.omgu.paidparking_server.mapper;

import org.mapstruct.Mapper;
import ru.omgu.paidparking_server.dto.response.UserResponseDto;
import ru.omgu.paidparking_server.entity.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(UserEntity user);
    List<UserResponseDto> toDto(List<UserEntity> users);
}
