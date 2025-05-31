package ru.omgu.paidparking_server.exception;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(String s) {
        super(s);
    }
}
