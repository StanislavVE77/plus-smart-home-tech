package ru.yandex.practicum.commerce.warehouse.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interactionapi.dto.*;
import ru.yandex.practicum.commerce.interactionapi.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.interactionapi.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseRepository;

import java.util.Optional;
import java.util.Random;
import java.security.*;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];
    private WarehouseRepository warehouseRepository;
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
}
