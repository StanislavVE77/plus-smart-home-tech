package ru.yandex.practicum.commerce.interactionapi.exception;

import java.util.UUID;

public class NoPaymentFoundException  extends RuntimeException{
    public NoPaymentFoundException(UUID paymentId) {
        super("Платеж с идентификатором " + paymentId + " не найден");
    }
}