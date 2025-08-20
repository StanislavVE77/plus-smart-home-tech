package ru.yandex.practicum.commerce.shoppingcart.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.shoppingcart.model.ShoppingCart;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartMapper {
    public ShoppingCartDto toShoppingCartDto(List<ShoppingCart> shoppingCartList) {
        Map<UUID, Long> products = shoppingCartList.stream()
                .collect(Collectors.toMap(ShoppingCart::getProductId, ShoppingCart::getQuantity));

        return new ShoppingCartDto(
                shoppingCartList.getFirst().getShoppingCartId(),
                shoppingCartList.getFirst().getUsername(),
                products
        );

    }
}
