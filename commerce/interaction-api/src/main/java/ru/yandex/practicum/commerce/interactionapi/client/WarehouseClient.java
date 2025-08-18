package ru.yandex.practicum.commerce.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.ShippedToDeliveryRequest;

@FeignClient(name = "warehouse", fallback = DefaultWarehouseOperations.class)
public interface WarehouseClient {

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto request);

    @PostMapping("/api/v1/warehouse/shipped")
    void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);

}
