package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.omgu.paidparking_server.entity.*;
import ru.omgu.paidparking_server.enums.PaymentStatus;
import ru.omgu.paidparking_server.exception.ReservationHistoryNotFoundException;
import ru.omgu.paidparking_server.repository.ReservationHistoryRepo;
import ru.omgu.paidparking_server.repository.UserRepo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationHistoryServiceTest {

    @Mock
    private ReservationHistoryRepo historyRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private ReservationHistoryService historyService;

    @Test
    void archiveReservation_ShouldArchiveReservation_WhenDataIsValid() {
        // Arrange
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(1L);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("+123456789");

        CarEntity car = new CarEntity();
        car.setId(1L);
        car.setCarNumber("A123BC77");

        BuildingEntity building = new BuildingEntity();
        building.setId(1L);
        building.setAddress("Main Street 123");

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setAmount(100L);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setConfirmedAt(LocalDateTime.now());

        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setBuilding(building);
        reservation.setPayment(payment);

        // Act
        historyService.archiveReservation(reservation);

        // Assert
        ArgumentCaptor<ReservationHistoryEntity> captor = ArgumentCaptor.forClass(ReservationHistoryEntity.class);
        verify(historyRepo, times(1)).save(captor.capture());

        ReservationHistoryEntity archived = captor.getValue();
        assertNotNull(archived);
        assertEquals(1L, archived.getUserId());
        assertEquals("John", archived.getUserFirstName());
        assertEquals("Doe", archived.getUserLastName());
        assertEquals("+123456789", archived.getUserPhoneNumber());
        assertEquals("A123BC77", archived.getCarNumber());
        assertEquals("Main Street 123", archived.getBuildingAddress());
        assertEquals(100L, archived.getPaymentAmount());
        assertEquals(PaymentStatus.COMPLETED, archived.getPaymentStatus());
    }

    @Test
    void getHistoryById_ShouldReturnHistory_WhenHistoryExists() {
        // Arrange
        Long historyId = 1L;

        ReservationHistoryEntity history = new ReservationHistoryEntity();
        history.setId(historyId);

        when(historyRepo.findById(historyId)).thenReturn(Optional.of(history));

        // Act
        ReservationHistoryEntity result = historyService.getHistoryById(historyId);

        // Assert
        assertNotNull(result);
        assertEquals(historyId, result.getId());
    }

    @Test
    void getHistoryById_ShouldThrowException_WhenHistoryNotFound() {
        // Arrange
        Long historyId = 999L;

        when(historyRepo.findById(historyId)).thenReturn(Optional.empty());

        // Act & Assert
        ReservationHistoryNotFoundException exception = assertThrows(
                ReservationHistoryNotFoundException.class,
                () -> historyService.getHistoryById(historyId)
        );
        assertEquals("История бронирования с id = 999 не найдена.", exception.getMessage());
    }

    @Test
    void getAllHistoryByUserId_ShouldReturnHistories_WhenUserHasHistories() {
        // Arrange
        Long userId = 1L;

        ReservationHistoryEntity history1 = new ReservationHistoryEntity();
        history1.setId(1L);

        ReservationHistoryEntity history2 = new ReservationHistoryEntity();
        history2.setId(2L);

        List<ReservationHistoryEntity> histories = List.of(history1, history2);

        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(historyRepo.findAllByUserId(userId)).thenReturn(histories);

        // Act
        List<ReservationHistoryEntity> result = historyService.getAllHistoryByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(h -> h.getId() == 1L));
        assertTrue(result.stream().anyMatch(h -> h.getId() == 2L));
    }

    @Test
    void getAllHistoryByUserId_ShouldReturnEmptyList_WhenUserHasNoHistories() {
        // Arrange
        Long userId = 1L;

        when(userRepo.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(historyRepo.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<ReservationHistoryEntity> result = historyService.getAllHistoryByUserId(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_ShouldDeleteHistory_WhenHistoryExists() {
        // Arrange
        Long historyId = 1L;

        ReservationHistoryEntity history = new ReservationHistoryEntity();
        history.setId(historyId);

        when(historyRepo.findById(historyId)).thenReturn(Optional.of(history));

        // Act
        Long deletedId = historyService.delete(historyId);

        // Assert
        assertEquals(historyId, deletedId);
        verify(historyRepo, times(1)).deleteById(historyId);
    }

    @Test
    void delete_ShouldThrowException_WhenHistoryNotFound() {
        // Arrange
        Long historyId = 999L;

        when(historyRepo.findById(historyId)).thenReturn(Optional.empty());

        // Act & Assert
        ReservationHistoryNotFoundException exception = assertThrows(
                ReservationHistoryNotFoundException.class,
                () -> historyService.delete(historyId)
        );
        assertEquals("История бронирования с id = 999 не найдена.", exception.getMessage());
    }
}