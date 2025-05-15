package ru.omgu.paidparking_server.mapper;

import org.mapstruct.Mapper;
import ru.omgu.paidparking_server.dto.response.RoleResponseDto;
import ru.omgu.paidparking_server.entity.RoleEntity;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponseDto toDto(RoleEntity role);
    List<RoleResponseDto> toDto(List<RoleEntity> roles);
    Set<RoleResponseDto> toDto(Set<RoleEntity> roles);
}


