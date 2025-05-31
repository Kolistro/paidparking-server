package ru.omgu.paidparking_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.omgu.paidparking_server.entity.*;
import ru.omgu.paidparking_server.exception.ReservationHistoryNotFoundException;
import ru.omgu.paidparking_server.repository.ReservationHistoryRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationHistoryService {
    private final ReservationHistoryRepo historyRepo;

    public void archiveReservation(ReservationEntity reservation) {
        ReservationHistoryEntity history = new ReservationHistoryEntity();

        UserEntity user = reservation.getUser();
        history.setUserId(user.getId());
        history.setUserFirstName(user.getFirstName());
        history.setUserLastName(user.getLastName());
        history.setUserPhoneNumber(user.getPhoneNumber());

        CarEntity car = reservation.getCar();
        history.setCarId(car.getId());
        history.setCarNumber(car.getCarNumber());

        BuildingEntity building = reservation.getBuilding();
        history.setBuildingId(building.getId());
        history.setBuildingAddress(building.getAddress());

        history.setStartTime(reservation.getStartTime());
        history.setEndTime(reservation.getEndTime());

        history.setStatus(reservation.getStatus());

        PaymentEntity payment = reservation.getPayment();
        if (payment != null) {
            history.setPaymentAmount(payment.getAmount());
            history.setPaymentStatus(payment.getStatus());
            history.setPaymentCreatedAt(payment.getCreatedAt());
            history.setPaymentConfirmedAt(payment.getConfirmedAt());
            history.setPaymentReceiptUploadedAt(payment.getReceiptUploadedAt());
            history.setReceiptFile(payment.getReceiptFile());
            history.setReceiptFileName(payment.getReceiptFileName());
            history.setReceiptContentType(payment.getReceiptContentType());
        }

        history.setArchivedAt(LocalDateTime.now());

        historyRepo.save(history);
    }

    public List<ReservationHistoryEntity> getHistoryByUserId(Long userId) {
        return historyRepo.findAllByUserId(userId);
    }

    public ReservationHistoryEntity getHistoryById(Long id) {
        return historyRepo.findById(id)
                .orElseThrow(() -> new ReservationHistoryNotFoundException(
                        "История бронирования с id = " + id + " не найдена."));
    }

    public List<ReservationHistoryEntity> getAllHistory() {
        return historyRepo.findAll();
    }

    public Long delete(Long id){
        historyRepo.findById(id)
                .orElseThrow(() -> new ReservationHistoryNotFoundException(
                        "История бронирования с id = " + id + " не найдена."));
        historyRepo.deleteById(id);
        return id;
    }
}
