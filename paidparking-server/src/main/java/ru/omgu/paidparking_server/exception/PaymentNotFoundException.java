package ru.omgu.paidparking_server.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String s) {
        super(s);
    }
}
