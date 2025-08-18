package ru.yandex.practicum.commerce.interactionapi.exception;

import ru.yandex.practicum.commerce.interactionapi.dto.shoppingstore.ProductDto;

public class CreateNewProductSericeException extends RuntimeException {
    public CreateNewProductSericeException(ProductDto product) {
        super("Сервис createNewProduct с ProductDto= " + product + " вернул ошибку");
    }
}