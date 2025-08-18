package ru.yandex.practicum.commerce.interactionapi.exception;

import java.util.UUID;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException{
    public SpecifiedProductAlreadyInWarehouseException(UUID productId) {
        super("Ошибка, товар " + productId + " уже на складе");
    }
}