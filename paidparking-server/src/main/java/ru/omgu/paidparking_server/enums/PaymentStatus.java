package ru.omgu.paidparking_server.enums;

public enum PaymentStatus {
    PENDING,    // Ожидает обработки
    COMPLETED,  // Платеж завершен успешно
    FAILED,     // Платеж не прошел
    CREATED,    // Платеж был создан
    CANCELED    // Платеж был отменен
}
