package ru.omgu.paidparking_server.mapper;

import org.mapstruct.Mapper;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.dto.response.BuildingResponseDto;
import ru.omgu.paidparking_server.entity.BuildingEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BuildingMapper {
    BuildingEntity toEntity(BuildingRequestDto building);
    BuildingResponseDto toDto(BuildingEntity buildingEntity);
    List<BuildingResponseDto> toDto(List<BuildingEntity> buildings);
}
