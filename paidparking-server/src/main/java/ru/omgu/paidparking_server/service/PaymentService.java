package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.omgu.paidparking_server.dto.request.PaymentRequestDto;
import ru.omgu.paidparking_server.entity.PaymentEntity;
import ru.omgu.paidparking_server.entity.ReservationEntity;
import ru.omgu.paidparking_server.enums.PaymentStatus;
import ru.omgu.paidparking_server.enums.ReservationStatus;
import ru.omgu.paidparking_server.exception.*;
import ru.omgu.paidparking_server.repository.PaymentRepo;
import ru.omgu.paidparking_server.repository.ReservationRepo;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepo paymentRepo;
    private final ReservationRepo reservationRepo;

    public void addPaymentForReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Бронирование не найдено"));

        if (paymentRepo.findByReservation(reservation).isPresent()) {
            throw new PaymentAlreadyExistsException("Платёж уже существует для этого бронирования");
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setReservation(reservation);
        payment.setAmount(getAmount(reservation));
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);
    }

    public void uploadPayment(Long reservationId, PaymentRequestDto dto) {
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Бронирование с ID " + reservationId + " не найдено."));

        PaymentEntity payment = paymentRepo.findByReservation(reservation)
                .orElseThrow(() -> new PaymentNotFoundException("Для данного бронирования нет платежа."));

        MultipartFile file = dto.receiptFile();
        try {
            payment.setReceiptFile(file.getBytes());
        } catch (IOException e) {
            throw new PaymentFailedReadCheckFileException("Не удалось прочитать файл чека", e);
        }

        payment.setReceiptFileName(file.getOriginalFilename());
        payment.setReceiptContentType(file.getContentType());
        payment.setReceiptUploadedAt(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepo.save(payment);
    }


    // Подтверждение успешного платежа оператором при ручной проверке чека
    public void completedPayment(Long paymentId) {
        PaymentEntity payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платёж не найден с id " + paymentId));
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setConfirmedAt(LocalDateTime.now());

        paymentRepo.save(payment);
    }

    // Отклонение платежа оператором при ручной проверке чека
    public void failedPayment(Long paymentId) {
        PaymentEntity payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платёж не найден с id " + paymentId));
        payment.setStatus(PaymentStatus.FAILED);
        payment.setConfirmedAt(LocalDateTime.now());

        paymentRepo.save(payment);
    }

    // Отмена пользователем (например, по кнопке "отменить платёж")
    @Transactional
    public void cancelPayment(Long paymentId) {
        PaymentEntity payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платёж не найден с id " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new PaymentCompletedNotPossibleToCancelException("Нельзя отменить уже завершённый платёж.");
        }

        payment.setStatus(PaymentStatus.CANCELED);
        paymentRepo.save(payment);

        ReservationEntity reservation = payment.getReservation();
        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepo.save(reservation);
    }

    public List<PaymentEntity> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepo.findAllByStatus(status);
    }

    public void deletePayment(Long paymentId) {
        if (!paymentRepo.existsById(paymentId)) {
            throw new PaymentNotFoundException("Платёж не найден");
        }
        paymentRepo.deleteById(paymentId);
    }

    public void expiredPayment(ReservationEntity reservation) {
        PaymentEntity payment = paymentRepo.findByReservation(reservation)
                .orElseThrow(() -> new PaymentNotFoundException("Платёж не найден"));

        if (payment.getStatus() == PaymentStatus.CREATED || payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepo.save(payment);
        }
    }

    private Long getAmount(ReservationEntity reservation){
        Long costPerHour = reservation.getBuilding().getCostPerHour();
        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();

        Duration duration = Duration.between(start, end);
        // Получаем количество часов (округляем вверх, 1ч 10м — считаем за 2ч)
        long hours = (long) Math.ceil(duration.toMinutes() / 60.0);
        return costPerHour * hours;
    }
}
