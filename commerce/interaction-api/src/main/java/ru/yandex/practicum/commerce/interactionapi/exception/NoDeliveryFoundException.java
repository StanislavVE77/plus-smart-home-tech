package ru.yandex.practicum.commerce.interactionapi.exception;

import java.util.UUID;

public class NoDeliveryFoundException extends RuntimeException{
    public NoDeliveryFoundException(UUID deliveryId) {
        super("Доставка с идентификатором " + deliveryId + " не найдена");
    }
}