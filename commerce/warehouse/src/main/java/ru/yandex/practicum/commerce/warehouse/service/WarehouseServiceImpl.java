package ru.yandex.practicum.commerce.warehouse.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.interactionapi.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.commerce.interactionapi.dto.warehouse.*;
import ru.yandex.practicum.commerce.interactionapi.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interactionapi.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.interactionapi.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];
    private WarehouseRepository warehouseRepository;
    private OrderBookingRepository orderBookingRepository;
    private WarehouseMapper mapper;

    @Override
    public WarehouseDto newProductInWarehouse(NewProductInWarehouseRequest request) {
        WarehouseProduct product = mapper.toProduct(request);
        product = warehouseRepository.save(product);
        return mapper.toWarehouseDto(product);

    }

    @Override
    public WarehouseDto addProductToWarehouse(AddProductToWarehouseRequest request) {
        Optional<WarehouseProduct> product = warehouseRepository.findById(request.getProductId());
        if (product.isEmpty()) {
            throw new NoSpecifiedProductInWarehouseException(request.getProductId());
        }
        product.get().setQuantity(request.getQuantity());
        WarehouseProduct updProduct = warehouseRepository.save(product.get());
        return mapper.toWarehouseDto(updProduct);
    }

    @Override
    public AddressDto getWarehouseAddress() {

        return mapper.toAddressDto(CURRENT_ADDRESS);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto request) {
        Double weight = 0D;
        Double volume = 0D;
        boolean fragile = false;
        log.info("Проверка достаточного количества товаров для корзины {}", request.getShoppingCartId());
        for (UUID productId : request.getProducts().keySet()) {
            Optional<WarehouseProduct> product = warehouseRepository.findById(productId);
            if (product.isEmpty()) {
                throw new NoSpecifiedProductInWarehouseException(productId);
            } else {
                Long cartQuantity = request.getProducts().get(productId);
                Long warehouseQuantity = product.get().getQuantity();
                if (warehouseQuantity < cartQuantity) {
                    throw new ProductInShoppingCartLowQuantityInWarehouse(productId);
                } else {
                    weight += product.get().getWeight() * cartQuantity;
                    volume += product.get().getDepth() * product.get().getWidth() * product.get().getHeight() * cartQuantity;
                    fragile = fragile || product.get().isFragile();
                }
            }

        }
        return new BookedProductsDto(weight, volume, fragile);

    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        Optional<OrderBooking> orderBooking = orderBookingRepository.findById(request.getOrderId());
        if (orderBooking.isEmpty()) {
            throw new NoOrderFoundException(request.getOrderId());
        }
        orderBooking.get().setDeliveryId(request.getDeliveryId());
        OrderBooking updOrderBooking = orderBookingRepository.save(orderBooking.get());
    }

    @Override
    public void acceptReturn(Map<UUID, Long> products) {
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            Optional<WarehouseProduct> product = warehouseRepository.findById(entry.getKey());
            if (product.isEmpty()) {
                throw new NoSpecifiedProductInWarehouseException(entry.getKey());
            } else {
                Long newQuantity = product.get().getQuantity() + entry.getValue();
                product.get().setQuantity(newQuantity);
                WarehouseProduct newProduct = warehouseRepository.save(product.get());
            }
        }
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Double weight = 0D;
        Double volume = 0D;
        boolean fragile = false;
        for (UUID productId : request.getProducts().keySet()) {
            Optional<WarehouseProduct> product = warehouseRepository.findById(productId);
            if (product.isEmpty()) {
                throw new NoSpecifiedProductInWarehouseException(productId);
            } else {
                Long requestQuantity = request.getProducts().get(productId);
                Long warehouseQuantity = product.get().getQuantity();
                if (warehouseQuantity < requestQuantity) {
                    throw new ProductInShoppingCartLowQuantityInWarehouse(productId);
                } else {
                    Long resultQuantity = warehouseQuantity - requestQuantity;
                    product.get().setQuantity(resultQuantity);
                    WarehouseProduct updProduct = warehouseRepository.save(product.get());
                    weight += product.get().getWeight() * requestQuantity;
                    volume += product.get().getDepth() * product.get().getWidth() * product.get().getHeight() * requestQuantity;
                    fragile = fragile || product.get().isFragile();
                }
            }
        }
        OrderBooking orderBooking = orderBookingRepository.save(mapper.toOrderBooking(request));
        return new BookedProductsDto(weight, volume, fragile);
    }
}
