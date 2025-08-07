package ru.yandex.practicum.commerce.interactionapi.exception;

import java.util.UUID;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    public NoSpecifiedProductInWarehouseException(UUID productId) {
        super("Продукт " + productId + " на складе не найден");
    }

}
