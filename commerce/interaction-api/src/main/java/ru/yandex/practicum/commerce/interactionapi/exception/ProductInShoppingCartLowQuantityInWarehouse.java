package ru.yandex.practicum.commerce.interactionapi.exception;

import java.util.UUID;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException{
    public ProductInShoppingCartLowQuantityInWarehouse(UUID productId) {
        super("Ошибка, товар " + productId + " из корзины не находится в требуемом количестве на складе");
    }
}