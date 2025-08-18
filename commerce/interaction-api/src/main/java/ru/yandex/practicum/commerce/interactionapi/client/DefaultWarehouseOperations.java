package ru.yandex.practicum.commerce.interactionapi.client;

import feign.RetryableException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.*;
import ru.yandex.practicum.commerce.interactionapi.exception.WarehouseServicesNotAvailableException;

@Component
public class DefaultWarehouseOperations  implements WarehouseClient {

    @Override
    public AddressDto getWarehouseAddress() {
        throw new WarehouseServicesNotAvailableException();
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto request) throws RetryableException {
        throw new WarehouseServicesNotAvailableException();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        throw new WarehouseServicesNotAvailableException();
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        throw new WarehouseServicesNotAvailableException();
    }
}
