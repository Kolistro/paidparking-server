package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.omgu.paidparking_server.dto.request.BuildingRequestDto;
import ru.omgu.paidparking_server.dto.response.BuildingResponseDto;
import ru.omgu.paidparking_server.entity.BuildingEntity;
import ru.omgu.paidparking_server.exception.BuildingAlreadyExistsException;
import ru.omgu.paidparking_server.exception.BuildingNotFoundException;
import ru.omgu.paidparking_server.mapper.BuildingMapper;
import ru.omgu.paidparking_server.repository.BuildingRepo;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    @Mock
    private BuildingRepo buildingRepo;

    @Mock
    private BuildingMapper buildingMapper;

    @InjectMocks
    private BuildingService buildingService;

    @Test
    void addBuilding_ShouldAddBuilding_WhenBuildingDoesNotExist() {
        // Arrange
        BuildingRequestDto requestDto = new BuildingRequestDto(
                "Main Building", "123 Main St", 10L, 10L, 50L, LocalTime.of(8, 0), LocalTime.of(18, 0)
        );
        BuildingEntity entity = new BuildingEntity();
        entity.setId(1L);
        entity.setLocationName("Main Building");
        entity.setAddress("123 Main St");
        entity.setTotalParkingSpots(10L);
        entity.setAvailableParkingSpots(10L);
        entity.setCostPerHour(50L);
        entity.setWorkingHoursStart(LocalTime.of(8, 0));
        entity.setWorkingHoursEnd(LocalTime.of(18, 0));

        when(buildingRepo.existsByLocationName("Main Building")).thenReturn(false);
        when(buildingMapper.toEntity(requestDto)).thenReturn(entity);
        when(buildingMapper.toDto(entity)).thenReturn(new BuildingResponseDto(
                1L, "Main Building", "123 Main St", 10L, 10L, 50L, LocalTime.of(8, 0), LocalTime.of(18, 0)
        ));

        // Act
        BuildingResponseDto responseDto = buildingService.addBuilding(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("Main Building", responseDto.locationName());
        assertEquals("123 Main St", responseDto.address());
        assertEquals(10L, responseDto.totalParkingSpots());
        assertEquals(10L, responseDto.availableParkingSpots());
        assertEquals(50L, responseDto.costPerHour());
        assertEquals(LocalTime.of(8, 0), responseDto.workingHoursStart());
        assertEquals(LocalTime.of(18, 0), responseDto.workingHoursEnd());
        verify(buildingRepo, times(1)).save(entity);
    }

    @Test
    void addBuilding_ShouldThrowException_WhenBuildingAlreadyExists() {
        // Arrange
        BuildingRequestDto requestDto = new BuildingRequestDto(
                "Main Building", "123 Main St", 10L, 10L, 50L, LocalTime.of(8, 0), LocalTime.of(18, 0)
        );

        when(buildingRepo.existsByLocationName("Main Building")).thenReturn(true);

        // Act & Assert
        BuildingAlreadyExistsException exception = assertThrows(
                BuildingAlreadyExistsException.class,
                () -> buildingService.addBuilding(requestDto)
        );
        assertEquals("Здание с названием локации Main Building уже существует.", exception.getMessage());
    }

    @Test
    void editBuilding_ShouldEditBuilding_WhenBuildingExists() {
        // Arrange
        BuildingRequestDto requestDto = new BuildingRequestDto(
                "Main Building", "456 New St", 15L, 5L, 60L, LocalTime.of(9, 0), LocalTime.of(17, 0)
        );
        BuildingEntity entity = new BuildingEntity();
        entity.setId(1L);
        entity.setLocationName("Main Building");
        entity.setAddress("123 Main St");

        when(buildingRepo.findByLocationName("Main Building")).thenReturn(Optional.of(entity));
        when(buildingMapper.toDto(entity)).thenReturn(new BuildingResponseDto(
                1L, "Main Building", "456 New St", 15L, 5L, 60L, LocalTime.of(9, 0), LocalTime.of(17, 0)
        ));

        // Act
        BuildingResponseDto responseDto = buildingService.editBuilding(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals("456 New St", responseDto.address());
        assertEquals(15L, responseDto.totalParkingSpots());
        assertEquals(5L, responseDto.availableParkingSpots());
        assertEquals(60L, responseDto.costPerHour());
        assertEquals(LocalTime.of(9, 0), responseDto.workingHoursStart());
        assertEquals(LocalTime.of(17, 0), responseDto.workingHoursEnd());
        verify(buildingRepo, times(1)).save(entity);
    }

    @Test
    void editBuilding_ShouldThrowException_WhenBuildingNotFound() {
        // Arrange
        BuildingRequestDto requestDto = new BuildingRequestDto(
                "Nonexistent Building", "456 New St", 15L, 5L, 60L, LocalTime.of(9, 0), LocalTime.of(17, 0)
        );

        when(buildingRepo.findByLocationName("Nonexistent Building")).thenReturn(Optional.empty());

        // Act & Assert
        BuildingNotFoundException exception = assertThrows(
                BuildingNotFoundException.class,
                () -> buildingService.editBuilding(requestDto)
        );
        assertEquals("Здание с названием локации Nonexistent Building не существует", exception.getMessage());
    }

    @Test
    void getListBuildings_ShouldReturnAllBuildings() {
        // Arrange
        BuildingEntity entity1 = new BuildingEntity();
        entity1.setId(1L);
        entity1.setLocationName("Building A");
        entity1.setAddress("Address A");

        BuildingEntity entity2 = new BuildingEntity();
        entity2.setId(2L);
        entity2.setLocationName("Building B");
        entity2.setAddress("Address B");

        List<BuildingEntity> entities = List.of(entity1, entity2);

        when(buildingRepo.findAll()).thenReturn(entities);
        when(buildingMapper.toDto(entities)).thenReturn(List.of(
                new BuildingResponseDto(1L, "Building A", "Address A", 10L, 5L, 50L, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                new BuildingResponseDto(2L, "Building B", "Address B", 15L, 10L, 60L, LocalTime.of(9, 0), LocalTime.of(17, 0))
        ));

        // Act
        List<BuildingResponseDto> responseDtos = buildingService.getListBuildings();

        // Assert
        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());
        assertEquals("Building A", responseDtos.get(0).locationName());
        assertEquals("Building B", responseDtos.get(1).locationName());
    }

    @Test
    void getListBuildings_ShouldReturnEmptyList_WhenNoBuildingsExist() {
        // Arrange
        when(buildingRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<BuildingResponseDto> responseDtos = buildingService.getListBuildings();

        // Assert
        assertNotNull(responseDtos);
        assertTrue(responseDtos.isEmpty());
    }

    @Test
    void getBuildingByLocationName_ShouldReturnBuilding_WhenBuildingExists() {
        // Arrange
        String locationName = "Main Building";
        BuildingEntity entity = new BuildingEntity();
        entity.setId(1L);
        entity.setLocationName(locationName);
        entity.setAddress("123 Main St");

        when(buildingRepo.findByLocationName(locationName)).thenReturn(Optional.of(entity));
        when(buildingMapper.toDto(entity)).thenReturn(new BuildingResponseDto(
                1L, locationName, "123 Main St", 10L, 5L, 50L, LocalTime.of(8, 0), LocalTime.of(18, 0)
        ));

        // Act
        BuildingResponseDto responseDto = buildingService.getBuildingByLocationName(locationName);

        // Assert
        assertNotNull(responseDto);
        assertEquals(locationName, responseDto.locationName());
        assertEquals("123 Main St", responseDto.address());
    }

    @Test
    void getBuildingByLocationName_ShouldThrowException_WhenBuildingNotFound() {
        // Arrange
        String locationName = "Nonexistent Building";

        when(buildingRepo.findByLocationName(locationName)).thenReturn(Optional.empty());

        // Act & Assert
        BuildingNotFoundException exception = assertThrows(
                BuildingNotFoundException.class,
                () -> buildingService.getBuildingByLocationName(locationName)
        );
        assertEquals("Здание с названием локации Nonexistent Building не существует", exception.getMessage());
    }

    @Test
    void isAvailableParkingSpots_ShouldReturnTrue_WhenAvailableSpotsExist() {
        // Arrange
        Long id = 1L;
        BuildingEntity entity = new BuildingEntity();
        entity.setId(id);
        entity.setAvailableParkingSpots(5L);

        when(buildingRepo.findById(id)).thenReturn(Optional.of(entity));

        // Act
        Boolean result = buildingService.isAvailableParkingSpots(id);

        // Assert
        assertTrue(result);
    }

    @Test
    void isAvailableParkingSpots_ShouldReturnFalse_WhenNoAvailableSpots() {
        // Arrange
        Long id = 1L;
        BuildingEntity entity = new BuildingEntity();
        entity.setId(id);
        entity.setAvailableParkingSpots(0L);

        when(buildingRepo.findById(id)).thenReturn(Optional.of(entity));

        // Act
        Boolean result = buildingService.isAvailableParkingSpots(id);

        // Assert
        assertFalse(result);
    }

    @Test
    void isAvailableParkingSpots_ShouldThrowException_WhenBuildingNotFound() {
        // Arrange
        Long id = 999L;

        when(buildingRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        BuildingNotFoundException exception = assertThrows(
                BuildingNotFoundException.class,
                () -> buildingService.isAvailableParkingSpots(id)
        );
        assertEquals("Здание с id = 999 не существует", exception.getMessage());
    }

    @Test
    void getCountAvailableParkingSpots_ShouldReturnCorrectCount_WhenBuildingExists() {
        // Arrange
        Long id = 1L;
        BuildingEntity entity = new BuildingEntity();
        entity.setId(id);
        entity.setAvailableParkingSpots(5L);

        when(buildingRepo.findById(id)).thenReturn(Optional.of(entity));

        // Act
        Long result = buildingService.getCountAvailableParkingSpots(id);

        // Assert
        assertEquals(5L, result);
    }

    @Test
    void getCountAvailableParkingSpots_ShouldThrowException_WhenBuildingNotFound() {
        // Arrange
        Long id = 999L;

        when(buildingRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        BuildingNotFoundException exception = assertThrows(
                BuildingNotFoundException.class,
                () -> buildingService.getCountAvailableParkingSpots(id)
        );
        assertEquals("Здание с id = 999 не существует", exception.getMessage());
    }

    @Test
    void delete_ShouldDeleteBuilding_WhenBuildingExists() {
        // Arrange
        Long id = 1L;
        BuildingEntity entity = new BuildingEntity();
        entity.setId(id);

        when(buildingRepo.findById(id)).thenReturn(Optional.of(entity));

        // Act
        buildingService.delete(id);

        // Assert
        verify(buildingRepo, times(1)).delete(entity);
    }

    @Test
    void delete_ShouldThrowException_WhenBuildingNotFound() {
        // Arrange
        Long id = 999L;

        when(buildingRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        BuildingNotFoundException exception = assertThrows(
                BuildingNotFoundException.class,
                () -> buildingService.delete(id)
        );
        assertEquals("Здание с id = 999 не существует", exception.getMessage());
    }
}