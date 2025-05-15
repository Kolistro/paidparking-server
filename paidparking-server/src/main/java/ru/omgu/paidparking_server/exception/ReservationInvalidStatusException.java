package ru.omgu.paidparking_server.exception;

public class ReservationInvalidStatusException extends RuntimeException {
    public ReservationInvalidStatusException(String message) {
        super(message);
    }
}
