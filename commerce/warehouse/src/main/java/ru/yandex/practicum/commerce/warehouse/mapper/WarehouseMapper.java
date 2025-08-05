package ru.yandex.practicum.commerce.warehouse.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.DimensionDto;
import ru.yandex.practicum.commerce.interactionapi.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.interactionapi.dto.WarehouseDto;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WarehouseMapper {
    public WarehouseProduct toProduct(NewProductInWarehouseRequest request) {
        WarehouseProduct product = new WarehouseProduct();
        product.setId(request.getProductId());
        product.setFragile(request.isFragile());
        product.setWeight(request.getWeight());
        product.setWidth(request.getDimension().getWidth());
        product.setHeight(request.getDimension().getHeight());
        product.setDepth(request.getDimension().getDepth());
        return product;
    }

    public WarehouseDto toWarehouseDto(WarehouseProduct product) {
        DimensionDto dimension = new DimensionDto();
        dimension.setDepth(product.getDepth());
        dimension.setHeight(product.getHeight());
        dimension.setWidth(product.getWidth());

        return new WarehouseDto(
                product.getId(),
                product.getWeight(),
                dimension,
                product.isFragile()
        );
    }

    public AddressDto toAddressDto (String address) {
        return new AddressDto(
                address,
                address,
                address,
                address,
                address
        );
    }

}
