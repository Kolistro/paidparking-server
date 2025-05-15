package ru.omgu.paidparking_server.exception;

public class ReservationHistoryNotFoundException extends RuntimeException {
    public ReservationHistoryNotFoundException(String message) {
        super(message);
    }
}
