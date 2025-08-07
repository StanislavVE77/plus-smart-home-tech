package ru.yandex.practicum.commerce.interactionapi.interfase;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interactionapi.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartOperations {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam(required = true) String username);

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(@RequestParam(required = true) String username, @RequestBody Map<UUID, Long> products);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam(required = true) String username, @RequestBody ChangeProductQuantityRequest request);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(@RequestParam(required = true) String username, @RequestBody List<UUID> productIds);

    @DeleteMapping
    boolean deactivateCurrentShoppingCart(@RequestParam(required = true) String username);
}
