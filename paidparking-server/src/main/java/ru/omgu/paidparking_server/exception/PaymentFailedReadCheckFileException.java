package ru.omgu.paidparking_server.exception;

import java.io.IOException;

public class PaymentFailedReadCheckFileException extends RuntimeException {
    public PaymentFailedReadCheckFileException(String s, IOException e) {
        super(s, e);
    }
}
