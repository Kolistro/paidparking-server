package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.omgu.paidparking_server.dto.request.ReservationRequestDto;
import ru.omgu.paidparking_server.dto.response.ReservationResponseDto;
import ru.omgu.paidparking_server.entity.*;
import ru.omgu.paidparking_server.enums.PaymentStatus;
import ru.omgu.paidparking_server.enums.ReservationStatus;
import ru.omgu.paidparking_server.exception.ReservationInvalidStatusException;
import ru.omgu.paidparking_server.exception.ReservationNotFoundException;
import ru.omgu.paidparking_server.exception.UserNotFoundException;
import ru.omgu.paidparking_server.mapper.ReservationMapper;
import ru.omgu.paidparking_server.repository.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepo reservationRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CarRepo carRepo;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private BuildingRepo buildingRepo;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationHistoryService historyService;

    private PaymentService paymentService;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        // Репозитории остаются моками (их поведение мы можем настраивать)
        paymentService = new PaymentService(paymentRepo, reservationRepo);

        reservationService = new ReservationService(
                reservationRepo,
                userRepo,
                carRepo,
                buildingRepo,
                reservationMapper,
                historyService,
                paymentService // <-- передаём настоящий экземпляр
        );
    }


    @Test
    void addReservation_ShouldAddReservation_WhenDataIsValid() {
        // Arrange
        Long userId = 1L;
        String carNumber = "A123BC77";
        Long buildingId = 1L;

        ReservationRequestDto requestDto = new ReservationRequestDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                carNumber,
                buildingId
        );

        UserEntity user = new UserEntity();
        user.setId(userId);

        CarEntity car = new CarEntity();
        car.setCarNumber(carNumber);

        BuildingEntity building = new BuildingEntity();
        building.setId(buildingId);

        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId(1L);
        reservationEntity.setStartTime(requestDto.startTime());
        reservationEntity.setEndTime(requestDto.endTime());
        reservationEntity.setUser(user);
        reservationEntity.setCar(car);
        reservationEntity.setBuilding(building);
        reservationEntity.setStatus(ReservationStatus.WAITING);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(carRepo.findByCarNumber(carNumber)).thenReturn(Optional.of(car));
        when(buildingRepo.findById(buildingId)).thenReturn(Optional.of(building));
        when(reservationMapper.toDto(any(ReservationEntity.class))).thenReturn(new ReservationResponseDto(
                1L, requestDto.startTime(), requestDto.endTime(), ReservationStatus.WAITING
        ));

        // Act
        ReservationResponseDto responseDto = reservationService.addReservation(requestDto, userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.id());
        assertEquals(ReservationStatus.WAITING, responseDto.status());
        verify(reservationRepo, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    void addReservation_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        ReservationRequestDto requestDto = new ReservationRequestDto(
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                "A123BC77",
                1L
        );

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> reservationService.addReservation(requestDto, userId)
        );
        assertEquals("Пользователя c id = 999 не существует.", exception.getMessage());
    }

    @Test
    void updateStatusToActive_ShouldUpdateStatus_WhenReservationIsWaiting() {
        // Arrange
        Long reservationId = 1L;
        Long userId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);
        reservation.setStatus(ReservationStatus.WAITING);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(reservationMapper.toDto(reservation)).thenReturn(new ReservationResponseDto(
                reservationId, null, null, ReservationStatus.ACTIVE
        ));

        // Act
        ReservationResponseDto responseDto = reservationService.updateStatusToActive(reservationId, userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals(ReservationStatus.ACTIVE, responseDto.status());
        verify(reservationRepo, times(1)).save(reservation);
    }

    @Test
    void updateStatusToActive_ShouldThrowException_WhenStatusIsNotWaiting() {
        // Arrange
        Long reservationId = 1L;
        Long userId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);
        reservation.setStatus(ReservationStatus.COMPLETED);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        // Act & Assert
        ReservationInvalidStatusException exception = assertThrows(
                ReservationInvalidStatusException.class,
                () -> reservationService.updateStatusToActive(reservationId, userId)
        );
        assertEquals("Можно активировать только бронирования со статусом WAITING", exception.getMessage());
    }

    @Test
    void cancelExpiredReservations_ShouldExpireReservationsAndPayments() {
        // Arrange
        ReservationEntity expiredReservation = new ReservationEntity();
        expiredReservation.setId(1L);
        expiredReservation.setStatus(ReservationStatus.WAITING);

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setStatus(PaymentStatus.CREATED);

        LocalDateTime fixedTime = LocalDateTime.of(2025, 6, 3, 14, 45, 45);
        expiredReservation.setCreatedAt(fixedTime.minusMinutes(20));

        when(reservationRepo.findByStatusAndCreatedAtBefore(
                eq(ReservationStatus.WAITING),
                eq(fixedTime.minusMinutes(15)))
        ).thenAnswer(invocation -> List.of(expiredReservation));

        when(paymentRepo.findByReservation(eq(expiredReservation))).thenAnswer(invocation -> Optional.of(payment));

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedTime);

            // Act
            reservationService.cancelExpiredReservations();
        }

        // Assert
        assertEquals(ReservationStatus.EXPIRED, expiredReservation.getStatus());
        verify(reservationRepo, times(1)).save(expiredReservation);
        verify(paymentRepo, times(1)).findByReservation(expiredReservation);
        verify(paymentRepo, times(1)).save(payment);
        assertEquals(PaymentStatus.EXPIRED, payment.getStatus());
    }

    @Test
    void completeReservation_ShouldCompleteAndArchiveReservation_WhenStatusIsActive() {
        // Arrange
        Long reservationId = 1L;
        Long userId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);
        reservation.setStatus(ReservationStatus.ACTIVE);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(reservationMapper.toDto(reservation)).thenReturn(new ReservationResponseDto(
                reservationId, null, null, ReservationStatus.COMPLETED
        ));

        // Act
        ReservationResponseDto responseDto = reservationService.completeReservation(reservationId, userId);

        // Assert
        assertNotNull(responseDto);
        assertEquals(ReservationStatus.COMPLETED, responseDto.status());
        verify(reservationRepo, times(1)).save(reservation);
        verify(historyService, times(1)).archiveReservation(reservation);
    }

    @Test
    void completeReservation_ShouldThrowException_WhenStatusIsNotActive() {
        // Arrange
        Long reservationId = 1L;
        Long userId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);
        reservation.setStatus(ReservationStatus.WAITING);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        // Act & Assert
        ReservationInvalidStatusException exception = assertThrows(
                ReservationInvalidStatusException.class,
                () -> reservationService.completeReservation(reservationId, userId)
        );
        assertEquals("Завершить можно только бронь со статусом ACTIVE", exception.getMessage());
    }

    @Test
    void getListReservationsByUserId_ShouldReturnReservations_WhenUserHasReservations() {
        // Arrange
        Long userId = 1L;

        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setId(1L);
        reservation1.setStatus(ReservationStatus.ACTIVE);

        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.setId(2L);
        reservation2.setStatus(ReservationStatus.WAITING);

        List<ReservationEntity> reservations = List.of(reservation1, reservation2);

        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(reservationRepo.findAllByUserId(userId)).thenReturn(reservations);
        when(reservationMapper.toDto(reservations)).thenReturn(List.of(
                new ReservationResponseDto(1L, null, null, ReservationStatus.ACTIVE),
                new ReservationResponseDto(2L, null, null, ReservationStatus.WAITING)
        ));

        // Act
        List<ReservationResponseDto> responseDtos = reservationService.getListReservationsByUserId(userId);

        // Assert
        assertNotNull(responseDtos);
        assertEquals(2, responseDtos.size());
        assertTrue(responseDtos.stream().anyMatch(r -> r.status() == ReservationStatus.ACTIVE));
        assertTrue(responseDtos.stream().anyMatch(r -> r.status() == ReservationStatus.WAITING));
    }

    @Test
    void getListReservationsByUserId_ShouldReturnEmptyList_WhenUserHasNoReservations() {
        // Arrange
        Long userId = 1L;

        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(reservationRepo.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<ReservationResponseDto> responseDtos = reservationService.getListReservationsByUserId(userId);

        // Assert
        assertNotNull(responseDtos);
        assertTrue(responseDtos.isEmpty());
    }

    @Test
    void deleteAllByUserId_ShouldDeleteAllReservations_WhenUserExists() {
        // Arrange
        Long userId = 1L;

        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        // Act
        reservationService.deleteAllByUserId(userId);

        // Assert
        verify(reservationRepo, times(1)).deleteAllByUserId(userId);
    }

    @Test
    void deleteAllByUserId_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> reservationService.deleteAllByUserId(userId)
        );
        assertEquals("Пользователя c id = 999 не существует.", exception.getMessage());
    }

    @Test
    void delete_ShouldDeleteReservation_WhenReservationAndUserExist() {
        // Arrange
        Long reservationId = 1L;
        Long userId = 1L;

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(new ReservationEntity()));
        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));

        // Act
        reservationService.delete(reservationId, userId);

        // Assert
        verify(reservationRepo, times(1)).deleteById(reservationId);
    }

    @Test
    void delete_ShouldThrowException_WhenReservationNotFound() {
        // Arrange
        Long reservationId = 999L;
        Long userId = 1L;

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        ReservationNotFoundException exception = assertThrows(
                ReservationNotFoundException.class,
                () -> reservationService.delete(reservationId, userId)
        );
        assertEquals("Брони c id = 999 не существует.", exception.getMessage());
    }
}