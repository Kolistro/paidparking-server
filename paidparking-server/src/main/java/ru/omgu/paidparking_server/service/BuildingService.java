package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.dto.response.BuildingResponseDto;
import ru.omgu.paidparking_server.entity.BuildingEntity;
import ru.omgu.paidparking_server.exception.BuildingAlreadyExistsException;
import ru.omgu.paidparking_server.exception.BuildingNotFoundException;
import ru.omgu.paidparking_server.mapper.BuildingMapper;
import ru.omgu.paidparking_server.repository.BuildingRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {
    private final BuildingRepo buildingRepo;
    private final BuildingMapper buildingMapper;

    public BuildingResponseDto addBuilding(BuildingRequestDto building){
        if(buildingRepo.existsByLocationName(building.locationName()))
            throw new BuildingAlreadyExistsException("Здание с названием локации "
                    + building.locationName() + " уже существует.");

        BuildingEntity buildingEntity = buildingMapper.toEntity(building);
        buildingRepo.save(buildingEntity);
        return buildingMapper.toDto(buildingEntity);
    }

    public BuildingResponseDto editBuilding(BuildingRequestDto building){
        BuildingEntity buildingEntity = buildingRepo.findByLocationName(building.locationName())
                .orElseThrow(() -> new BuildingNotFoundException("Здание с названием локации "
                        + building.locationName() + " не существует"));
        buildingEntity.setAddress(buildingEntity.getAddress());
        buildingEntity.setCostPerHour(buildingEntity.getCostPerHour());
        buildingEntity.setAvailableParkingSpots(buildingEntity.getAvailableParkingSpots());
        buildingEntity.setTotalParkingSpots(buildingEntity.getTotalParkingSpots());
        buildingEntity.setWorkingHoursStart(building.workingHoursStart());
        buildingEntity.setWorkingHoursEnd(building.workingHoursEnd());
        buildingRepo.save(buildingEntity);
        return buildingMapper.toDto(buildingEntity);
    }

    public List<BuildingResponseDto> getListBuildings(){
        List<BuildingEntity> buildings = buildingRepo.findAll();
        return buildingMapper.toDto(buildings);
    }

    public BuildingResponseDto getBuildingByLocationName(String locationName){
        BuildingEntity buildingEntity = buildingRepo.findByLocationName(locationName)
                .orElseThrow(() -> new BuildingNotFoundException("Здание с названием локации "
                        + locationName + " не существует"));
        return buildingMapper.toDto(buildingEntity);
    }

    public Boolean isAvailableParkingSpots(Long id){
        BuildingEntity buildingEntity = buildingRepo.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException("Здание с id = " + id + " не существует"));
        return buildingEntity.getAvailableParkingSpots() > 0;
    }

    public Long getCountAvailableParkingSpots(Long id){
        BuildingEntity buildingEntity = buildingRepo.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException("Здание с id = " + id + " не существует"));
        return buildingEntity.getAvailableParkingSpots();
    }

    public void delete(Long id){
        BuildingEntity buildingEntity = buildingRepo.findById(id)
                .orElseThrow(() -> new BuildingNotFoundException("Здание с id = " + id + " не существует"));
        buildingRepo.delete(buildingEntity);
    }
}
