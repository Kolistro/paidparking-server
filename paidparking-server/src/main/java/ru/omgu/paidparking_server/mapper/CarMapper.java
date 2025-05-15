package ru.omgu.paidparking_server.mapper;

import org.mapstruct.Mapper;
import ru.omgu.paidparking_server.dto.request.CarRequestDto;
import ru.omgu.paidparking_server.dto.response.CarResponseDto;
import ru.omgu.paidparking_server.entity.CarEntity;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarEntity toEntity(CarRequestDto car);
    CarResponseDto toDto(CarEntity carEntity);
    Set<CarResponseDto> toDto(Set<CarEntity> cars);
}
