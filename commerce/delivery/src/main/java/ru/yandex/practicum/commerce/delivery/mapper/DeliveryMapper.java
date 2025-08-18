package ru.yandex.practicum.commerce.delivery.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.model.DeliveryAddress;
import ru.yandex.practicum.commerce.interactionapi.dto.AddressDto;
import ru.yandex.practicum.commerce.interactionapi.dto.delivery.DeliveryDto;

@Component
@RequiredArgsConstructor
public class DeliveryMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public Delivery toDelivery(DeliveryDto deliveryDto) {

        Delivery delivery = new Delivery();
        delivery.setDeliveryId(deliveryDto.getDeliveryId());
        delivery.setDeliveryState(deliveryDto.getDeliveryState());
        delivery.setOrderId(deliveryDto.getOrderId());
        delivery.setDeliveryFromAddress(modelMapper.map(deliveryDto.getFromAddress(), DeliveryAddress.class));
        delivery.setDeliveryToAddress(modelMapper.map(deliveryDto.getToAddress(), DeliveryAddress.class));

        return delivery;
    }

    public DeliveryDto toDeliveryDto(Delivery delivery) {
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDeliveryId(delivery.getDeliveryId());
        deliveryDto.setDeliveryState(delivery.getDeliveryState());
        deliveryDto.setFromAddress(modelMapper.map(delivery.getDeliveryFromAddress(), AddressDto.class));
        deliveryDto.setToAddress(modelMapper.map(delivery.getDeliveryToAddress(), AddressDto.class));

        return deliveryDto;
    }
}
