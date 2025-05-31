package ru.omgu.paidparking_server.enums;

public enum PaymentStatus {
    PENDING,    // Чек загружен, ожидает подтверждения.
    COMPLETED,  // Платеж завершен успешно
    FAILED,     // Платеж не прошел
    CREATED,    // Платеж был создан
    CANCELED,   // Платеж был отменен
    EXPIRED     // Время платежа истекло
}
