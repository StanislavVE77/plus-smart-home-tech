package ru.yandex.practicum.commerce.interactionapi.exception;

import java.util.UUID;

public class NoOrderFoundException extends RuntimeException{
    public NoOrderFoundException(UUID orderId) {
        super("Заказ с идентификатором " + orderId + " не найден");
    }
}