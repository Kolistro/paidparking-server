package ru.omgu.paidparking_server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.omgu.paidparking_server.dto.request.PaymentRequestDto;
import ru.omgu.paidparking_server.entity.BuildingEntity;
import ru.omgu.paidparking_server.entity.PaymentEntity;
import ru.omgu.paidparking_server.entity.ReservationEntity;
import ru.omgu.paidparking_server.enums.PaymentStatus;
import ru.omgu.paidparking_server.enums.ReservationStatus;
import ru.omgu.paidparking_server.exception.*;
import ru.omgu.paidparking_server.repository.PaymentRepo;
import ru.omgu.paidparking_server.repository.ReservationRepo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private ReservationRepo reservationRepo;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void addPaymentForReservation_ShouldAddPayment_WhenReservationExistsAndPaymentDoesNotExist() {
        // Arrange
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);

        BuildingEntity building = new BuildingEntity();
        building.setCostPerHour(100L); // Установите стоимость за час

        reservation.setBuilding(building); // Свяжите здание с бронированием

        // Устанавливаем startTime и endTime
        LocalDateTime now = LocalDateTime.now();
        reservation.setStartTime(now);
        reservation.setEndTime(now.plusHours(2)); // Бронирование на 2 часа

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentRepo.findByReservation(reservation)).thenReturn(Optional.empty());

        // Act
        paymentService.addPaymentForReservation(reservationId);

        // Assert
        verify(paymentRepo, times(1)).save(any(PaymentEntity.class));
    }

    @Test
    void addPaymentForReservation_ShouldThrowException_WhenPaymentAlreadyExists() {
        // Arrange
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);

        PaymentEntity existingPayment = new PaymentEntity();
        existingPayment.setId(1L);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentRepo.findByReservation(reservation)).thenReturn(Optional.of(existingPayment));

        // Act & Assert
        PaymentAlreadyExistsException exception = assertThrows(
                PaymentAlreadyExistsException.class,
                () -> paymentService.addPaymentForReservation(reservationId)
        );
        assertEquals("Платёж уже существует для этого бронирования", exception.getMessage());
    }

    @Test
    void addPaymentForReservation_ShouldThrowException_WhenReservationNotFound() {
        // Arrange
        Long reservationId = 999L;

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        ReservationNotFoundException exception = assertThrows(
                ReservationNotFoundException.class,
                () -> paymentService.addPaymentForReservation(reservationId)
        );
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void uploadPayment_ShouldUploadReceipt_WhenPaymentExists() throws IOException {
        // Arrange
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setReservation(reservation);

        MockMultipartFile file = new MockMultipartFile("receiptFile", "test.txt", "text/plain", "test data".getBytes());

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentRepo.findByReservation(reservation)).thenReturn(Optional.of(payment));

        // Act
        paymentService.uploadPayment(reservationId, new PaymentRequestDto(file));

        // Assert
        assertNotNull(payment.getReceiptFile());
        assertEquals("test.txt", payment.getReceiptFileName());
        assertEquals("text/plain", payment.getReceiptContentType());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        verify(paymentRepo, times(1)).save(payment);
    }

    @Test
    void uploadPayment_ShouldThrowException_WhenFileIsCorrupted() throws IOException{
        // Arrange
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setReservation(reservation);

        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getBytes()).thenThrow(new IOException("Файл поврежден"));

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentRepo.findByReservation(reservation)).thenReturn(Optional.of(payment));

        // Act & Assert
        PaymentFailedReadCheckFileException exception = assertThrows(
                PaymentFailedReadCheckFileException.class,
                () -> paymentService.uploadPayment(reservationId, new PaymentRequestDto(invalidFile))
        );
        assertEquals("Не удалось прочитать файл чека", exception.getMessage());
    }


    @Test
    void completedPayment_ShouldCompletePayment_WhenPaymentExists() {
        // Arrange
        Long paymentId = 1L;

        PaymentEntity payment = new PaymentEntity();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        paymentService.completedPayment(paymentId);

        // Assert
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertNotNull(payment.getConfirmedAt());
        verify(paymentRepo, times(1)).save(payment);
    }

    @Test
    void completedPayment_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        Long paymentId = 999L;

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.completedPayment(paymentId)
        );
        assertEquals("Платёж не найден с id 999", exception.getMessage());
    }

    @Test
    void failedPayment_ShouldFailPayment_WhenPaymentExists() {
        // Arrange
        Long paymentId = 1L;

        PaymentEntity payment = new PaymentEntity();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        paymentService.failedPayment(paymentId);

        // Assert
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertNotNull(payment.getConfirmedAt());
        verify(paymentRepo, times(1)).save(payment);
    }

    @Test
    void failedPayment_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        Long paymentId = 999L;

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.failedPayment(paymentId)
        );
        assertEquals("Платёж не найден с id 999", exception.getMessage());
    }

    @Test
    void cancelPayment_ShouldCancelPaymentAndReservation_WhenPaymentExists() {
        // Arrange
        Long paymentId = 1L;

        PaymentEntity payment = new PaymentEntity();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.CREATED);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.WAITING);
        payment.setReservation(reservation);

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act
        paymentService.cancelPayment(paymentId);

        // Assert
        assertEquals(PaymentStatus.CANCELED, payment.getStatus());
        assertEquals(ReservationStatus.CANCELED, reservation.getStatus());
        verify(paymentRepo, times(1)).save(payment);
        verify(reservationRepo, times(1)).save(reservation);
    }

    @Test
    void cancelPayment_ShouldThrowException_WhenPaymentIsCompleted() {
        // Arrange
        Long paymentId = 1L;

        PaymentEntity payment = new PaymentEntity();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepo.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act & Assert
        PaymentCompletedNotPossibleToCancelException exception = assertThrows(
                PaymentCompletedNotPossibleToCancelException.class,
                () -> paymentService.cancelPayment(paymentId)
        );
        assertEquals("Нельзя отменить уже завершённый платёж.", exception.getMessage());
    }

    @Test
    void getPaymentByReservation_ShouldReturnPayment_WhenReservationAndPaymentExist() {
        // Arrange
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setReservation(reservation);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentRepo.findByReservation(reservation)).thenReturn(Optional.of(payment));

        // Act
        PaymentEntity result = paymentService.getPaymentByReservation(reservationId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPaymentByReservation_ShouldThrowException_WhenReservationNotFound() {
        // Arrange
        Long reservationId = 999L;

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        ReservationNotFoundException exception = assertThrows(
                ReservationNotFoundException.class,
                () -> paymentService.getPaymentByReservation(reservationId)
        );
        assertEquals("Бронирование с ID 999 не найдено.", exception.getMessage());
    }

    @Test
    void getPaymentByReservation_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);

        when(reservationRepo.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentRepo.findByReservation(reservation)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentByReservation(reservationId)
        );
        assertEquals("Для данного бронирования нет платежа.", exception.getMessage());
    }

    @Test
    void getPaymentsByStatus_ShouldReturnPayments_WhenPaymentsExist() {
        // Arrange
        PaymentStatus status = PaymentStatus.PENDING;

        PaymentEntity payment1 = new PaymentEntity();
        payment1.setId(1L);
        payment1.setStatus(status);

        PaymentEntity payment2 = new PaymentEntity();
        payment2.setId(2L);
        payment2.setStatus(status);

        List<PaymentEntity> payments = List.of(payment1, payment2);

        when(paymentRepo.findAllByStatus(status)).thenReturn(payments);

        // Act
        List<PaymentEntity> result = paymentService.getPaymentsByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getStatus() == status));
    }

    @Test
    void getPaymentsByStatus_ShouldReturnEmptyList_WhenNoPaymentsFound() {
        // Arrange
        PaymentStatus status = PaymentStatus.COMPLETED;

        when(paymentRepo.findAllByStatus(status)).thenReturn(Collections.emptyList());

        // Act
        List<PaymentEntity> result = paymentService.getPaymentsByStatus(status);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deletePayment_ShouldDeletePayment_WhenPaymentExists() {
        // Arrange
        Long paymentId = 1L;

        PaymentEntity payment = new PaymentEntity();
        payment.setId(paymentId);

        when(paymentRepo.existsById(paymentId)).thenReturn(true);

        // Act
        paymentService.deletePayment(paymentId);

        // Assert
        verify(paymentRepo, times(1)).deleteById(paymentId);
    }

    @Test
    void deletePayment_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        Long paymentId = 999L;

        when(paymentRepo.existsById(paymentId)).thenReturn(false);

        // Act & Assert
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.deletePayment(paymentId)
        );
        assertEquals("Платёж не найден", exception.getMessage());
    }
}
