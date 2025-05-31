package ru.omgu.paidparking_server.exception;

public class PaymentCompletedNotPossibleToCancelException extends RuntimeException {
    public PaymentCompletedNotPossibleToCancelException(String s) {
        super(s);
    }
}
