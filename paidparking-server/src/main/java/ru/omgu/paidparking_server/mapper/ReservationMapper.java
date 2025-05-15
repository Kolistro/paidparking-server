package ru.omgu.paidparking_server.mapper;

import org.mapstruct.Mapper;
import ru.omgu.paidparking_server.dto.response.ReservationResponseDto;
import ru.omgu.paidparking_server.entity.ReservationEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationResponseDto toDto(ReservationEntity reservation);

    List<ReservationResponseDto> toDto(List<ReservationEntity> reservations);
}
