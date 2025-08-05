package ru.yandex.practicum.commerce.interactionapi.interfase;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interactionapi.dto.PageDTO;
import ru.yandex.practicum.commerce.interactionapi.dto.ProductCategory;
import ru.yandex.practicum.commerce.interactionapi.dto.ProductDto;
import ru.yandex.practicum.commerce.interactionapi.dto.ProductQuantityState;

import java.util.UUID;

public interface ShoppingStoreOperations {

    @PutMapping
    ProductDto createNewProduct(@RequestBody ProductDto product);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto product);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable("productId") UUID productId);

    @GetMapping
    PageDTO<ProductDto> getProducts(@RequestParam(required = false) ProductCategory category,
                                    @PageableDefault(sort = {"productName"}) Pageable pageable);

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@RequestParam(required = true) UUID productId,
                                    @RequestParam(required = true) ProductQuantityState quantityState);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID productId);
}
