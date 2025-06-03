package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.omgu.paidparking_server.dto.request.CarRequestDto;
import ru.omgu.paidparking_server.dto.response.CarResponseDto;
import ru.omgu.paidparking_server.entity.CarEntity;
import ru.omgu.paidparking_server.entity.UserEntity;
import ru.omgu.paidparking_server.enums.CarColor;
import ru.omgu.paidparking_server.exception.CarNotFoundException;
import ru.omgu.paidparking_server.exception.UserNotFoundException;
import ru.omgu.paidparking_server.mapper.CarMapper;
import ru.omgu.paidparking_server.repository.CarRepo;
import ru.omgu.paidparking_server.repository.UserRepo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepo carRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarService carService;

    @Test
    void addCar_ShouldAddNewCar_WhenCarDoesNotExist() {
        // Arrange
        Long userId = 1L;
        CarRequestDto requestDto = new CarRequestDto("A123BC77", "Toyota", "Camry", CarColor.WHITE);

        UserEntity user = new UserEntity();
        user.setId(userId);

        CarEntity carEntity = new CarEntity();
        carEntity.setId(1L);
        carEntity.setCarNumber("A123BC77");

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carRepo.findByCarNumber("A123BC77")).thenReturn(Optional.empty());
        when(carMapper.toEntity(requestDto)).thenReturn(carEntity);
        when(carMapper.toDto(carEntity)).thenReturn(new CarResponseDto(
                1L, "A123BC77", "Toyota", "Camry", CarColor.WHITE
        ));

        // Act
        CarResponseDto responseDto = carService.addCar(requestDto, userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals("A123BC77", responseDto.carNumber());
        verify(carRepo, times(1)).save(any(CarEntity.class));
    }

    @Test
    void addCar_ShouldLinkExistingCarToUser_WhenCarAlreadyExists() {
        // Arrange
        Long userId = 1L;
        CarRequestDto requestDto = new CarRequestDto("A123BC77", "Toyota", "Camry", CarColor.WHITE);

        UserEntity user = new UserEntity();
        user.setId(userId);

        CarEntity existingCar = new CarEntity();
        existingCar.setId(1L);
        existingCar.setCarNumber("A123BC77");

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carRepo.findByCarNumber("A123BC77")).thenReturn(Optional.of(existingCar));
        when(carMapper.toDto(existingCar)).thenReturn(new CarResponseDto(
                1L, "A123BC77", null, null, CarColor.UNDEFINED
        ));

        // Act
        CarResponseDto responseDto = carService.addCar(requestDto, userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals("A123BC77", responseDto.carNumber());
        verify(carRepo, never()).save(any(CarEntity.class)); // Автомобиль не сохраняется заново
    }

    @Test
    void editCar_ShouldEditCar_WhenCarExists() {
        // Arrange
        Long carId = 1L;
        Long userId = 1L;
        CarRequestDto requestDto = new CarRequestDto("A123BC77", "Honda", "Civic", CarColor.BLUE);

        UserEntity user = new UserEntity();
        user.setId(userId);

        CarEntity carEntity = new CarEntity();
        carEntity.setId(carId);
        carEntity.setCarNumber("A123BC77");
        carEntity.setBrand("Toyota");
        carEntity.setModel("Camry");
        carEntity.setColor(CarColor.WHITE);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carRepo.findById(carId)).thenReturn(Optional.of(carEntity));
        when(carMapper.toDto(carEntity)).thenReturn(new CarResponseDto(
                carId, "A123BC77", "Honda", "Civic", CarColor.BLUE
        ));

        // Act
        CarResponseDto responseDto = carService.editCar(requestDto, carId, userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals("Honda", responseDto.brand());
        assertEquals("Civic", responseDto.model());
        assertEquals(CarColor.BLUE, responseDto.color());
        verify(carRepo, times(1)).save(carEntity);
    }

    @Test
    void editCar_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        Long carId = 999L;
        Long userId = 1L;
        CarRequestDto requestDto = new CarRequestDto("A123BC77", "Honda", "Civic", CarColor.BLUE);

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carRepo.findById(carId)).thenReturn(Optional.empty());

        // Act & Assert
        CarNotFoundException exception = assertThrows(
                CarNotFoundException.class,
                () -> carService.editCar(requestDto, carId, userId)
        );
        assertEquals("Автомобиль c id 999 не существует.", exception.getMessage());
    }

    @Test
    void getCarByNumber_ShouldReturnCar_WhenCarExists() {
        // Arrange
        String carNumber = "A123BC77";

        CarEntity carEntity = new CarEntity();
        carEntity.setId(1L);
        carEntity.setCarNumber(carNumber);

        when(carRepo.findByCarNumber(carNumber)).thenReturn(Optional.of(carEntity));
        when(carMapper.toDto(carEntity)).thenReturn(new CarResponseDto(
                1L, carNumber, null, null, CarColor.UNDEFINED
        ));

        // Act
        CarResponseDto responseDto = carService.getCarByNumber(carNumber);

        // Assert
        assertNotNull(responseDto);
        assertEquals(carNumber, responseDto.carNumber());
    }

    @Test
    void getCarByNumber_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        String carNumber = "A123BC77";

        when(carRepo.findByCarNumber(carNumber)).thenReturn(Optional.empty());

        // Act & Assert
        CarNotFoundException exception = assertThrows(
                CarNotFoundException.class,
                () -> carService.getCarByNumber(carNumber)
        );
        assertEquals("Автомобиль c номером A123BC77 не существует.", exception.getMessage());
    }

    @Test
    void getListCarByUserId_ShouldReturnCars_WhenUserHasCars() {
        // Arrange
        Long userId = 1L;

        CarEntity car1 = new CarEntity();
        car1.setId(1L);
        car1.setCarNumber("A123BC77");

        CarEntity car2 = new CarEntity();
        car2.setId(2L);
        car2.setCarNumber("B456CD77");

        Set<CarEntity> cars = Set.of(car1, car2);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setCars(cars);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carMapper.toDto(cars)).thenReturn(Set.of(
                new CarResponseDto(1L, "A123BC77", null, null, CarColor.UNDEFINED),
                new CarResponseDto(2L, "B456CD77", null, null, CarColor.UNDEFINED)
        ));

        // Act
        Set<CarResponseDto> responseDtos = carService.getListCarByUserId(userId);

        // Assert
        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());
        assertTrue(responseDtos.stream().anyMatch(car -> car.carNumber().equals("A123BC77")));
        assertTrue(responseDtos.stream().anyMatch(car -> car.carNumber().equals("B456CD77")));
    }

    @Test
    void getListCarByUserId_ShouldReturnEmptySet_WhenUserHasNoCars() {
        // Arrange
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setCars(Collections.emptySet());

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carMapper.toDto(Collections.emptySet())).thenReturn(Collections.emptySet());

        // Act
        Set<CarResponseDto> responseDtos = carService.getListCarByUserId(userId);

        // Assert
        assertNotNull(responseDtos);
        assertTrue(responseDtos.isEmpty());
    }

    @Test
    void deleteByUserId_ShouldRemoveCarFromUser_WhenCarIsSharedWithOtherUsers() {
        // Arrange
        Long carId = 1L;
        Long userId = 1L;

        UserEntity user1 = new UserEntity();
        user1.setId(1L);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);

        CarEntity car = new CarEntity();
        car.setId(carId);
        car.setCarNumber("A123BC77");
        car.setUsers(new HashSet<>(Set.of(user1, user2)));

        when(userRepo.findById(userId)).thenReturn(Optional.of(user1));
        when(carRepo.findById(carId)).thenReturn(Optional.of(car));

        // Act
        Long deletedCarId = carService.deleteByUserId(carId, userId);

        // Assert
        assertEquals(carId, deletedCarId);
        assertFalse(car.getUsers().contains(user1)); // Пользователь удален из множества
        assertTrue(car.getUsers().contains(user2));  // Другой пользователь остается
        verify(carRepo, never()).delete(any(CarEntity.class)); // Автомобиль не удален полностью
    }

    @Test
    void deleteByUserId_ShouldDeleteCarCompletely_WhenCarIsNotSharedWithOtherUsers() {
        // Arrange
        Long carId = 1L;
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        CarEntity car = new CarEntity();
        car.setId(carId);
        car.setCarNumber("A123BC77");
        car.setUsers(new HashSet<>(Set.of(user)));

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carRepo.findById(carId)).thenReturn(Optional.of(car));

        // Act
        Long deletedCarId = carService.deleteByUserId(carId, userId);

        // Assert
        assertEquals(carId, deletedCarId);
        assertTrue(car.getUsers().isEmpty()); // Множество пользователей пустое
        verify(carRepo, times(1)).delete(car); // Автомобиль полностью удален
    }

    @Test
    void deleteByUserId_ShouldThrowException_WhenCarNotFound() {
        // Arrange
        Long carId = 999L;
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carRepo.findById(carId)).thenReturn(Optional.empty());

        // Act & Assert
        CarNotFoundException exception = assertThrows(
                CarNotFoundException.class,
                () -> carService.deleteByUserId(carId, userId)
        );
        assertEquals("Автомобиль c id 999не существует.", exception.getMessage());
    }

    @Test
    void deleteByUserId_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long carId = 1L;
        Long userId = 999L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> carService.deleteByUserId(carId, userId)
        );
        assertEquals("Пользователя c id = 999 не существует.", exception.getMessage());
    }
}